package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by tuyuelai on 2017/11/1.
 */
public class CuratorZookeeperClient {
    private final int CONNECT_TIMEOUT = 15000;
    private final int RETRY_TIME = Integer.MAX_VALUE;
    private final int RETRY_INTERVAL = 1000;
    private static final Logger logger = LoggerFactory.getLogger(CuratorZookeeperClient.class);
    private CuratorFramework curator;

    private volatile static CuratorZookeeperClient instance;

    private static ConcurrentHashMap<String, Map<String, String>> zkCacheMap = new ConcurrentHashMap<String, Map<String, String>>();

    public static Map<String, Map<String, String>> getZkCacheMap() {
        return zkCacheMap;
    }

    public CuratorFramework getCurator(){
        return curator;
    }

    private CuratorFramework newCurator(String zkServers) {
        return CuratorFrameworkFactory.builder().connectString(zkServers)
                .retryPolicy(new RetryNTimes(RETRY_TIME, RETRY_INTERVAL))
                .connectionTimeoutMs(CONNECT_TIMEOUT).build();
    }

    private CuratorZookeeperClient(String zkServers) {
        if (curator == null) {
            curator = newCurator(zkServers);
            curator.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                public void stateChanged(CuratorFramework client, ConnectionState state) {
                    if (state == ConnectionState.LOST) {
                        //连接丢失
                        logger.info("lost session with zookeeper");
                    } else if (state == ConnectionState.CONNECTED) {
                        //连接新建
                        logger.info("connected with zookeeper");
                    } else if (state == ConnectionState.RECONNECTED) {
                        logger.info("reconnected with zookeeper");
                        //连接重连
                        /*for(ZkStateListener s:stateListeners){
                            s.reconnected();
                        }*/
                    }
                }
            });
            curator.start();
        }
    }

    public static CuratorZookeeperClient getInstance(String zkServers) {
        if (instance == null) {
            synchronized (CuratorZookeeperClient.class) {
                if (instance == null) {
                    logger.info("initial CuratorZookeeperClient instance");
                    instance = new CuratorZookeeperClient(zkServers);
                }
            }
        }
        return instance;
    }
}
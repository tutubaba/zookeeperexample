package com.example.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;

import java.util.concurrent.Executor;

/**
 * Created by tuyuelai on 2017/11/2.
 */
public class WatchExample {
    static Object lock = new Object();
    public static void main(String[] args) throws Exception{
        String hoststr = "192.168.32.151:2181,192.168.32.xx151:2182,192.168.32.151:2183";
        CuratorFramework curatorFramework = CuratorZookeeperClient.getInstance(hoststr).getCurator();
        String path = "/test3";
        testWatchNode(curatorFramework,path);
        curatorFramework.setData().forPath("/test3","i love  2".getBytes("utf-8"));

        //无限期等待
        while(true){
            synchronized(lock){
                System.out.println( "2.无限期等待中..." );
                lock.wait(); //等待，直到其它线程调用 lock.notify()
            }
        }

    }



    /**
     * 监听节点变化
     * @param curatorFramework
     * @param path
     * @throws Exception
     */
    public static void testWatchNode(CuratorFramework curatorFramework,String path) throws Exception{
        NodeCache nodeCache = new NodeCache(curatorFramework,path,false);
        nodeCache.start();
        //监听类型：节点是否存在，节点数据内容改变，不监听节点删除。
        nodeCache.getListenable().addListener(new NodeCacheListener(){
            @Override
            public void nodeChanged() throws Exception {
                // TODO Auto-generated method stub
                if(nodeCache.getCurrentData()!=null)
                    System.out.println("path:"+nodeCache.getCurrentData().getPath()+",data:"+new String(nodeCache.getCurrentData().getData()));
            }});
        System.out.println("-----------------");
        //curatorFramework.close();
    }

    /**
     * 监听子节点变化
     * @param curatorFramework
     * @param path
     * @param pool    处理监听
     * @throws Exception
     */
    public static void testWatcChildhNode(CuratorFramework curatorFramework, String path, Executor pool) throws Exception{
        final PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                System.out.println("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }
                },
                pool
        );

    }
}

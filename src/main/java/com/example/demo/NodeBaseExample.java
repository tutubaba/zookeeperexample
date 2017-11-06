package com.example.demo;

import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** curator 客户端操作zookeeper文件系统 测试
 *
 */
public class NodeBaseExample {

    CuratorFramework curatorFramework = null;
    @Before
    public void before(){
        String hoststr = "192.168.32.151:2181,192.168.32.151:2182,192.168.32.151:2183";
        curatorFramework = CuratorZookeeperClient.getInstance(hoststr).getCurator();
    }

    /**
     * 创建一个节点
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception{
        curatorFramework.create().forPath("/test3");
    }

    /**
     * 彺节点写入数据
     * @throws Exception
     */
    @Test
    public void testWrite() throws Exception{
        curatorFramework.setData().forPath("/test3","i love you".getBytes("utf-8"));
    }

    /**
     * 彺节点写入数据
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception{
        String data = new String(curatorFramework.getData().forPath("/test3"),"utf-8");
        System.out.println(data);
    }

    @After
    public void after(){
        if (curatorFramework != null){
            curatorFramework.close();
        }
    }
}

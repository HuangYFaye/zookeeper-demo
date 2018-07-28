package com.one;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Created by huangyifei on 2018/7/28.
 */
public class CuratorDamo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString("35.185.164.128:2181").
                sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,3)).
                namespace("Curator-test").build();

        curatorFramework.start();

        //创建多层节点
        curatorFramework.create().creatingParentContainersIfNeeded().
                withMode(CreateMode.PERSISTENT).forPath("/one/node1","1".getBytes());

        //删除节点
//        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/one/node1");

        Stat stat = new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath("/one/node1");
        curatorFramework.setData().withVersion(stat.getVersion()).forPath("/one/node1","test".getBytes());

        curatorFramework.close();
    }
}

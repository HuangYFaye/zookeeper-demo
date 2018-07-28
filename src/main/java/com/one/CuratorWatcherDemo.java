package com.one;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by huangyifei on 2018/7/29.
 */
public class CuratorWatcherDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString("35.185.164.128:2181").
                sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,3)).
                namespace("CuratorWatcher-test").build();

        curatorFramework.start();

        addListenerWithNodeCache(curatorFramework,"/one");
        addListenerWithPathChildCache(curatorFramework,"/one");
        addListenerWithTreeCache(curatorFramework,"/one");

        System.in.read();
    }


    /**
     * NodeCache 监听一个节点的创建、更新
     * PathChildCache 监听一个节点下子节点的创建，更新，删除
     * TreeCache 监听一个节点下所有节点的创建，更新，删除，功能相当于NodeCache、PathChildCache功能总和
     *
     */
    public static void addListenerWithNodeCache(CuratorFramework curatorFramework,String path) throws Exception {
        final NodeCache nodeCache = new NodeCache(curatorFramework,path,false);
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Receive NodeEvent: " + nodeCache.getCurrentData().getPath());
            }
        };

        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    public static void addListenerWithPathChildCache(CuratorFramework curatorFramework,String path) throws Exception {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework,
                                   PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("Receive PathChildEvent : " + pathChildrenCacheEvent.getType());
            }
        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public static void addListenerWithTreeCache(CuratorFramework curatorFramework,String path) throws Exception {
        TreeCache treeCache = new TreeCache(curatorFramework, path);
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("Receive TreeEvent: " + treeCacheEvent.getType());
            }
        };

        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }

}

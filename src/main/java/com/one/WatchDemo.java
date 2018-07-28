package com.one;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by huangyifei on 2018/7/27.
 */
public class WatchDemo {
    public static void main(String[] args) {

        try {
            final CountDownLatch cnt = new CountDownLatch(1);
            final ZooKeeper zooKeeper = new ZooKeeper("35.185.164.128:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("默认事件： "+ watchedEvent.getType());
                    if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                        cnt.countDown();
                    }
                }
            });
            cnt.await();
            Stat stat = new Stat();
            zooKeeper.create("/zookeeper-test","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            System.out.println(stat.getVersion());
            stat = zooKeeper.exists("/zookeeper-test", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    try {
                        zooKeeper.exists("/zookeeper-test",true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(watchedEvent.getType()+"->"+watchedEvent.getPath());
                }
            });
            System.out.println(stat.getVersion());
            stat = zooKeeper.setData("/zookeeper-test","1".getBytes(),stat.getVersion());
            System.out.println(stat.getVersion());
            Thread.sleep(1000);
            System.out.println(stat.getVersion());
            zooKeeper.delete("/zookeeper-test",stat.getVersion());
            System.out.println(stat.getVersion());

            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}

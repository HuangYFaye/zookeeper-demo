package com.one;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by huangyifei on 2018/7/27.
 */
public class TestConnection {
    public static void main(String[] args) {
        try {
            final CountDownLatch cnt = new CountDownLatch(1);
            final ZooKeeper zooKeeper = new ZooKeeper("35.185.164.128:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                        cnt.countDown();
                    }
                }
            });
            cnt.await();
            System.out.println(zooKeeper.getState());

            zooKeeper.create("/zookeeper-test","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            Thread.sleep(1000);
            Stat stat = new Stat();

            byte[] bytes = zooKeeper.getData("/zookeeper-test",null,stat);
            System.out.println(new String(bytes));

            zooKeeper.setData("/zookeeper-test","1".getBytes(),stat.getVersion());

            byte[] bytes1 = zooKeeper.getData("/zookeeper-test",null,stat);
            System.out.println(new String(bytes1));

            zooKeeper.delete("/zookeeper-test",stat.getVersion());
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

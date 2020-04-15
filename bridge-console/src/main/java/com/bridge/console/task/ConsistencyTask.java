package com.bridge.console.task;

import com.bridge.console.service.zk.ZookeeperComponent;
import com.bridge.domain.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @version v1.0
 * @description 定时器，用于同步db和zk的数据
 * @date 2019-02-26 09:47
 */
@Component
public class ConsistencyTask {

    @Autowired
    private ZookeeperComponent zookeeperComponent;


    /**
     * 每隔30分钟同步一次，通过zk选举出master去执行任务
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void doJob() {
        CuratorFramework curatorFramework = zookeeperComponent.zookeeperClient().getCuratorFramework();
        // 先进行选举，选出的master执行job
        LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, Constants.JOB_MASTER, new LeaderSelectorListener() {

            @Override
            public void takeLeadership(CuratorFramework client) {
                zookeeperComponent.consistencyDbAndZkByEnv();
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
                    throw new CancelLeadershipException("zk连接异常，正在中断并且取消正在执行takeLeadership");
                }
            }
        });
        leaderSelector.start();
    }
}

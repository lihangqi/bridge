package com.bridge.zookeeper.watcher;

import com.alibaba.fastjson.JSON;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.log.LogHandler;
import com.bridge.utils.NodePathUtils;
import com.bridge.zookeeper.BridgeNodeManager;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description 对app的子节点configKey的watcher
 * @date 2019-02-15 17:17
 */
@Slf4j
public class ConfigKeyNodeDataChangedWatcher implements PathChildrenCacheListener {

    private BridgeNodeManager bridgeNodeManager;

    public ConfigKeyNodeDataChangedWatcher(BridgeNodeManager bridgeNodeManager) {
        this.bridgeNodeManager = bridgeNodeManager;
    }

    /**
     * key节点发生改变后的监听事件
     *
     * @param client
     * @param event
     */
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
        switch (event.getType()) {
            // 新增节点 || 子节点的数据发生了改变
            case CHILD_ADDED:
            case CHILD_UPDATED: {
                // key的节点路径
                String configKeyNodePath = event.getData().getPath();
                // 这里是对订阅节点的排除
                if (configKeyNodePath.contains(bridgeNodeManager.getConsumerHostNodeName())) {
                    break;
                }
                // 获取数据
                String data = new String(event.getData().getData());
                if (StringUtils.isEmpty(data)) {
                    LogHandler.error(String.format("zk节点读取内容失败,失败的节点为 -> 「%s」", configKeyNodePath));
                    throw new BridgeProcessFailException(BridgeErrorEnum.ZK_NODE_READ_ERROR);
                }
                ConfigKeyNodeData configKeyNodeData = JSON.parseObject(data, ConfigKeyNodeData.class);
                bridgeNodeManager.doWhenConfigKeyNodeDataChanged(configKeyNodePath, configKeyNodeData);
                break;
            }

            // 节点被删除
            case CHILD_REMOVED: {
                // key的节点路径
                String configKeyNodePath = event.getData().getPath();
                // 获取数据
                String data = new String(event.getData().getData());
                LogHandler.info(String.format("删除的zk节点为：「%s」, 配置项数据为：「%s」", configKeyNodePath, data));
                break;
            }
            default:
                break;
        }
    }
}

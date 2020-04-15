package com.bridge.zookeeper;

import com.alibaba.fastjson.JSON;
import com.bridge.domain.BridgeConfig;
import com.bridge.enums.*;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.processor.init.cache.CacheManager;
import com.bridge.utils.NodePathUtils;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import com.bridge.zookeeper.data.MachineNodeData;
import com.bridge.zookeeper.event.NodeDataChangeEvent;
import com.bridge.zookeeper.watcher.ConfigKeyNodeDataChangedWatcher;
import com.bridge.processor.init.cache.container.BeanDefinitionListenerContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bridge.log.LogHandler.error;
import static com.bridge.log.LogHandler.info;


/**
 * @author Jay
 * @version v1.0
 * @description 基于zk操作节点
 * @date 2019-01-02 16:33
 */
@Slf4j
public class BridgeNodeManager implements NodeDataChangeEvent {

    /**
     * 系统编码
     */
    private String appCode;

    /**
     * 环境区分
     */
    private EnvEnum envEnum;

    /**
     * zk客户端工具
     */
    private ZookeeperClient zookeeperClient;


    private BridgeNodeManager() {

    }

    private static class InstancesHolder {
        private static BridgeNodeManager bridgeNodeManager = new BridgeNodeManager();
    }

    public static BridgeNodeManager getInstance() {
        return InstancesHolder.bridgeNodeManager;
    }

    /**
     * 初始化节点
     *
     * @param bridgeConfig
     */
    public void init(BridgeConfig bridgeConfig) {
        this.appCode = bridgeConfig.getAppCode();
        this.envEnum = bridgeConfig.getEnvEnum();
        this.zookeeperClient = ZookeeperClient.getInstance();
    }

    /**
     * 监听到key节点发生变化后的处理
     *
     * @param configKeyNodeData
     * @param configKeyNodePath
     */
    @Override
    public void doWhenConfigKeyNodeDataChanged(String configKeyNodePath, ConfigKeyNodeData configKeyNodeData) {
        try {
            // 获取machine节点路径
            String machineNodePath = NodePathUtils.getMachineNodePath(configKeyNodePath, NodePathUtils.getIp());
            // 系统是否在使用了这个key
            boolean isConfigKeyInContainer = isConfigKeyInContainer(configKeyNodeData.getKey());
            // 判断是否使用了这个key
            if (isConfigKeyInContainer) {
                // 不存在machine节点
                if (!zookeeperClient.checkNodeIsExist(machineNodePath)) {
                    log.info("Configuration item property change detected ...");
                    info("检测到配置项发生变更 ...");
                    // 刷新缓存
                    CacheManager.getInstance().onCacheRefresh(configKeyNodeData);
                    this.createMachineNode(machineNodePath, NeedUpdateEnum.NOT_NEED_UPDATE.getKey(), configKeyNodeData);
                    log.info("Instance property value loading completed ...");
                    info("实例属性值加载完成 ...");
                } else {
                    // machine节点存在则获取machine节点数据
                    MachineNodeData machineNodeData = this.getMachineNodeData(machineNodePath);
                    Integer needUpdate = machineNodeData.getNeedUpdate();
                    // 标识需要更新,那么就刷新缓存
                    if (needUpdate.equals(NeedUpdateEnum.NEED_UPDATE.getKey())) {
                        log.info("Configuration item property change detected ...");
                        info("检测到配置项发生变更 ...");
                        // 刷新缓存
                        CacheManager.getInstance().onCacheRefresh(configKeyNodeData);
                        // key在本系统中还在使用,则重置状态为不需要更新; 否则将状态置为已废弃
                        machineNodeData.setNeedUpdate(NeedUpdateEnum.NOT_NEED_UPDATE.getKey());
                        machineNodeData.setVersion(configKeyNodeData.getVersion());
                        zookeeperClient.updateNodeData(machineNodePath, JSON.toJSONString(machineNodeData));
                        log.info("Instance property value loading completed ...");
                        info("实例属性值加载完成 ...");
                    }
                }
            }
///            else {
//                log.info("[Bridge]>>>>>>>>>>>>> Key = {} is not used", configKeyNodeData.getKey());
//                LogHandler.info(String.format("未使用 key = %s 的配置项", configKeyNodeData.getKey()));
//            }
        } catch (Exception e) {
            log.error("在监听到zk的node发生变更，处理数据时发生了一个异常，异常信息为:", e);
            error(String.format("在监听到zk的节点发生变更，处理数据时发生了一个异常，异常信息为: %s", e.getMessage()));
            throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
        }
    }


    /**
     * 更新machine节点数据
     *
     * @param key
     * @param configKeyNodeData
     * @param updateStatus
     * @param envEnum
     */
    public void registerMachineNode(String key, ConfigKeyNodeData configKeyNodeData,
                                    Integer updateStatus, EnvEnum envEnum) {
        if (configKeyNodeData == null) {
            log.error("[Bridge]>>>>>>>>>>>>> key = {} 未在本地缓存池中找到对应的值，请先前往控制台配置", key);
            error(String.format("未在本地缓存池中找到 key 为 %s 对应的值，请先前往控制台配置！", key));
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode()
                    , String.format("key = {%s} 未在本地缓存池中找到对应的值，请先前往控制台配置", key));
        }
        // 获取当前machine节点路径
        String machineNodePath
                = NodePathUtils.getMachineNodePath(appCode, configKeyNodeData.getKey(), NodePathUtils.getIp(), envEnum);
        try {
            // 该节点存在则更新
            if (zookeeperClient.checkNodeIsExist(machineNodePath)) {
                // 获取节点数据
                MachineNodeData machineNodeData = this.getMachineNodeData(machineNodePath);
                // 版本号相同则不需要更新
                if (machineNodeData.getVersion().equals(configKeyNodeData.getVersion())
                        && machineNodeData.getNeedUpdate() == NeedUpdateEnum.NOT_NEED_UPDATE.getKey()) {
                    return;
                }
                machineNodeData.setNeedUpdate(updateStatus);
                machineNodeData.setVersion(configKeyNodeData.getVersion());
                machineNodeData.setMachineHost(NodePathUtils.getIp());
                zookeeperClient.updateNodeData(machineNodePath, JSON.toJSONString(machineNodeData));
            } else {
                // 创建一个machine节点
                this.createMachineNode(machineNodePath, updateStatus, configKeyNodeData);
            }
        } catch (Exception e) {
            log.error("在更新machine节点数据，并对machine节点添加监听时，出现异常，异常的节点为:{},e->{}", machineNodePath, e);
            error(String.format("在更新machine节点数据，并对machine节点添加监听时出现异常，异常的节点为:%s,e->%s",
                    machineNodePath, e.getMessage()));
            throw new BridgeProcessFailException(BridgeErrorEnum.ZK_NODE_UPDATE_ERROR);
        }
    }


    /**
     * 添加对app的子节点的监听
     *
     * @param appNodePath
     */
    public void addAppNodePathChildrenListener(String appNodePath) {
        zookeeperClient.addPathChildrenCacheListener(appNodePath, new ConfigKeyNodeDataChangedWatcher(this));
    }


    /**
     * 从zookeeper上获取配置文件信息
     *
     * @param envEnum
     * @param appCode
     * @return
     */
    public List<ConfigKeyNodeData> getFromZookeeper(String appCode, EnvEnum envEnum) {
        info("正在拉取配置项信息 ...");
        List<String> children = zookeeperClient.getChildren(NodePathUtils.getAppNodePath(appCode, envEnum));
        if (CollectionUtils.isEmpty(children)) {
            log.error("未拉取到对应的配置文件信息, appCode -> 「{}」,envEnum -> 「{}」", appCode, envEnum);
            error(String.format("未拉取到对应的配置项信息,appCode = %s", appCode));
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(), "未拉取到对应的配置文件信息");
        }
        List<ConfigKeyNodeData> configKeyNodeDataList = new ArrayList<>();
        children.forEach(configKey -> {
            // 获取key的节点数据
            String configKeyNodePath = NodePathUtils.getConfigKeyNodePath(appCode, configKey, envEnum);
            String nodeData = zookeeperClient.getNodeData(configKeyNodePath);
            ConfigKeyNodeData configKeyNodeData = null;
            try {
                configKeyNodeData = JSON.parseObject(nodeData, ConfigKeyNodeData.class);
            } catch (Exception e) {
                error(String.format("配置项数据解析异常，异常的配置项为:%s, 节点数据 = %s, e -> %s",
                        configKey, nodeData, e.getMessage()));
                log.error("配置项数据解析异常，异常的配置项为:「{}」,节点数据 ->「{}」,e -> 「{}」", configKey, nodeData, e);
            }
            if (configKeyNodeData != null){
                configKeyNodeDataList.add(configKeyNodeData);
            }

        });
        return configKeyNodeDataList;
    }

    /**
     * 订阅服务
     */
    public void registerConsumerHost() {
        String consumerHostNodePath = NodePathUtils.getConsumerHostPath(appCode, envEnum, NodePathUtils.getIp());
        if (!zookeeperClient.checkNodeIsExist(consumerHostNodePath)) {
            zookeeperClient.createNode(consumerHostNodePath, "", CreateMode.EPHEMERAL);
        }
    }


    /**
     * 获取订阅节点的名称
     *
     * @return
     */
    public String getConsumerHostNodeName() {
        return NodePathUtils.getConsumerHostKey(appCode);
    }


    //-----------------------------------------private method-------------------------------------------------------------------


    /**
     * key是否在容器内
     *
     * @param configKey
     * @return
     */
    private static boolean isConfigKeyInContainer(String configKey) {
        return BeanDefinitionListenerContainer.isConfigKeyInContainer(configKey);
    }

    /**
     * 创建一个机器节点
     *
     * @param machineNodePath
     */
    private void createMachineNode(String machineNodePath, Integer needUpdateStatus, ConfigKeyNodeData configKeyNodeData) {
        MachineNodeData machineNodeData = new MachineNodeData();
        machineNodeData.setMachineHost(NodePathUtils.getIp());
        machineNodeData.setNeedUpdate(needUpdateStatus);
        machineNodeData.setVersion(configKeyNodeData.getVersion());
        zookeeperClient.createNode(machineNodePath, JSON.toJSONString(machineNodeData), CreateMode.EPHEMERAL);
    }


    /**
     * 根据machine节点路径获取该节点数据
     *
     * @param machineNodePath
     * @return
     */
    private MachineNodeData getMachineNodeData(String machineNodePath) {
        // 获取machine节点数据
        String machineNodeDataJson = zookeeperClient.getNodeData(machineNodePath);
        if (StringUtils.isEmpty(machineNodeDataJson)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ZK_NULL_ERROR);
        }
        MachineNodeData machineNodeData = JSON.parseObject(machineNodeDataJson, MachineNodeData.class);
        if (machineNodeData == null || StringUtils.isEmpty(machineNodeData.getMachineHost())
                || StringUtils.isEmpty(machineNodeData.getNeedUpdate())) {
            error(String.format("根据machine节点路径获取该节点数据时，部分参数值缺失，节点为 -> 「%s」", machineNodePath));
            log.error("根据machine节点路径获取该节点数据时，部分参数值缺失，节点为 -> 「{}」", machineNodePath);
            throw new BridgeProcessFailException(BridgeErrorEnum.ZK_NULL_ERROR.getCode(), "zk返回数据异常");
        }
        return machineNodeData;
    }

}

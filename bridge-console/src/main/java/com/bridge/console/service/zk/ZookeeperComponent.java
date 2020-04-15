package com.bridge.console.service.zk;

import com.alibaba.fastjson.JSON;
import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.ConfigMapper;
import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.entity.ConfigDO;
import com.bridge.console.model.enums.KeyStatusEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.EnabledStateEnum;
import com.bridge.console.model.vo.AppListQuery;
import com.bridge.console.model.vo.ConfigListQuery;
import com.bridge.enums.EnvEnum;
import com.bridge.enums.NeedUpdateEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.zookeeper.data.AppNodeData;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import com.bridge.domain.Constants;
import com.bridge.utils.NodePathUtils;
import com.bridge.zookeeper.ZookeeperClient;
import com.bridge.zookeeper.data.MachineNodeData;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description zk相关的操作
 * @date 2019-02-01 15:52
 */
@Slf4j
public class ZookeeperComponent {

    @Value("${zk.address}")
    private String zkAddress;

    private ZookeeperClient zookeeperClient;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private AppDefMapper appDefMapper;


    /**
     * 初始化
     */
    public void init() {
        zookeeperClient = ZookeeperClient.getInstance();
        zookeeperClient.init(zkAddress);
        zookeeperClient.startConnection();
        // 创建root节点
        createRootNodeIfNotExist();
        // 同步所有环境的zk和db的节点
        consistencyDbAndZkByEnv();
    }


    /**
     * 创建key节点
     *
     * @param path
     * @param configKeyNodeData
     * @return
     */
    public void createConfigKeyNode(String path, ConfigKeyNodeData configKeyNodeData) {
        zookeeperClient.createNode(path, JSON.toJSONString(configKeyNodeData), CreateMode.PERSISTENT);
    }


    /**
     * 创建app节点
     *
     * @param path
     * @param appNodeData
     */
    public void createAppNode(String path, AppNodeData appNodeData) {
        zookeeperClient.createNode(path, JSON.toJSONString(appNodeData), CreateMode.PERSISTENT);
    }

    /**
     * 获取machine节点数据
     *
     * @param machineNodePath
     * @return
     */
    public MachineNodeData getMachineNodeData(String machineNodePath) {
        String machineNode = zookeeperClient.getNodeData(machineNodePath);
        return JSON.parseObject(machineNode, MachineNodeData.class);
    }


    /**
     * 获取子节点路径
     *
     * @param nodePath
     * @return
     */
    public List<String> getChildrenPath(String nodePath) {
        return zookeeperClient.getChildren(nodePath);
    }

    /**
     * 更新machine节点数据
     *
     * @param path
     * @param machineNodeData
     */
    public void updateMachineNodeData(String path, MachineNodeData machineNodeData) {
        zookeeperClient.updateNodeData(path, JSON.toJSONString(machineNodeData));
    }

    /**
     * 根据configKeyPath获取子节点数据
     *
     * @param configKeyNodePath
     * @return
     */
    public List<MachineNodeData> getMachineNodeDataList(String configKeyNodePath) {
        List<String> children = getChildren(configKeyNodePath);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        List<MachineNodeData> machineNodeDataList = new ArrayList<>();
        children.forEach(machinePath -> {
            String machineNodePath = NodePathUtils.getMachineNodePath(configKeyNodePath, machinePath);
            MachineNodeData machineNodeData = getMachineNodeData(machineNodePath);
            if (machineNodeData != null && machineNodeData.getNeedUpdate() != null
                    && !StringUtils.isEmpty(machineNodeData.getMachineHost())) {
                machineNodeDataList.add(machineNodeData);
            }
        });
        return machineNodeDataList;
    }


    /**
     * 根据appNodePath获取子节点数据
     *
     * @param appNodePath
     * @return
     */
    public List<ConfigKeyNodeData> getConfigKeyNodeDataList(String appNodePath) {
        List<String> children = getChildren(appNodePath);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        List<ConfigKeyNodeData> configKeyNodeDataList = new ArrayList<>();
        children.forEach(child -> {
            String configKeyNodePath = NodePathUtils.getConfigKeyNodePathByAppNodePathAndConfigKey(appNodePath, child);
            ConfigKeyNodeData configKeyNodeData = getConfigKeyNodeData(configKeyNodePath);
            if (configKeyNodeData != null && configKeyNodeData.getVersion() != null
                    && !StringUtils.isEmpty(configKeyNodeData.getKey())) {
                configKeyNodeDataList.add(configKeyNodeData);
            }
        });
        return configKeyNodeDataList;
    }


    /**
     * key节点如果不存在则创建，如果存在则将key节点更新到最新值
     *
     * @param configDO
     */
    public String createConfigKeyNodeIfNotExistOrUpdate(ConfigDO configDO) {
        // 判断key节点是否存在，不存在则创建
        String appCode = appDefMapper.selectAppCodeById(configDO.getAppId());
        Integer envId = configDO.getEnvId();
        String configKeyNodePath = NodePathUtils.getConfigKeyNodePath(appCode, configDO.getConfigKey(), EnvEnum.getEnvEnum(envId));
        ConfigKeyNodeData configKeyNodeData = this.fillConfigKeyNodeData(configDO);
        // 不存在 && 传入类型为创建 则创建key
        if (!checkNodeIsExist(configKeyNodePath)) {
            createConfigKeyNode(configKeyNodePath, configKeyNodeData);
        }
        // 存在 && 传入类型为更新 则更新key
        if (checkNodeIsExist(configKeyNodePath)) {
            updateConfigKeyNodeData(configKeyNodePath, configKeyNodeData);
        }
        return configKeyNodePath;
    }


    /**
     * 更新key节点数据
     *
     * @param path
     * @param configKeyNodeData
     */
    public void updateConfigKeyNodeData(String path, ConfigKeyNodeData configKeyNodeData) {
        zookeeperClient.updateNodeData(path, JSON.toJSONString(configKeyNodeData));
    }


    /**
     * 删除节点和它的子节点
     *
     * @param nodePath
     */
    public void deletingChildrenIfNeeded(String nodePath) {
        if (checkNodeIsExist(nodePath)) {
            zookeeperClient.deletingChildrenIfNeeded(nodePath);
        }
    }


    /**
     * 资源释放
     */
    public void destroy() {
        zookeeperClient.closeZooKeeperConnection();
    }

    /**
     * 判断节点是否存在
     *
     * @param nodePath
     * @return
     */
    public boolean checkNodeIsExist(String nodePath) {
        return zookeeperClient.checkNodeIsExist(nodePath);
    }


    /**
     * 返回zk实例
     *
     * @return
     */
    public ZookeeperClient zookeeperClient() {
        return zookeeperClient;
    }


    /**
     * 同步所有环境的zk和db的节点
     */
    public void consistencyDbAndZkByEnv() {
        for (EnvEnum envEnum : EnvEnum.values()) {
            consistencyDbAndZk(envEnum.getEnvId(), null);
        }
    }


    /**
     * 创建dev、test、stable、online环境的节点
     *
     * @param appDefDO
     */
    public void createAppNodeWithEnv(AppDefDO appDefDO) {
        for (EnvEnum envEnum : EnvEnum.values()) {
            createAppNode(appDefDO, envEnum);
        }
    }

    /**
     * 同步zk和db的节点
     *
     * @param envId
     */
    public void consistencyDbAndZk(Integer envId, Integer appId) {
        // 查询所有的需要创建的应用节点,如果不存在则创建
        AppListQuery appListQuery = new AppListQuery();
        if (appId != null) {
            appListQuery.setAppId(appId);
        }
        List<AppDefDO> appDefDoList = appDefMapper.queryAppList(appListQuery);
        if (CollectionUtils.isEmpty(appDefDoList)) {
            return;
        }
        // 获取环境
        EnvEnum envEnum = EnvEnum.getEnvEnum(envId);
        appDefDoList.forEach(appDefDO -> {
            if (appDefDO == null || StringUtils.isEmpty(appDefDO.getAppCode())) {
                throw new BridgeProcessFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统应用中存在异常的数据");
            }
            if (appDefDO.getEnabledState() == EnabledStateEnum.NOT_ENABLED.getKey()) {
                return;
            }
            // 创建app节点 如果不存在的话
            createAppNode(appDefDO, EnvEnum.getEnvEnum(envId));
            // 查询configKey的节点
            ConfigListQuery configListQuery = new ConfigListQuery();
            configListQuery.setAppId(appDefDO.getId());
            configListQuery.setEnvId(envId);
            configListQuery.setKeyStatus(KeyStatusEnum.ENABLED.getKey());
            List<ConfigDO> configDoList = configMapper.queryConfigKeyList(configListQuery);
            if (CollectionUtils.isEmpty(configDoList)) {
                return;
            }
            // 创建configKey的zk节点
            configDoList.forEach(configDO -> {
                if (configDO == null || StringUtils.isEmpty(configDO.getConfigValue())) {
                    log.error("[Bridge]>>>>>>>>>>>>> app == {},存在异常的数据 == {}", appDefDO, configDO);
                    return;
                }
                String configKeyPath = NodePathUtils.getConfigKeyNodePath(appDefDO.getAppCode(), configDO.getConfigKey(), envEnum);
                // 如果节点不存在则创建
                if (!zookeeperClient.checkNodeIsExist(configKeyPath)) {
                    createConfigKeyNode(configKeyPath, fillConfigKeyNodeData(configDO));
                    log.info("[Bridge]>>>>>>>>>>>>> create keyNode :[{}]", configKeyPath);
                    return;
                }
                // 存在的话校验一下db的数据和zk节点数据是否相等，不相等的话就做同步
                ConfigKeyNodeData configKeyNodeData = this.getConfigKeyNodeData(configKeyPath);
                if (configKeyNodeData == null) {
                    configKeyNodeData = this.fillConfigKeyNodeData(configDO);
                    this.updateConfigKeyNodeData(configKeyPath, configKeyNodeData);
                    return;
                }
                // 如果zk的数据与db的数据不相等，则更新
                if (this.checkConfigKeyNodeDataAndConfigDoIsEquals(configKeyNodeData, configDO)) {
                    configKeyNodeData.setVersion(configDO.getKeyVersion());
                    configKeyNodeData.setValue(configDO.getConfigValue());
                    configKeyNodeData.setKey(configDO.getConfigKey());
                    // 更新key的子节点machine节点数据
                    List<MachineNodeData> machineNodeDataList = getMachineNodeDataList(configKeyPath);
                    if (!CollectionUtils.isEmpty(machineNodeDataList)) {
                        machineNodeDataList.forEach(machineNodeData -> {
                            if (machineNodeData == null || StringUtils.isEmpty(machineNodeData.getMachineHost())) {
                                throw new BridgeProcessFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "节点数据异常");
                            }
                            machineNodeData.setNeedUpdate(NeedUpdateEnum.NEED_UPDATE.getKey());
                            String machineNodePath = NodePathUtils.getMachineNodePath(configKeyPath, machineNodeData.getMachineHost());
                            this.updateMachineNodeData(machineNodePath, machineNodeData);
                        });
                    }
                    // 更新key节点数据
                    this.updateConfigKeyNodeData(configKeyPath, configKeyNodeData);
                }
            });
        });
    }

    //--------------------------------------------------------private method-----------------------------------------------------


    /**
     * 创建app节点
     *
     * @param appDefDO
     * @param envEnum
     */
    private void createAppNode(AppDefDO appDefDO, EnvEnum envEnum) {
        String appNodePath = NodePathUtils.getAppNodePath(appDefDO.getAppCode(), envEnum);
        // 如果节点不存在则创建
        if (!zookeeperClient.checkNodeIsExist(appNodePath)) {
            // 创建应用节点
            createAppNode(appNodePath, fillAppNodeData(appDefDO));
            log.info("[Bridge]>>>>>>>>>>>>> create appNode :[{}]", appNodePath);
        }
    }


    /**
     * 创建root节点
     */
    private void createRootNodeIfNotExist() {
        // 不存在则创建一个持久节点
        if (!zookeeperClient.checkNodeIsExist(Constants.DEV_ROOT)) {
            // 创建一个dev节点
            zookeeperClient.createNode(Constants.DEV_ROOT, "bridge-dev", CreateMode.PERSISTENT);
            log.info("[Bridge]>>>>>>>>>>>>> create appNode :[{}]", Constants.DEV_ROOT);
        }
        if (!zookeeperClient.checkNodeIsExist(Constants.TEST_ROOT)) {
            // 创建一个test节点
            zookeeperClient.createNode(Constants.TEST_ROOT, "bridge-test", CreateMode.PERSISTENT);
            log.info("[Bridge]>>>>>>>>>>>>> create appNode :[{}]", Constants.TEST_ROOT);
        }
        if (!zookeeperClient.checkNodeIsExist(Constants.STABLE_ROOT)) {
            // 创建一个stable节点
            zookeeperClient.createNode(Constants.STABLE_ROOT, "bridge-stable", CreateMode.PERSISTENT);
            log.info("[Bridge]>>>>>>>>>>>>> create appNode :[{}]", Constants.STABLE_ROOT);
        }
        if (!zookeeperClient.checkNodeIsExist(Constants.ONLINE_ROOT)) {
            // 创建一个online节点
            zookeeperClient.createNode(Constants.ONLINE_ROOT, "bridge-online", CreateMode.PERSISTENT);
            log.info("[Bridge]>>>>>>>>>>>>> create appNode :[{}]", Constants.ONLINE_ROOT);
        }
    }


    /**
     * 校验是否相等
     *
     * @param configKeyNodeData
     * @param configDO
     * @return
     */
    private boolean checkConfigKeyNodeDataAndConfigDoIsEquals(ConfigKeyNodeData configKeyNodeData, ConfigDO configDO) {
        return !configKeyNodeData.getVersion().equals(configDO.getKeyVersion())
                || !configKeyNodeData.getKey().equals(configDO.getConfigKey())
                || !configKeyNodeData.getValue().equals(configDO.getConfigValue());
    }


    /**
     * 组装对象
     *
     * @param configDO
     * @return
     */
    @SuppressWarnings("Duplicates")
    private ConfigKeyNodeData fillConfigKeyNodeData(ConfigDO configDO) {
        ConfigKeyNodeData configKeyNodeData = new ConfigKeyNodeData();
        configKeyNodeData.setKey(configDO.getConfigKey());
        configKeyNodeData.setValue(configDO.getConfigValue());
        configKeyNodeData.setVersion(configDO.getKeyVersion());
        return configKeyNodeData;
    }


    /**
     * 组装对象
     *
     * @param appDefDO
     * @return
     */
    private AppNodeData fillAppNodeData(AppDefDO appDefDO) {
        AppNodeData appNodeData = new AppNodeData();
        appNodeData.setAppDes(appDefDO.getAppDes());
        appNodeData.setAppName(appDefDO.getAppName());
        return appNodeData;
    }


    /**
     * 根据key节点路径获取该节点数据
     *
     * @param configKeyNodePath
     * @return
     */
    private ConfigKeyNodeData getConfigKeyNodeData(String configKeyNodePath) {
        // 获取machine节点数据
        String configKeyNodeDataJson = zookeeperClient.getNodeData(configKeyNodePath);
        if (StringUtils.isEmpty(configKeyNodeDataJson)) {
            log.error("zk返回数据异常,异常的数据为:{},节点路径为:{}", configKeyNodeDataJson, configKeyNodePath);
            return null;
        }
        ConfigKeyNodeData configKeyNodeData = JSON.parseObject(configKeyNodeDataJson, ConfigKeyNodeData.class);
        if (configKeyNodeData == null || StringUtils.isEmpty(configKeyNodeData.getKey())
                || StringUtils.isEmpty(configKeyNodeData.getValue()) || StringUtils.isEmpty(configKeyNodeData.getVersion())) {
            log.error("zk返回数据异常,异常的数据为:{},节点路径为:{}", configKeyNodeData, configKeyNodePath);
            return null;
        }
        return configKeyNodeData;
    }


    /**
     * 获取子节点路径
     *
     * @param path
     * @return
     */
    private List<String> getChildren(String path) {
        if (!checkNodeIsExist(path)) {
            return null;
        }
        List<String> children = zookeeperClient.getChildren(path);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        return children;
    }

}

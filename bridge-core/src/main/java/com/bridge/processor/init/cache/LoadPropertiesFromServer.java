package com.bridge.processor.init.cache;

import com.alibaba.fastjson.JSON;
import com.bridge.domain.BridgeConfig;
import com.bridge.download.DownLoadService;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.EnvEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.listener.DefaultPropertiesChangeListenerImpl;
import com.bridge.listener.PropertiesChangeListener;
import com.bridge.listener.PropertiesChangeListenerHolder;
import com.bridge.log.LogHandler;
import com.bridge.processor.BridgePropertySource;
import com.bridge.processor.init.cache.container.LocalCacheHolder;
import com.bridge.schedule.AutoCheckNodeSchedule;
import com.bridge.utils.NodePathUtils;
import com.bridge.zookeeper.BridgeNodeManager;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.bridge.enums.NeedUpdateEnum.NEED_UPDATE;

/**
 * @author Jay
 * @version v1.0
 * @description 从服务器端拉取缓存
 * @date 2019-01-14 14:28
 */
@Slf4j
public class LoadPropertiesFromServer implements LoadPropertiesCache<BridgeConfig, ConfigKeyNodeData> {


    private PropertiesChangeListener propertiesChangeListener;

    private BridgeConfig bridgeConfig;

    public LoadPropertiesFromServer() {
        propertiesChangeListener = new DefaultPropertiesChangeListenerImpl();
    }

    /**
     * 初始化缓存配置文件信息
     *
     * @param bridgeConfig
     */
    @Override
    public void onCacheInit(BridgeConfig bridgeConfig) {
        this.bridgeConfig = bridgeConfig;
        doCacheInit(bridgeConfig.getAppCode(), bridgeConfig.getEnvEnum());
    }

    /**
     * 缓存刷新
     *
     * @param configKeyNodeData
     */
    @Override
    public void onCacheRefresh(ConfigKeyNodeData configKeyNodeData) {
        try {
            doCacheRefresh(configKeyNodeData);
        } catch (Exception e) {
            log.error("缓存刷新失败 ...,异常信息为:", e);
            LogHandler.error("缓存刷新失败, 异常信息为:".concat(e.getMessage()));
            // 这里出现异常的时候，需要触发一次重新注册，这样可以让控制台感知到本次下发是否是有效的
            String key = configKeyNodeData.getKey();
            ConfigKeyNodeData cacheData = LocalCacheHolder.get(key);
            if (cacheData != null) {
                EnvEnum envEnum = bridgeConfig.getEnvEnum();
                BridgeNodeManager.getInstance().registerMachineNode(key, cacheData, NEED_UPDATE.getKey(), envEnum);
                BridgeNodeManager.getInstance().registerConsumerHost();
            }
            throw new BridgeProcessFailException(BridgeErrorEnum.CACHE_REFRESH_ERROR);
        }
    }


    //--------------------------------------private method---------------------------------------


    /**
     * 缓存刷新
     *
     * @param configKeyNodeData
     */
    private void doCacheRefresh(ConfigKeyNodeData configKeyNodeData) {
        // 本地缓存池中存在一样的就不刷新缓存
        if (LocalCacheHolder.judgeIsExist(configKeyNodeData)) {
            return;
        }
        // 修改值之前的回调
        PropertiesChangeListenerHolder.doCallBack(LocalCacheHolder.get(configKeyNodeData.getKey()), true);
        // 刷新bean的属性值
        propertiesChangeListener.onPropertiesChanged(configKeyNodeData);
        // 存入到本地缓存池中
        LocalCacheHolder.put(configKeyNodeData);
        LogHandler.debug(String.format("本地缓存仓库数据更新成功，当前仓库内数据为: %s",
                JSON.toJSONString(LocalCacheHolder.getCacheHolder())));
        // 修改值之后的回调
        PropertiesChangeListenerHolder.doCallBack(configKeyNodeData, false);
        // 刷新本地配置文件
        DownLoadService.downloadProperties(LocalCacheHolder.getAllConfigKeyData(), bridgeConfig);
    }


    /**
     * 初始化缓存
     *
     * @param envEnum
     * @param appCode
     */
    private void doCacheInit(String appCode, EnvEnum envEnum) {
        List<ConfigKeyNodeData> configList = BridgeNodeManager.getInstance().getFromZookeeper(appCode, envEnum);
        // 写入数据至缓存池中
        LocalCacheHolder.setConfigKeyData2Cache(configList, true);
        LogHandler.info("配置项数据已存入缓存仓库中 ...");
        // 添加对app的子节点的监听
        BridgeNodeManager.getInstance().addAppNodePathChildrenListener(NodePathUtils.getAppNodePath(appCode, envEnum));
        // 注册消费者节点
        BridgeNodeManager.getInstance().registerConsumerHost();
        LogHandler.info("系统节点注册监听成功 ...");
        // 执行检查临时节点的订阅情况的任务,服务启动10分钟后开始执行，以后每次延迟10分钟间隔50分钟即以后为1小时执行一次
        AutoCheckNodeSchedule.startAutoCheckNodeSchedule(envEnum);
        // 构建properties对象
        BridgePropertySource.getInstance().buildProperties(LocalCacheHolder.getCacheHolder());
        // 保存配置文件到本地
        DownLoadService.downloadProperties(configList, bridgeConfig);
    }
}

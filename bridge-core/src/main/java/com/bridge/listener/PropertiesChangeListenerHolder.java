package com.bridge.listener;

import com.bridge.exception.BridgeProcessFailException;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jay
 * @version v1.0
 * @description 监听器的容器
 * @date 2019-02-19 15:48
 */
public class PropertiesChangeListenerHolder {

    /**
     * 监听部分key的监听器容器
     */
    private static ConcurrentHashMap<String, List<PropertiesChangeListener>> holder = new ConcurrentHashMap<>();

    /**
     * 监听所有key的监听器容器
     */
    private static List<PropertiesChangeListener> allKeyListener = Collections.synchronizedList(new ArrayList<>());


    /**
     * 放入监听的容器内
     *
     * @param key
     * @param propertiesChangeListener
     * @return
     */
    public static void addPropertiesChangeListener(String key, PropertiesChangeListener propertiesChangeListener) {
        if (StringUtils.isEmpty(key) || propertiesChangeListener == null) {
            return;
        }
        List<PropertiesChangeListener> listenerList = holder.get(key);
        if (CollectionUtils.isEmpty(listenerList)) {
            listenerList = new ArrayList<>();
            listenerList.add(propertiesChangeListener);
            holder.put(key, listenerList);
        } else {
            listenerList.add(propertiesChangeListener);
        }
    }


    /**
     * 放入监听的容器内
     *
     * @param ketSet
     * @param changeListener
     */
    public static void addPropertiesChangeListener(Set<String> ketSet, PropertiesChangeListener changeListener) {
        if (CollectionUtils.isEmpty(ketSet) || changeListener == null) {
            return;
        }
        ketSet.forEach(key -> addPropertiesChangeListener(key, changeListener));
    }

    /**
     * 根据key去取对应的监听器
     *
     * @param key
     * @return
     */
    public static List<PropertiesChangeListener> getPropertiesChangeListenerList(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        return holder.get(key);
    }

    /**
     * 添加所有key的监听器
     *
     * @param changeListener
     */
    public static void addAllKeyListener(PropertiesChangeListener changeListener) {
        allKeyListener.add(changeListener);
    }


    /**
     * 回调
     *
     * @param isBeforeChange
     * @param configKeyNodeData
     */
    public static void doCallBack(ConfigKeyNodeData configKeyNodeData, boolean isBeforeChange) {
        // 监听的部分key的回调
        List<PropertiesChangeListener> keyListenerList = getPropertiesChangeListenerList(configKeyNodeData.getKey());
        if (!CollectionUtils.isEmpty(keyListenerList)) {
            keyListenerList.forEach(
                    propertiesChangeListener -> callBack(isBeforeChange, propertiesChangeListener, configKeyNodeData)
            );
        }
        // 所有key的监听器回调
        if (!CollectionUtils.isEmpty(allKeyListener)) {
            allKeyListener.forEach(
                    propertiesChangeListener -> callBack(isBeforeChange, propertiesChangeListener, configKeyNodeData)
            );
        }
    }


    /**
     * 回调
     *
     * @param isBeforeChange
     * @param propertiesChangeListener
     * @param configKeyNodeData
     */
    private static void callBack(boolean isBeforeChange, PropertiesChangeListener propertiesChangeListener,
                                 ConfigKeyNodeData configKeyNodeData) {
        if (isBeforeChange) {
            propertiesChangeListener.onBeforePropertiesChanged(configKeyNodeData);
        } else {
            propertiesChangeListener.onPropertiesChanged(configKeyNodeData);
        }
    }

    /**
     * 清空监听
     */
    public static void clearPropertiesChangeListener() {
        if (holder != null) {
            holder.clear();
        }
        if (allKeyListener != null) {
            allKeyListener.clear();
        }
    }


}

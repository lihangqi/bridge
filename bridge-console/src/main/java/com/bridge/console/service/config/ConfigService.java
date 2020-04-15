package com.bridge.console.service.config;

import com.bridge.console.model.entity.ConfigDO;
import com.bridge.console.model.vo.*;
import com.bridge.domain.ConfigDTO;
import com.bridge.zookeeper.data.MachineNodeData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 配置文件操作
 * @date 2019-01-28 14:43
 */
public interface ConfigService {


    /**
     * 获取选项列表,如果是管理员则获取所有的，如果是团队leader则获取自己团队下的
     *
     * @param teamId
     * @return
     */
    List<ConfigSelectorVO> getSelectorData(Integer teamId);


    /**
     * 新增kv
     *
     * @param configDataVO
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    Boolean addConfigKv(ConfigDataVO configDataVO, Integer operatorId, Integer roleType, Integer teamId);


    /**
     * 编辑kv
     *
     * @param configDataVO
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    Boolean editConfigKv(ConfigDataVO configDataVO, Integer operatorId, Integer roleType, Integer teamId);


    /**
     * 下发k/v至客户端
     *
     * @param pushConfigKvVO
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    Boolean pushConfigKv(PushConfigKvVO pushConfigKvVO, Integer operatorId, Integer roleType, Integer teamId);


    /**
     * 一键下发k/v至客户端，全量下发
     *
     * @param appId
     * @param envId
     * @param operatorId
     * @param roleType
     * @param teamId
     * @return
     */
    Boolean pushAllConfigKv(Integer appId, Integer envId, Integer operatorId, Integer roleType, Integer teamId);


    /**
     * 通知zk
     *
     * @param machineList
     * @param configDO
     */
    void notifyZookeeper(List<String> machineList, ConfigDO configDO);


    /**
     * 获取客户端节点数据
     *
     * @param appCode
     * @param configKey
     * @param envId
     * @return
     */
    List<MachineNodeData> getMachineNodeDataListByConfigKey(String appCode, String configKey, Integer envId);

    /**
     * 查询key的集合列表
     *
     * @param appId
     * @param envId
     * @return
     */
    List<ValueVO> queryConfigKeyList(Integer appId, Integer envId);


    /**
     * 根据uniqueToken查询配置列表
     *
     * @param uniqueToken
     * @return
     */
    ConfigDTO getConfigList(String uniqueToken);


    /**
     * 删除key
     *
     * @param id
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    Boolean deleteConfigKey(Integer id, Integer operatorId, Integer roleType, Integer teamId);


    /**
     * 根据appId查询
     *
     * @param appId
     * @param envId
     * @return
     */
    List<ZkDataVO> queryZkDataList(Integer appId, Integer envId);


    /**
     * 下发次数自增1
     *
     * @param operatorId
     */
    void increasePushCount(Integer operatorId);


    /**
     * 判断是否有权限操作配置项，包括编辑、新增、下发、回滚
     *
     * @param operatorId
     * @param appId
     * @param roleType
     * @param operatorTeamId
     */
    void permissionCheck(Integer operatorId, Integer appId, Integer roleType, Integer operatorTeamId);


    /**
     * 导出文件
     *
     * @param configListQuery
     * @param request
     * @param response
     * @throws IOException
     */
    void exportFile(ConfigListQuery configListQuery, HttpServletResponse response, HttpServletRequest request) throws IOException;


    /**
     * 查询消费者订阅情况
     *
     * @param appId
     * @param envId
     * @return
     */
    List<String> queryConsumerHost(Integer appId, Integer envId);


    /**
     * 解析文件
     *
     * @param inputStream
     * @param appId
     * @param envId
     * @return
     */
    String parseProperties(InputStream inputStream, Integer appId, Integer envId);


    /**
     * 同步配置项至其他环境
     *
     * @param envId
     * @param appId
     * @param configKvVOList
     * @param userId
     */
    void syncConfigKey(Integer envId, Integer appId, List<ConfigKvVO> configKvVOList, Integer userId);
}

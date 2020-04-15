package com.bridge.console.model.dao;

import com.bridge.console.model.entity.ConfigDO;
import com.bridge.console.model.vo.ConfigListQuery;
import com.bridge.console.model.vo.ValueVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description k/v表
 * @date 2019-01-29 11:50
 */
@Repository
public interface ConfigMapper {
    /**
     * 根据id删除记录
     *
     * @param id 记录id
     * @return 删除条数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insert(ConfigDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(ConfigDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    ConfigDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateConfigKv(ConfigDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(ConfigDO record);


    /**
     * 查询记录数目
     *
     * @param configListQuery
     * @return
     */
    int countConfigKeyList(ConfigListQuery configListQuery);


    /**
     * 查询列表
     *
     * @param configListQuery
     * @return
     */
    List<ConfigDO> queryConfigKeyList(ConfigListQuery configListQuery);


    /**
     * 判断key是否存在
     *
     * @param configKey
     * @param appId
     * @param envId
     * @return
     */
    int judgeKeyIsExist(@Param("configKey") String configKey, @Param("appId") Integer appId, @Param("envId") Integer envId);


    /**
     * 根据id和appId查询记录
     *
     * @param id
     * @return
     */
    ConfigDO selectById(@Param("id") Integer id);

    /**
     * 更新preConfigValue至configValue
     *
     * @param configDO
     * @return
     */
    int updateById(ConfigDO configDO);


    /**
     * 查询list
     *
     * @param appId
     * @param envId
     * @return
     */
    List<ValueVO> selectConfigKeyList(@Param("appId") Integer appId, @Param("envId") Integer envId);


    /**
     * 查询生效的k/v
     *
     * @param appId
     * @return
     */
    List<ConfigDO> selectByAppId(@Param("appId") Integer appId);


    /**
     * 根据删除id记录
     *
     * @param id
     * @param modifier
     * @return
     */
    int deletedById(@Param("modifier") Integer modifier, @Param("id") Integer id);


    /**
     * 查询key
     *
     * @param configKey
     * @param appId
     * @param envId
     * @return
     */
    ConfigDO selectByAppIdAndEnvIdAndConfigKey(@Param("configKey") String configKey,
                                               @Param("appId") Integer appId,
                                               @Param("envId") Integer envId);

    /**
     * 更新预备值
     *
     * @param configDO
     * @return
     */
    int updatePreConfigValue(ConfigDO configDO);


    /**
     * 根据appId和envId查询所有配置项id
     *
     * @param appId
     * @param envId
     * @return
     */
    List<Integer> selectConfigKeyIdList(@Param("appId") Integer appId, @Param("envId") Integer envId);
}
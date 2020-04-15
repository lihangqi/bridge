package com.bridge.console.model.dao;

import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.vo.AppListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 系统定义表
 * @date 2019-01-21 21:01
 */
@Repository
public interface AppDefMapper {

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
    int insert(AppDefDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(AppDefDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    AppDefDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKeySelective(AppDefDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(AppDefDO record);


    /**
     * 查询列表数目
     *
     * @param appListQuery
     * @return
     */
    int countAppList(AppListQuery appListQuery);


    /**
     * 查询列表
     *
     * @param appListQuery
     * @return
     */
    List<AppDefDO> queryAppList(AppListQuery appListQuery);


    /**
     * 查询工作台应用列表
     *
     * @param appListQuery
     * @return
     */
    List<AppDefDO> queryWorkSpaceAppList(AppListQuery appListQuery);

    /**
     * 根据id更新
     *
     * @param appDefDO
     * @return
     */
    int updateById(AppDefDO appDefDO);


    /**
     * 根据appCode查询记录
     *
     * @param appCode
     * @return 对应记录
     */
    AppDefDO selectByAppCode(@Param("appCode") String appCode);


    /**
     * 根据appCode查询记录appName
     *
     * @param appCode
     * @return
     */
    String selectAppNameByAppCode(@Param("appCode") String appCode);


    /**
     * 根据appOwner查询记录
     *
     * @param appOwner
     * @return 对应记录
     */
    List<AppDefDO> selectByAppOwner(@Param("appOwner") Integer appOwner);


    /**
     * 根据id查询appCode
     *
     * @param id
     * @return
     */
    String selectAppCodeById(@Param("id") Integer id);


    /**
     * 根据id查询appName
     *
     * @param id
     * @return
     */
    String selectAppNameById(@Param("id") Integer id);

    /**
     * 删除记录
     *
     * @param id
     * @param modifier
     * @return
     */
    int deleteById(@Param("id") Integer id, @Param("modifier") Integer modifier);


    /**
     * 查询appId集合
     *
     * @param appName
     * @param teamId
     * @return
     */
    List<Integer> queryAppIdList(@Param("appName") String appName, @Param("teamId") Integer teamId);


    /**
     * 查询app
     *
     * @param appName
     * @return
     */
    AppDefDO selectByAppName(@Param("appName") String appName);
}
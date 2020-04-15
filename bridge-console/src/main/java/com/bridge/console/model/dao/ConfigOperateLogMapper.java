package com.bridge.console.model.dao;

import com.bridge.console.model.entity.ConfigOperateLogDO;
import com.bridge.console.model.vo.OperateLogQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description k/v操作日志表
 * @date 2019-01-29 11:50
 */
@Repository
public interface ConfigOperateLogMapper {

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
    int insert(ConfigOperateLogDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(ConfigOperateLogDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    ConfigOperateLogDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKeySelective(ConfigOperateLogDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(ConfigOperateLogDO record);

    /**
     * 统计总条数
     *
     * @param operateLogQuery
     * @return
     */
    int countOperateLogList(OperateLogQuery operateLogQuery);


    /**
     * 查询记录
     *
     * @param operateLogQuery
     * @return
     */
    List<ConfigOperateLogDO> queryOperateLogList(OperateLogQuery operateLogQuery);
}
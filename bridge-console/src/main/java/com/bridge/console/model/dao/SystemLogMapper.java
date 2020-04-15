package com.bridge.console.model.dao;

import com.bridge.console.model.entity.SystemLogDO;
import com.bridge.console.model.vo.SystemLogQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 系统日志表
 * @date 2019-01-29 11:50
 */

@Repository
public interface SystemLogMapper {
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
    int insert(SystemLogDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(SystemLogDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    SystemLogDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKeySelective(SystemLogDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(SystemLogDO record);


    /**
     * 查询记录条数
     *
     * @param systemLogQuery
     * @return
     */
    int countSystemLogList(SystemLogQuery systemLogQuery);


    /**
     * 查询记录
     *
     * @param systemLogQuery
     * @return
     */
    List<SystemLogDO> querySystemLogList(SystemLogQuery systemLogQuery);
}
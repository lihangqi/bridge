package com.bridge.console.model.dao;


import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.console.model.vo.UserListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 用户信息表
 * @date 2019-01-21 21:01
 */
@Repository
public interface UserAccountMapper {

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
    int insert(UserAccountDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(UserAccountDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    UserAccountDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKeySelective(UserAccountDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(UserAccountDO record);


    /**
     * 根据账号查询数据
     *
     * @param accountName
     * @return
     */
    UserAccountDO selectByAccountName(@Param("accountName") String accountName);


    /**
     * 根据账号查询数据
     *
     * @param token
     * @return
     */
    UserAccountDO selectByToken(@Param("token") String token);


    /**
     * 更新token
     *
     * @param token
     * @param id
     * @return
     */
    int updateTokenByAccountId(@Param("token") String token, @Param("id") Integer id);


    /**
     * 统计数目接口
     *
     * @param userListQuery
     * @return
     */
    int countUserPageList(UserListQuery userListQuery);


    /**
     * 查询接口
     *
     * @param userListQuery
     * @return
     */
    List<UserAccountDO> queryUserPageList(UserListQuery userListQuery);


    /**
     * 更新是否启用
     *
     * @param accountId
     * @param enabledState
     * @return
     */
    int updateEnableById(@Param("accountId") Integer accountId, @Param("enabledState") Integer enabledState);


    /**
     * 更新记录删除状态
     *
     * @param id
     * @param isDeleted
     * @param modifier
     * @return
     */
    int updateIsDeletedById(@Param("id") Integer id, @Param("isDeleted") Integer isDeleted, @Param("modifier") Integer modifier);


    /**
     * 根据teamId查询满足条件的记录
     *
     * @param teamId
     * @return
     */
    int countNumberByTeamId(@Param("teamId") Integer teamId);


    /**
     * 根据id查询真实姓名
     *
     * @param id
     * @return
     */
    String selectRealNameById(@Param("id") Integer id);


    /**
     * 查询用户列表
     *
     * @param teamId
     * @return
     */
    List<UserAccountDO> queryUserList(@Param("teamId") Integer teamId);


    /**
     * pushCount自增1
     *
     * @param operatorId
     * @return
     */
    int increasePushCount(Integer operatorId);


    /**
     * 查询下发次数
     *
     * @param teamId
     * @param id
     * @return
     */
    int selectPushCount(@Param("teamId") Integer teamId, @Param("id") Integer id);


    /**
     * 查询团队下的用户id集合
     *
     * @param teamId
     * @return
     */
    List<Integer> queryUserIdListInTeam(@Param("teamId") Integer teamId);
}
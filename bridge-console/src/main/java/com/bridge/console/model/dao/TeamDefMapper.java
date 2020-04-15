package com.bridge.console.model.dao;

import com.bridge.console.model.entity.TeamDefDO;
import com.bridge.console.model.vo.TeamAndAppVO;
import com.bridge.console.model.vo.TeamIdAndNameVO;
import com.bridge.console.model.vo.TeamListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description team信息表
 * @date 2019-01-21 21:01
 */
@Repository
public interface TeamDefMapper {
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
    int insert(TeamDefDO record);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 插入条数
     */
    int insertSelective(TeamDefDO record);

    /**
     * 根据主键查询记录
     *
     * @param id 主键id
     * @return 对应记录
     */
    TeamDefDO selectByPrimaryKey(Integer id);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKeySelective(TeamDefDO record);

    /**
     * 更新记录
     *
     * @param record 对应记录
     * @return 更新条数
     */
    int updateByPrimaryKey(TeamDefDO record);


    /**
     * 查询所有
     *
     * @param id
     * @return
     */
    List<TeamDefDO> findAll(@Param("id") Integer id);


    /**
     * 根据id查询团队名称
     *
     * @param id
     * @return
     */
    String selectTeamNameById(@Param("id") Integer id);


    /**
     * 根据teamName查询团队
     *
     * @param teamName
     * @return
     */
    TeamDefDO selectByTeamName(@Param("teamName") String teamName);


    /**
     * 查询所有
     *
     * @param teamListQuery
     * @return
     */
    List<TeamDefDO> queryPageList(TeamListQuery teamListQuery);


    /**
     * 查询所有数目
     *
     * @param teamListQuery
     * @return
     */
    int countList(TeamListQuery teamListQuery);


    /**
     * 根据id更新
     *
     * @param teamName
     * @param teamDes
     * @param id
     * @return
     */
    int updateByTeamName(@Param("teamName") String teamName, @Param("teamDes") String teamDes, @Param("id") Integer id);

    /**
     * 根据id删除记录
     *
     * @param teamId
     * @param modifier
     * @return
     */
    int deleteByTeamId(@Param("teamId") Integer teamId, @Param("modifier") Integer modifier);


    /**
     * 根据id查询
     *
     * @param teamId
     * @return
     */
    List<TeamDefDO> queryTeamList(@Param("teamId") Integer teamId);


    /**
     * 根据accountId查询对应的teamId和teamName
     *
     * @param accountId
     * @return
     */
    TeamIdAndNameVO selectTeamInfoByAccountId(@Param("accountId") Integer accountId);


    /**
     * 应用id
     *
     * @param appId
     * @return
     */
    TeamAndAppVO selectTeamNameAndAppNameByAppId(@Param("appId") Integer appId);
}
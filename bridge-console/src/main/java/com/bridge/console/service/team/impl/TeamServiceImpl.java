package com.bridge.console.service.team.impl;

import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.TeamDefDO;
import com.bridge.console.model.vo.TeamDefVO;
import com.bridge.console.service.team.TeamService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-24 17:48
 */
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;


    /**
     * 添加team
     *
     * @param teamDefVO
     * @return
     */
    @Override
    public Boolean addTeam(TeamDefVO teamDefVO) {
        // 参数校验
        this.checkTeamDefVO(teamDefVO);
        // 校验是否存在
        if (judgeIsExist(teamDefVO.getTeamName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "team已经存在");
        }
        // 保存
        TeamDefDO teamDefDO = new TeamDefDO();
        BeanUtil.copyProperties(teamDefVO, teamDefDO);
        Date date = new Date();
        teamDefDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        teamDefDO.setGmtCreate(date);
        teamDefDO.setGmtModified(date);
        teamDefDO.setCreator(0);
        teamDefDO.setCreator(0);
        if (teamDefMapper.insert(teamDefDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "新增数据失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 编辑team信息
     *
     * @param teamDefVO
     * @return
     */
    @Override
    public Boolean editTeam(TeamDefVO teamDefVO) {
        // 参数校验
        this.checkTeamDefVO(teamDefVO);
        // 更新
        if (teamDefMapper.updateByTeamName(teamDefVO.getTeamName(), teamDefVO.getTeamDes(), teamDefVO.getId())
                != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "更新数据失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 删除team
     *
     * @param teamId
     * @return
     */
    @Override
    public Boolean deleteTeam(Integer teamId, Integer modifier) {
        if (teamId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "teamId不能为空");
        }
        // 判断该teamId下面是否还有团队成员，如果有，则无法删除
        if (userAccountMapper.countNumberByTeamId(teamId) > BaseBizEnum.ZERO.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "该团队下仍有成员, 无法删除!");
        }
        // 删除
        if (teamDefMapper.deleteByTeamId(teamId, modifier) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "删除团队失败");
        }
        return Boolean.TRUE;
    }

    //-------------private method-----------------------------


    /**
     * 参数校验
     *
     * @param teamDefVO
     */
    private void checkTeamDefVO(TeamDefVO teamDefVO) {
        if (teamDefVO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (StringUtils.isEmpty(teamDefVO.getTeamName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "teamName不能为空");
        }
        if (StringUtils.isEmpty(teamDefVO.getTeamDes())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "teamDes不能为空");
        }
    }

    /**
     * 校验是否存在
     *
     * @param teamName
     */
    private boolean judgeIsExist(String teamName) {
        TeamDefDO teamDefDoFromDb = teamDefMapper.selectByTeamName(teamName);
        if (teamDefDoFromDb != null) {
            return true;
        }
        return false;
    }
}

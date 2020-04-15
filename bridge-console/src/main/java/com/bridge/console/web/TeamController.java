package com.bridge.console.web;

import com.bridge.console.annotation.AdminRequired;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.entity.TeamDefDO;
import com.bridge.console.model.vo.TeamDefVO;
import com.bridge.console.model.vo.TeamListQuery;
import com.bridge.console.service.team.TeamService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 团队相关的控制器
 * @date 2019-01-24 17:09
 */
@RestController
public class TeamController extends BaseController {


    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private TeamService teamService;

    /**
     * 分页查询团队列表
     *
     * @param teamListQuery
     * @param pageable
     * @return
     */
    @AdminRequired
    @RequestMapping("/queryTeamDefVOList")
    @ResponseBody
    public PagingResult<TeamDefVO> queryTeamDefVoList(TeamListQuery teamListQuery
            , @PageableDefault(page = 1, size = 20, sort = "gmt_create", direction = Sort.Direction.DESC) Pageable pageable) {
        int total = teamDefMapper.countList(teamListQuery);
        if (total == 0) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(teamListQuery, pageable);
        // 查询
        List<TeamDefDO> teamDefDoList = teamDefMapper.queryPageList(teamListQuery);
        if (CollectionUtils.isEmpty(teamDefDoList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        return PagingResult.wrapSuccessfulResult(BeanUtil.copyList(teamDefDoList, TeamDefVO.class), pageable, total);
    }


    /**
     * 新增团队记录
     *
     * @param teamDefVO
     * @return
     */
    @AdminRequired
    @RequestMapping("/addTeam")
    @ResponseBody
    public Result<Boolean> addTeam(TeamDefVO teamDefVO) {
        return Result.wrapSuccessfulResult(teamService.addTeam(teamDefVO));
    }


    /**
     * 更新团队记录
     *
     * @param teamDefVO
     * @return
     */
    @AdminRequired
    @RequestMapping("/editTeam")
    @ResponseBody
    public Result<Boolean> editTeam(TeamDefVO teamDefVO) {
        return Result.wrapSuccessfulResult(teamService.editTeam(teamDefVO));
    }


    /**
     * 删除团队记录
     *
     * @param teamId
     * @return
     */
    @AdminRequired
    @RequestMapping("/deleteTeam")
    @ResponseBody
    public Result<Boolean> deleteTeam(Integer teamId) {
        if (teamId.equals(getSessionContext().getTeamId())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "你无法删除自己的团队！");
        }
        return Result.wrapSuccessfulResult(teamService.deleteTeam(teamId, getSessionContext().getId()));
    }

}

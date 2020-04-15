package com.bridge.console.web;

import com.bridge.console.annotation.TeamLeaderRequired;
import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.entity.TeamDefDO;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.console.model.vo.*;
import com.bridge.console.service.system.AppManagerService;
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
 * @description 应用相关的controller
 * @date 2019-01-25 15:43
 */
@RestController
public class AppManagerController extends BaseController {


    @Autowired
    private AppManagerService appManagerService;

    @Autowired
    private AppDefMapper appDefMapper;

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    /**
     * 查询团队记录
     *
     * @param appListQuery
     * @param pageable
     * @return
     */
    @RequestMapping("/queryAppPageList")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public PagingResult<AppDefVO> queryAppPageList(AppListQuery appListQuery
            , @PageableDefault(page = 1, size = 20, sort = "gmt_create", direction = Sort.Direction.DESC) Pageable pageable) {
        if (appListQuery == null) {
            appListQuery = new AppListQuery();
        }
        // 如果是管理员则查询所有的，否则查询当前登录人的team下所有项目
        if (!isAdmin()) {
            appListQuery.setTeamId(getSessionContext().getTeamId());
        }
        int total = appDefMapper.countAppList(appListQuery);
        if (total == BaseBizEnum.ZERO.getCode()) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(appListQuery, pageable);
        List<AppDefDO> appDefDoList = appDefMapper.queryAppList(appListQuery);
        if (CollectionUtils.isEmpty(appDefDoList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        List<AppDefVO> appDefVoList = BeanUtil.copyList(appDefDoList, AppDefVO.class);
        appDefVoList.forEach(appDefVO -> {
            appDefVO.setTeamName(teamDefMapper.selectTeamNameById(appDefVO.getTeamId()));
            appDefVO.setOwnerRealName(userAccountMapper.selectRealNameById(appDefVO.getAppOwner()));
        });
        return PagingResult.wrapSuccessfulResult(appDefVoList, pageable, total);
    }


    /**
     * 查询团队列表，如果是管理员则查询所有团队,目前只有管理员和团队leader可以访问该接口
     *
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/queryTeamList")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public Result<List<EnumVO>> queryTeamList() {
        List<EnumVO> teamVoList = new ArrayList<>();
        Integer teamId = null;
        if (isTeamLeader()) {
            teamId = getSessionContext().getTeamId();
        }
        List<TeamDefDO> teamDefDoList = teamDefMapper.queryTeamList(teamId);
        if (!CollectionUtils.isEmpty(teamDefDoList)) {
            teamDefDoList.forEach(teamDefDO -> {
                EnumVO enumVO = new EnumVO();
                enumVO.setKey(teamDefDO.getId());
                enumVO.setValue(teamDefDO.getTeamName());
                teamVoList.add(enumVO);
            });
        }
        return Result.wrapSuccessfulResult(teamVoList);
    }


    /**
     * 根据accountId查询对应的teamId和teamName
     *
     * @param teamId
     * @return
     */
    @RequestMapping("/queryAccountByTeamId")
    @ResponseBody
    public Result<List<EnumVO>> queryAccountByTeamId(Integer teamId) {
        if (teamId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "teamId不能为空");
        }
        List<EnumVO> enumVoList = new ArrayList<>();
        List<UserAccountDO> userAccountDoList = userAccountMapper.queryUserList(teamId);
        if (!CollectionUtils.isEmpty(userAccountDoList)) {
            userAccountDoList.forEach(userAccountDO -> {
                EnumVO enumVO = new EnumVO();
                enumVO.setKey(userAccountDO.getId());
                enumVO.setValue(userAccountDO.getRealName());
                enumVoList.add(enumVO);
            });
        }
        return Result.wrapSuccessfulResult(enumVoList);
    }


    /**
     * 编辑应用,目前只有团队leader和管理员可以操作
     *
     * @param appDefEditOrAddVO
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/editApp")
    @ResponseBody
    public Result<Boolean> editApp(AppDefEditOrAddVO appDefEditOrAddVO) {
        return Result.wrapSuccessfulResult(appManagerService.editApp(appDefEditOrAddVO, getSessionContext().getId()));
    }


    /**
     * 新增应用,目前只有团队leader和管理员可以操作
     *
     * @param appDefEditOrAddVO
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/addApp")
    @ResponseBody
    public Result<Boolean> addApp(AppDefEditOrAddVO appDefEditOrAddVO) {
        return Result.wrapSuccessfulResult(appManagerService.addApp(appDefEditOrAddVO, getSessionContext().getId()));
    }


    /**
     * 删除应用,目前只有团队leader和管理员可以操作
     *
     * @param id
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/deleteApp")
    @ResponseBody
    public Result<Boolean> deleteApp(Integer id) {
        return Result.wrapSuccessfulResult(appManagerService.deleteApp(id, getSessionContext().getId()));
    }

}

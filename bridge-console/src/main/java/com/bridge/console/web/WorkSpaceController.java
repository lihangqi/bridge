package com.bridge.console.web;

import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.vo.WorkSpaceVO;
import com.bridge.console.service.operatelog.OperateLogService;
import com.bridge.console.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jay
 * @version v1.0
 * @description 工作台
 * @date 2019-02-20 14:54
 */
@RestController
public class WorkSpaceController extends BaseController {


    @Autowired
    private OperateLogService operateLogService;


    /**
     * 查询工作台数据
     *
     * @return
     */
    @RequestMapping("/getWorkSpaceInfo")
    @ResponseBody
    public Result<WorkSpaceVO> getWorkSpaceInfo(Integer envId) {
        Integer accountId = getSessionContext().getId();
        Integer teamId = getSessionContext().getTeamId();
        AccountRoleEnum accountRoleEnum = null;
        if (isAdmin()) {
            accountRoleEnum = AccountRoleEnum.ROLE_ADMIN;
        }
        if (isTeamLeader()) {
            accountRoleEnum = AccountRoleEnum.ROLE_TEAM_LEADER;
        }
        if (isUser()) {
            accountRoleEnum = AccountRoleEnum.ROLE_USER;
        }
        WorkSpaceVO workSpaceVO = operateLogService.getWorkSpaceInfo(teamId, accountId, accountRoleEnum,envId);
        return Result.wrapSuccessfulResult(workSpaceVO);
    }



}

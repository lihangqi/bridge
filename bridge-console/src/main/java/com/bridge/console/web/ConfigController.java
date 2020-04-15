package com.bridge.console.web;

import com.bridge.console.annotation.NotCertification;
import com.bridge.console.model.constant.Constant;
import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.ConfigMapper;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.entity.ConfigDO;
import com.bridge.console.model.enums.KeyTypeEnum;
import com.bridge.console.model.enums.PushStatusEnum;
import com.bridge.console.model.vo.*;
import com.bridge.console.service.config.ConfigService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.*;
import com.bridge.domain.ConfigDTO;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.NeedUpdateEnum;
import com.bridge.zookeeper.data.MachineNodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 配置项相关的controller
 * @date 2018-12-29 16:19
 */
@RestController
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    /**
     * 查询配置文件信息
     *
     * @param uniqueToken
     * @return
     */
    @NotCertification
    @RequestMapping("/getConfigList")
    public Result<ConfigDTO> getConfigList(String uniqueToken) {
        return Result.wrapSuccessfulResult(configService.getConfigList(uniqueToken));
    }

    /**
     * 获取选项列表,如果是管理员则获取所有的，如果是团队leader则获取自己团队下的
     *
     * @return
     */
    @RequestMapping("/getSelectorData")
    @ResponseBody
    public Result<List<ConfigSelectorVO>> getSelectorData() {
        Integer teamId = null;
        if (!isAdmin()) {
            teamId = getSessionContext().getTeamId();
        }
        return Result.wrapSuccessfulResult(configService.getSelectorData(teamId));
    }

    /**
     * 根据appId获取配置文件列表
     *
     * @param configListQuery
     * @return
     */
    @RequestMapping("/queryConfigDataByAppId")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public PagingResult<ConfigDataVO> queryConfigDataByAppId(ConfigListQuery configListQuery
            , @PageableDefault(page = 1, size = 20, sort = "config_key", direction = Sort.Direction.ASC) Pageable pageable) {
        if (configListQuery == null || configListQuery.getAppId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        if (configListQuery.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        int total = configMapper.countConfigKeyList(configListQuery);
        if (total == BaseBizEnum.ZERO.getCode()) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(configListQuery, pageable);
        // 查询
        List<ConfigDO> configList = configMapper.queryConfigKeyList(configListQuery);
        if (CollectionUtils.isEmpty(configList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        List<ConfigDataVO> configDataVoList = BeanUtil.copyList(configList, ConfigDataVO.class);
        configDataVoList.forEach(configDataVO -> {
            configDataVO.setPushStatus(PushStatusEnum.NOT_NEED_PUSH.getKey());
            // 获取machine节点的数据
            String appCode = appDefMapper.selectAppCodeById(configDataVO.getAppId());
            List<MachineNodeData> machineNodeDataList
                    = configService.getMachineNodeDataListByConfigKey(appCode, configDataVO.getConfigKey(), configListQuery.getEnvId());
            List<MachineNodeDataVO> machineNodeList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(machineNodeDataList)) {
                machineNodeDataList.forEach(machineNodeData -> {
                    if (machineNodeData != null) {
                        MachineNodeDataVO machineNodeDataVO = new MachineNodeDataVO();
                        machineNodeDataVO.setMachineHost(machineNodeData.getMachineHost());
                        // 根据版本号判断是否需要更新
                        if (!StringUtils.isEmpty(machineNodeData.getVersion()) && machineNodeData.getVersion().equals(configDataVO.getKeyVersion())) {
                            machineNodeDataVO.setNeedUpdate(NeedUpdateEnum.NOT_NEED_UPDATE.getKey());
                        } else {
                            machineNodeDataVO.setNeedUpdate(NeedUpdateEnum.NEED_UPDATE.getKey());
                            configDataVO.setPushStatus(PushStatusEnum.NEED_PUSH.getKey());
                        }
                        machineNodeList.add(machineNodeDataVO);
                    }
                });
            }
            configDataVO.setMachineNodeDataList(machineNodeList);
        });
        return PagingResult.wrapSuccessfulResult(configDataVoList, pageable, total);
    }


    /**
     * 根据appId查询团队名称和应用名称
     *
     * @param appId
     * @return
     */
    @RequestMapping("/queryTeamNameAndAppNameByAppId")
    @ResponseBody
    public Result<TeamAndAppVO> queryTeamNameAndAppNameByAppId(Integer appId) {
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        return Result.wrapSuccessfulResult(teamDefMapper.selectTeamNameAndAppNameByAppId(appId));
    }


    /**
     * 新增k/v
     *
     * @param configDataVO
     * @return
     */
    @RequestMapping("/addConfigKv")
    @ResponseBody
    public Result<Boolean> addConfigKv(ConfigDataVO configDataVO) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        return Result.wrapSuccessfulResult(configService.addConfigKv(configDataVO, accountId, roleType, teamId));
    }

    /**
     * 编辑k/v
     *
     * @param configDataVO
     * @return
     */
    @RequestMapping("/editConfigKv")
    @ResponseBody
    public Result<Boolean> editConfigKv(ConfigDataVO configDataVO) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        return Result.wrapSuccessfulResult(configService.editConfigKv(configDataVO, accountId, roleType, teamId));
    }

    /**
     * 下发k/v至客户端
     *
     * @param pushConfigKvVO
     * @return
     */
    @RequestMapping("/pushConfigKv")
    @ResponseBody
    public Result<Boolean> pushConfigKv(@RequestBody PushConfigKvVO pushConfigKvVO) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        Boolean result = configService.pushConfigKv(pushConfigKvVO, accountId, roleType, teamId);
        return Result.wrapSuccessfulResult(result);
    }


    /**
     * 一键下发k/v至客户端，全量下发
     *
     * @param envId
     * @param appId
     * @return
     */
    @RequestMapping("/pushAllConfigKv")
    @ResponseBody
    public Result<Boolean> pushAllConfigKv(Integer envId, Integer appId) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        if (envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        Boolean result = configService.pushAllConfigKv(appId, envId, accountId, roleType, teamId);
        return Result.wrapSuccessfulResult(result);
    }


    /**
     * 查询configKey的集合
     *
     * @param appId
     * @param envId
     * @return
     */
    @RequestMapping("/queryConfigKeyList")
    @ResponseBody
    public Result<List<ValueVO>> queryConfigKeyList(Integer appId, Integer envId) {
        return Result.wrapSuccessfulResult(configService.queryConfigKeyList(appId, envId));
    }


    /**
     * 删除configKey
     *
     * @param id
     * @return
     */
    @RequestMapping("/deleteConfigKey")
    @ResponseBody
    public Result<Boolean> deleteConfigKey(Integer id) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        return Result.wrapSuccessfulResult(configService.deleteConfigKey(id, accountId, roleType, teamId));
    }


    /**
     * 批量删除configKey
     *
     * @param configKeyDelVO
     * @return
     */
    @RequestMapping("/batchDeleteConfigKey")
    @ResponseBody
    public Result<Boolean> batchDeleteConfigKey(@RequestBody ConfigKeyDelVO configKeyDelVO) {
        Integer accountId = getSessionContext().getId();
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        if (configKeyDelVO == null || CollectionUtils.isEmpty(configKeyDelVO.getIdList())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请选择要删除的配置项");
        }
        configKeyDelVO.getIdList().forEach(id -> {
            configService.deleteConfigKey(id, accountId, roleType, teamId);
        });
        return Result.wrapSuccessfulResult(Boolean.TRUE);
    }


    /**
     * 导出配置项
     *
     * @param configListQuery
     * @param httpServletResponse
     * @return
     */
    @NotCertification
    @RequestMapping("/exportFile")
    @ResponseBody
    @SuppressWarnings("ALL")
    public Result<String> exportFile(ConfigListQuery configListQuery, HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        if (configListQuery == null || configListQuery.getAppId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择系统");
        }
        if (configListQuery.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先指定环境");
        }
        if (configMapper.countConfigKeyList(configListQuery) == BaseBizEnum.ZERO.getCode()) {
            return Result.wrapErrorResult(BaseErrorEnum.DATA_NOT_EXISTS.getCode(), "暂无数据可以导出");
        }
        configListQuery.setSorts(Arrays.asList("config_key asc"));
        configService.exportFile(configListQuery, response, request);
        return Result.wrapSuccessfulResult("导出成功");
    }


    /**
     * 判断是否有数据导出
     *
     * @param configListQuery
     * @return
     */
    @RequestMapping("/judgeExportFile")
    @ResponseBody
    @SuppressWarnings("ALL")
    public Result<String> judgeExportFile(ConfigListQuery configListQuery) {
        if (configListQuery == null || configListQuery.getAppId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择系统");
        }
        if (configListQuery.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先指定环境");
        }
        if (configMapper.countConfigKeyList(configListQuery) == BaseBizEnum.ZERO.getCode()) {
            return Result.wrapErrorResult(BaseErrorEnum.DATA_NOT_EXISTS.getCode(), "暂无数据可以导出");
        }
        return Result.wrapSuccessfulResult("等待导出数据");
    }


    /**
     * 查询订阅的节点
     *
     * @param appId
     * @param envId
     * @return
     */
    @RequestMapping("/queryConsumerHost")
    @ResponseBody
    public Result<List<String>> queryConsumerHost(Integer appId, Integer envId) {
        return Result.wrapSuccessfulResult(configService.queryConsumerHost(appId, envId));
    }


    /**
     * 查询KeyType的集合
     *
     * @return
     */
    @RequestMapping("/queryKeyTypeList")
    @ResponseBody
    public Result<List<EnumVO>> queryKeyTypeList() {
        List<EnumVO> enumVoList = new ArrayList<>();
        for (KeyTypeEnum enumObj : KeyTypeEnum.values()) {
            EnumVO enumVO = new EnumVO();
            enumVO.setKey(enumObj.getKey());
            enumVO.setValue(enumObj.getName());
            enumVoList.add(enumVO);
        }
        return Result.wrapSuccessfulResult(enumVoList);
    }


    /**
     * 上传文件解析
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @RequestMapping("/uploadProperties")
    @ResponseBody
    public Result<String> uploadProperties(MultipartFile multipartFile, Integer envId, Integer appId) throws IOException {
        if (multipartFile == null) {
            return Result.wrapErrorResult(BridgeErrorEnum.FILE_TYPE_ERROR.getCode(), "请选择需要上传的文件");
        }
        // 获取文件
        InputStream inputStream = multipartFile.getInputStream();
        String fileName = multipartFile.getOriginalFilename();
        if (inputStream == null || StringUtils.isEmpty(fileName) || inputStream.available() == 0) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "文件为空");
        }
        if (envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        String msg = checkExt(fileName);
        if (msg != null) {
            return Result.wrapErrorResult(BridgeErrorEnum.FILE_TYPE_ERROR.getCode(), msg);
        }
        String tip = configService.parseProperties(inputStream, appId, envId);
        if (tip != null) {
            return Result.wrapErrorResult(BaseErrorEnum.DATA_NOT_EXISTS.getCode(), tip);
        }
        return Result.wrapSuccessfulResult("操作成功");
    }


    /**
     * 同步配置项至其他环境
     *
     * @param syncConfigKeyVO
     * @return
     */
    @RequestMapping("/syncConfigKey")
    @ResponseBody
    public Result<Boolean> syncConfigKey(@RequestBody SyncConfigKeyVO syncConfigKeyVO) {
        if (syncConfigKeyVO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        Integer envId = syncConfigKeyVO.getEnvId();
        if (envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择需要同步的环境");
        }
        Integer appId = syncConfigKeyVO.getAppId();
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择系统");
        }
        List<ConfigKvVO> configKvVOList = syncConfigKeyVO.getConfigKvVOList();
        if (CollectionUtils.isEmpty(configKvVOList)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "同步的配置项不能为空");
        }
        // 权限校验
        configService.permissionCheck(getUserId(), appId, getAccounntRole(), getTeamId());
        // 同步配置项至其他环境
        configService.syncConfigKey(envId, appId, configKvVOList, getUserId());
        return Result.wrapSuccessfulResult(Boolean.TRUE);
    }





    /*--------------------------------------------------private method---------------------------------------------*/

    /**
     * 截取文件后缀并校验是否合法
     *
     * @param fileName
     * @return
     */
    private String checkExt(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "文件不能为空";
        }
        int index = fileName.lastIndexOf(".");
        String ext = index >= 0 ? fileName.substring(index + 1) : fileName;
        if (StringUtils.isEmpty(ext)) {
            return "文件类型需要为.properties";
        }
        if (!ext.equals(Constant.PROPERTIES)) {
            return "文件类型需要为.properties";
        }
        return null;
    }


}

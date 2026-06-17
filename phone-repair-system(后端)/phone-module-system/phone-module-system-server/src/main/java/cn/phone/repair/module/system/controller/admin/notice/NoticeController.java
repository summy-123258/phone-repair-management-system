package cn.phone.repair.module.system.controller.admin.notice;

import cn.hutool.core.lang.Assert;
import cn.phone.repair.framework.common.enums.UserTypeEnum;
import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.module.infra.api.websocket.WebSocketSenderApi;
import cn.phone.repair.module.system.api.notify.NotifyMessageSendApi;
import cn.phone.repair.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.phone.repair.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.phone.repair.module.system.controller.admin.notice.vo.NoticeRespVO;
import cn.phone.repair.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import cn.phone.repair.module.system.dal.dataobject.notice.NoticeDO;
import cn.phone.repair.module.system.dal.dataobject.user.AdminUserDO;
import cn.phone.repair.module.system.service.notice.NoticeService;
import cn.phone.repair.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 通知公告")
@RestController
@RequestMapping("/system/notice")
@Validated
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private WebSocketSenderApi webSocketSenderApi;

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Resource
    private AdminUserService adminUserService;

    @PostMapping("/create")
    @Operation(summary = "创建通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:create')")
    public CommonResult<Long> createNotice(@Valid @RequestBody NoticeSaveReqVO createReqVO) {
        Long noticeId = noticeService.createNotice(createReqVO);
        return success(noticeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody NoticeSaveReqVO updateReqVO) {
        noticeService.updateNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除通知公告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除通知公告")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNoticeList(@RequestParam("ids") List<Long> ids) {
        noticeService.deleteNoticeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获取通知公告列表")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<PageResult<NoticeRespVO>> getNoticePage(@Validated NoticePageReqVO pageReqVO) {
        PageResult<NoticeDO> pageResult = noticeService.getNoticePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, NoticeRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得通知公告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<NoticeRespVO> getNotice(@RequestParam("id") Long id) {
        NoticeDO notice = noticeService.getNotice(id);
        return success(BeanUtils.toBean(notice, NoticeRespVO.class));
    }

    @PostMapping("/push")
    @Operation(summary = "推送通知公告给所有用户", description = "用户登录后在消息中心可见")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> push(@RequestParam("id") Long id) {
        NoticeDO notice = noticeService.getNotice(id);
        Assert.notNull(notice, "公告不能为空");

        // 1. WebSocket 在线推送
        webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), "notice-push", notice);

        // 2. 获取【所有正常用户】（100%匹配你现有接口）
        List<AdminUserDO> userList = adminUserService.getUserListByStatus(0); // 0=正常
        List<Long> userIds = userList.stream().map(AdminUserDO::getId).collect(Collectors.toList());

        // 3. 组装参数（无 Map.of，不爆红）
        Map<String, Object> params = new HashMap<>();
        params.put("title", notice.getTitle());
        params.put("content", notice.getContent());

        // 4. 给【所有用户】发送站内信
        for (Long userId : userIds) {
            NotifySendSingleToUserReqDTO reqDTO = new NotifySendSingleToUserReqDTO();
            reqDTO.setUserId(userId);
            reqDTO.setTemplateCode("sys-notice-push");
            reqDTO.setTemplateParams(params);
            notifyMessageSendApi.sendSingleMessageToAdmin(reqDTO);
        }

        return success(true);
    }

}

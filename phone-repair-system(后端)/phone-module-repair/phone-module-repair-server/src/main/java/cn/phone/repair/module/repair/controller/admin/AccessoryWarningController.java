package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryWarningRespVO;
import cn.phone.repair.module.repair.service.AccessoryWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 配件预警")
@RestController
@RequestMapping("/repair/accessory/warning")
@Validated
public class AccessoryWarningController {

    @Resource
    private AccessoryWarningService accessoryWarningService;

    @GetMapping("/list")
    @Operation(summary = "获取预警列表")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryWarningRespVO>> getAccessoryWarningList() {
        return success(accessoryWarningService.getAccessoryWarningList());
    }

    @GetMapping("/list-by-status")
    @Operation(summary = "根据状态获取预警列表")
    @Parameter(name = "status", description = "状态", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryWarningRespVO>> getAccessoryWarningListByStatus(@RequestParam("status") String status) {
        return success(accessoryWarningService.getAccessoryWarningListByStatus(status));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新预警状态")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "status", description = "状态", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:update')")
    public CommonResult<Boolean> updateWarningStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status) {
        accessoryWarningService.updateWarningStatus(id, status);
        return success(true);
    }

    @PostMapping("/check-all")
    @Operation(summary = "检查所有配件预警")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<Boolean> checkAllAccessoryWarnings() {
        accessoryWarningService.checkAllAccessoryWarnings();
        return success(true);
    }
}

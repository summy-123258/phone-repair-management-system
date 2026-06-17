package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryFlowRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryReturnSaveReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryUseSaveReqVO;
import cn.phone.repair.module.repair.service.AccessoryFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 配件流水")
@RestController
@RequestMapping("/repair/accessory/flow")
@Validated
public class AccessoryFlowController {

    @Resource
    private AccessoryFlowService accessoryFlowService;

    @PostMapping("/use")
    @Operation(summary = "领用配件")
    @PreAuthorize("@ss.hasPermission('repair:accessory:use')")
    public CommonResult<Long> createAccessoryUse(@Valid @RequestBody AccessoryUseSaveReqVO reqVO) {
        return success(accessoryFlowService.createAccessoryUse(reqVO));
    }

    @PostMapping("/return")
    @Operation(summary = "退库配件")
    @PreAuthorize("@ss.hasPermission('repair:accessory:return')")
    public CommonResult<Long> createAccessoryReturn(@Valid @RequestBody AccessoryReturnSaveReqVO reqVO) {
        return success(accessoryFlowService.createAccessoryReturn(reqVO));
    }

    @GetMapping("/list-by-repair-order")
    @Operation(summary = "根据维修单号获取配件流水")
    @Parameter(name = "repairOrderNo", description = "维修单号", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryFlowRespVO>> getAccessoryFlowListByRepairOrderNo(@RequestParam("repairOrderNo") String repairOrderNo) {
        return success(accessoryFlowService.getAccessoryFlowListByRepairOrderNo(repairOrderNo));
    }

    @GetMapping("/list-by-accessory")
    @Operation(summary = "根据配件获取流水")
    @Parameter(name = "accessoryId", description = "配件ID", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryFlowRespVO>> getAccessoryFlowListByAccessoryId(@RequestParam("accessoryId") Long accessoryId) {
        return success(accessoryFlowService.getAccessoryFlowListByAccessoryId(accessoryId));
    }

    @GetMapping("/available-return-quantity")
    @Operation(summary = "获取可退数量")
    @Parameter(name = "repairOrderNo", description = "维修单号", required = true)
    @Parameter(name = "accessoryId", description = "配件ID", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<Integer> getAvailableReturnQuantity(
            @RequestParam("repairOrderNo") String repairOrderNo,
            @RequestParam("accessoryId") Long accessoryId) {
        return success(accessoryFlowService.getAvailableReturnQuantity(repairOrderNo, accessoryId));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有配件流水")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryFlowRespVO>> getAllAccessoryFlowList() {
        return success(accessoryFlowService.getAllAccessoryFlowList());
    }
}

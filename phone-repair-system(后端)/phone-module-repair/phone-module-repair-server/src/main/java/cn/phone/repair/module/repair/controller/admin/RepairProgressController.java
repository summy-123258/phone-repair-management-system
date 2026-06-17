package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairProgressSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.RepairProgressDO;
import cn.phone.repair.module.repair.service.RepairProgressService;
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

@Tag(name = "管理后台 - 维修进度")
@RestController
@RequestMapping("/repair/repair-progress")
@Validated
public class RepairProgressController {

    @Resource
    private RepairProgressService repairProgressService;

    @PostMapping("/create")
    @Operation(summary = "创建维修进度")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:update')")
    public CommonResult<Long> createRepairProgress(@Valid @RequestBody RepairProgressSaveReqVO createReqVO) {
        return success(repairProgressService.createRepairProgress(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除维修进度")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('repair:repair-order:delete')")
    public CommonResult<Boolean> deleteRepairProgress(@RequestParam("id") Long id) {
        repairProgressService.deleteRepairProgress(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取维修进度列表")
    @Parameter(name = "orderId", description = "维修单ID", required = true)
    @PreAuthorize("@ss.hasPermission('repair:repair-order:query')")
    public CommonResult<List<RepairProgressDO>> getRepairProgressListByOrderId(@RequestParam("orderId") Long orderId) {
        return success(repairProgressService.getRepairProgressListByOrderId(orderId));
    }
}

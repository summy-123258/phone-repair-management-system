package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.framework.common.pojo.PageParam;
import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.framework.excel.core.util.ExcelUtils;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderPageReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.CustomerDO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import cn.phone.repair.module.repair.service.CustomerService;
import cn.phone.repair.module.repair.service.RepairOrderService;
import cn.phone.repair.module.system.api.dept.DeptApi;
import cn.phone.repair.module.system.api.dept.dto.DeptRespDTO;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import cn.phone.repair.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 维修单")
@RestController
@RequestMapping("/repair/repair-order")
@Validated
public class RepairOrderController {

    @Resource
    private RepairOrderService repairOrderService;

    @Resource
    private CustomerService customerService;

    @Resource
    private DeptApi deptApi;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建维修单")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:create')")
    public CommonResult<Long> createRepairOrder(@Valid @RequestBody RepairOrderSaveReqVO createReqVO) {
        return success(repairOrderService.createRepairOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新维修单")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:update')")
    public CommonResult<Boolean> updateRepairOrder(@Valid @RequestBody RepairOrderSaveReqVO updateReqVO) {
        repairOrderService.updateRepairOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除维修单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('repair:repair-order:delete')")
    public CommonResult<Boolean> deleteRepairOrder(@RequestParam("id") Long id) {
        repairOrderService.deleteRepairOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取维修单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:query')")
    public CommonResult<RepairOrderRespVO> getRepairOrder(@RequestParam("id") Long id) {
        RepairOrderRespVO respVO = repairOrderService.getRepairOrderWithDetails(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获取维修单分页列表")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:query')")
    public CommonResult<PageResult<RepairOrderRespVO>> getRepairOrderPage(@Valid RepairOrderPageReqVO reqVO) {
        PageResult<RepairOrderRespVO> pageResult = repairOrderService.getRepairOrderPageWithDetails(reqVO);
        return success(pageResult);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新维修单状态")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('repair:repair-order:update')")
    public CommonResult<Boolean> updateRepairOrderStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status,
            @RequestParam("statusText") String statusText) {
        repairOrderService.updateRepairOrderStatus(id, status, statusText);
        return success(true);
    }

    @PutMapping("/update-cost")
    @Operation(summary = "更新维修费用")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "cost", description = "维修费用", required = true)
    @PreAuthorize("@ss.hasPermission('repair:repair-order:update')")
    public CommonResult<Boolean> updateRepairOrderCost(
            @RequestParam("id") Long id,
            @RequestParam("cost") BigDecimal cost) {
        repairOrderService.updateRepairOrderCost(id, cost);
        return success(true);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出维修单 Excel")
    @PreAuthorize("@ss.hasPermission('repair:repair-order:export')")
    public void exportRepairOrderExcel(@Valid RepairOrderPageReqVO reqVO, HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RepairOrderRespVO> list = repairOrderService.getRepairOrderPageWithDetails(reqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "维修单.xls", "数据", RepairOrderRespVO.class, list);
    }
}

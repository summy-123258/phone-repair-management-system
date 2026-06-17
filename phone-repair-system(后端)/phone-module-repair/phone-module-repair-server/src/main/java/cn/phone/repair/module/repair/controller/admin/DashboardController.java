package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardAccessoryStockRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardStatsRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTrendRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTypeDistributionRespVO;
import cn.phone.repair.module.repair.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理后台 - 仪表盘")
@RestController
@RequestMapping("/repair/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public CommonResult<DashboardStatsRespVO> getStats() {
        return CommonResult.success(dashboardService.getStats());
    }

    @Operation(summary = "获取维修订单趋势")
    @GetMapping("/trend")
    public CommonResult<DashboardTrendRespVO> getTrend() {
        return CommonResult.success(dashboardService.getTrend());
    }

}

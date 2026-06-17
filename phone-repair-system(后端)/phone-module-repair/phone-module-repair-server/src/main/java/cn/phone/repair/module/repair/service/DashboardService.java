package cn.phone.repair.module.repair.service;



import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardAccessoryStockRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardStatsRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTrendRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTypeDistributionRespVO;

import java.util.List;

public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsRespVO getStats();

    /**
     * 获取维修订单趋势
     */
    DashboardTrendRespVO getTrend();
}

package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardAccessoryStockRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardStatsRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTrendRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.dashboard.DashboardTypeDistributionRespVO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDeptStockDO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import cn.phone.repair.module.repair.dal.mysql.AccessoryDeptStockMapper;
import cn.phone.repair.module.repair.dal.mysql.CustomerMapper;
import cn.phone.repair.module.repair.dal.mysql.RepairOrderMapper;
import cn.phone.repair.module.repair.service.DashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Resource
    private AccessoryDeptStockMapper accessoryDeptStockMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public DashboardStatsRespVO getStats() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();

        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();

        DashboardStatsRespVO stats = new DashboardStatsRespVO();

        long orderCount = repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrderDO>()
                .eq(RepairOrderDO::getDeptId, deptId)
                .ge(RepairOrderDO::getCreateTime, monthStart)
                .lt(RepairOrderDO::getCreateTime, monthEnd));
        stats.setOrderCount((int) orderCount);

        List<RepairOrderDO> orders = repairOrderMapper.selectList(new LambdaQueryWrapper<RepairOrderDO>()
                .eq(RepairOrderDO::getDeptId, deptId)
                .ge(RepairOrderDO::getCreateTime, monthStart)
                .lt(RepairOrderDO::getCreateTime, monthEnd));

        Set<Long> customerIds = orders.stream()
                .map(RepairOrderDO::getCustomerId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        stats.setCustomerCount(customerIds.size());

        BigDecimal totalAmount = orders.stream()
                .map(RepairOrderDO::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount.intValue());

        return stats;
    }


    @Override
    public DashboardTrendRespVO getTrend() {
        LocalDate now = LocalDate.now();
        LocalDate lastWeekStart = now.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate lastWeekEnd = lastWeekStart.plusDays(7);

        LocalDateTime startDate = lastWeekStart.atStartOfDay();
        LocalDateTime endDate = lastWeekEnd.atStartOfDay();

        Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();

        List<RepairOrderDO> orders = repairOrderMapper.selectList(new LambdaQueryWrapper<RepairOrderDO>()
                .eq(RepairOrderDO::getDeptId, deptId)
                .ge(RepairOrderDO::getSubmitTime, startDate)
                .lt(RepairOrderDO::getSubmitTime, endDate)
                .orderByAsc(RepairOrderDO::getSubmitTime));

        Map<String, Long> orderCountByDay = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getSubmitTime().toLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                        Collectors.counting()
                ));

        List<Integer> actualData = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            String date = lastWeekStart.plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd"));
            long count = orderCountByDay.getOrDefault(date, 0L);
            actualData.add((int) count);
        }

        DashboardTrendRespVO trend = new DashboardTrendRespVO();
        trend.setActualData(actualData);

        return trend;
    }

    private DashboardTypeDistributionRespVO createTypeDistribution(String name, int value) {
        DashboardTypeDistributionRespVO vo = new DashboardTypeDistributionRespVO();
        vo.setName(name);
        vo.setValue(value);
        return vo;
    }
}

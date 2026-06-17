package cn.phone.repair.module.repair.controller.admin.vo.dashboard;

import lombok.Data;

@Data
public class DashboardStatsRespVO {

    /**
     * 客户数量
     */
    private Integer customerCount;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 总金额
     */
    private Integer totalAmount;

    /**
     * 订单数量
     */
    private Integer orderCount;
}

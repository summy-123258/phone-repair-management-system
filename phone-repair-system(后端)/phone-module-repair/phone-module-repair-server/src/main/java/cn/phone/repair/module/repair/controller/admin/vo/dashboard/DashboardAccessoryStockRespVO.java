package cn.phone.repair.module.repair.controller.admin.vo.dashboard;

import lombok.Data;

@Data
public class DashboardAccessoryStockRespVO {

    /**
     * 配件名称
     */
    private String name;

    /**
     * 可用库存
     */
    private Integer availableStock;

    /**
     * 已锁定库存
     */
    private Integer lockedStock;
}

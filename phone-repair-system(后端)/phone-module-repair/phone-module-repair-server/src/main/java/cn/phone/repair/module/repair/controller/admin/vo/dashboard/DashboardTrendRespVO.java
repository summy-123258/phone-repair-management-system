package cn.phone.repair.module.repair.controller.admin.vo.dashboard;

import lombok.Data;

import java.util.List;

@Data
public class DashboardTrendRespVO {

    /**
     * 预计数据
     */
    private List<Integer> expectedData;

    /**
     * 实际数据
     */
    private List<Integer> actualData;
}

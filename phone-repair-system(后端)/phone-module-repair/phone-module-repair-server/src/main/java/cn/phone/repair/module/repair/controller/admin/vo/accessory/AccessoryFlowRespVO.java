package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessoryFlowRespVO {

    private Long id;

    private Long accessoryId;

    private String accessoryName;

    private String accessorySpec;

    private String repairOrderNo;

    private String type;

    private Integer quantity;

    private Long operatorId;

    private String operator;

    private String deptName;

    private String reason;

    private String remark;

    private LocalDateTime createTime;
}

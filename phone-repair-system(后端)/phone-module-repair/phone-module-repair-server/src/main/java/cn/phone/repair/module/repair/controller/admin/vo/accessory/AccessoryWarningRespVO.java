package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessoryWarningRespVO {

    private Long id;

    private Long accessoryId;

    private String accessoryName;

    private String accessorySpec;

    private String level;

    private Integer currentStock;

    private Integer safeStock;

    private String status;

    private Long deptId;

    private String deptName;

    private LocalDateTime lastNotifyTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccessoryReturnSaveReqVO {

    private Long id;

    @NotNull(message = "维修单号不能为空")
    private String repairOrderNo;

    @NotNull(message = "配件ID不能为空")
    private Long accessoryId;

    @NotNull(message = "退库数量不能为空")
    private Integer quantity;

    @NotNull(message = "退库原因不能为空")
    private String reason;

    private String reasonDetail;

    private Boolean isQualityIssue;

    private String remark;
}

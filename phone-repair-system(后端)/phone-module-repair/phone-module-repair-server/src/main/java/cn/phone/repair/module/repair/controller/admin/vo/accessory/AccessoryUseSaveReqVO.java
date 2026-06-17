package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccessoryUseSaveReqVO {

    private Long id;

    @NotNull(message = "维修单号不能为空")
    private String repairOrderNo;

    @NotNull(message = "配件ID不能为空")
    private Long accessoryId;

    @NotNull(message = "领用数量不能为空")
    private Integer quantity;

    @Schema(description = "领用原因")
    private String reason;

    private String remark;
}

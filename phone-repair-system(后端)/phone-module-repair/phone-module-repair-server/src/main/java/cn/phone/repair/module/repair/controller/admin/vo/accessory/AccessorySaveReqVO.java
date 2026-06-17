package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 配件创建/修改 Request VO")
@Data
public class AccessorySaveReqVO {

    @Schema(description = "配件ID", example = "1")
    private Long id;

    @Schema(description = "门店ID（管理员新增时必填）", example = "101")
    private Long deptId;

    @Schema(description = "配件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 13 电池")
    @NotBlank(message = "配件名称不能为空")
    private String name;

    @Schema(description = "规格型号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A2650")
    @NotBlank(message = "规格型号不能为空")
    private String spec;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "个")
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "安全库存阈值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "安全库存不能为空")
    private Integer safeStock;

    @Schema(description = "最大库存上限", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    @NotNull(message = "最大库存不能为空")
    private Integer maxStock;

    @Schema(description = "总库存", example = "30")
    private Integer totalStock;

    @Schema(description = "可用库存", example = "25")
    private Integer availableStock;

    @Schema(description = "供应商", example = "苹果供应商")
    private String supplier;

    @Schema(description = "备注", example = "iPhone 13 原装电池")
    private String remark;

}
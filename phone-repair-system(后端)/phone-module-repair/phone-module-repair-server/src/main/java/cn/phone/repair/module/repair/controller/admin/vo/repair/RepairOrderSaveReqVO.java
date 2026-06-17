package cn.phone.repair.module.repair.controller.admin.vo.repair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.phone.repair.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 维修单创建/修改 Request VO")
@Data
public class RepairOrderSaveReqVO {

    @Schema(description = "维修单ID", example = "1")
    private Long id;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "客户姓名", example = "张三")
    private String name;

    @Schema(description = "客户手机号", example = "13800138000")
    private String phone;

    @Schema(description = "手机型号", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 14 Pro")
    @NotNull(message = "手机型号不能为空")
    private String phoneModel;

    @Schema(description = "故障描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "屏幕碎裂")
    @NotNull(message = "故障描述不能为空")
    private String faultDescription;

    @Schema(description = "状态", example = "submitted")
    private String status;

    @Schema(description = "状态文本", example = "已提交")
    private String statusText;

    @Schema(description = "维修费用", example = "1299.00")
    private BigDecimal cost;

    @Schema(description = "提交时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTime;

    @Schema(description = "预计完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime estimatedCompletionTime;

    @Schema(description = "实际完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime actualCompletionTime;

    @Schema(description = "配送方式", example = "pickup")
    private String deliveryOption;

    @Schema(description = "自提码", example = "123456")
    private String pickupCode;

    @Schema(description = "部门ID（门店ID）", example = "1")
    private Long deptId;

    @Schema(description = "负责员工ID", example = "1")
    private Long staffId;

    @Schema(description = "维修人员姓名", example = "李四")
    private String staffName;
}

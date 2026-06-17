package cn.phone.repair.module.repair.controller.admin.vo.repair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.phone.repair.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 维修进度创建/修改 Request VO")
@Data
public class RepairProgressSaveReqVO {

    @Schema(description = "进度ID", example = "1")
    private Long id;

    @Schema(description = "维修单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "维修单ID不能为空")
    private Long orderId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "accepted")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "状态文本", requiredMode = Schema.RequiredMode.REQUIRED, example = "已受理")
    @NotNull(message = "状态文本不能为空")
    private String statusText;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "维修中心受理维修申请")
    @NotNull(message = "描述不能为空")
    private String description;

    @Schema(description = "时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime time;
}

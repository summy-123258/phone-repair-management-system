package cn.phone.repair.module.repair.controller.admin.vo.repair;

import cn.phone.repair.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.phone.repair.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 维修单分页 Request VO")
@Data
public class RepairOrderPageReqVO extends PageParam {

    @Schema(description = "维修单号", example = "WX20260210001")
    private String orderNo;

    @Schema(description = "手机型号", example = "iPhone 14 Pro")
    private String phoneModel;

    @Schema(description = "状态", example = "submitted")
    private String status;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "客户名称", example = "1")
    private String name;

    @Schema(description = "负责员工ID", example = "1")
    private Long staffId;

    @Schema(description = "维修人员名称", example = "1")
    private String staffName;

    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    @Schema(description = "提交时间开始")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeBegin;

    @Schema(description = "提交时间结束")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeEnd;
}

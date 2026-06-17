package cn.phone.repair.module.repair.controller.admin.vo.repair;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 维修单详情 Response VO")
@Data
public class RepairOrderRespVO {

    @Schema(description = "维修单ID", example = "1")
    @ExcelProperty("维修单ID")
    private Long id;

    @Schema(description = "维修单号", example = "WX20260210001")
    @ExcelProperty("维修单号")
    private String orderNo;

    @Schema(description = "客户ID", example = "1")
    @ExcelProperty("客户ID")
    private Long customerId;

    @Schema(description = "客户姓名", example = "张三")
    @ExcelProperty("客户姓名")
    private String name;

    @Schema(description = "客户手机号", example = "13800138000")
    @ExcelProperty("客户手机号")
    private String phone;

    @Schema(description = "手机型号", example = "iPhone 14 Pro")
    @ExcelProperty("手机型号")
    private String phoneModel;

    @Schema(description = "故障描述", example = "屏幕碎裂")
    @ExcelProperty("故障描述")
    private String faultDescription;

    @Schema(description = "状态", example = "submitted")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "状态文本", example = "已提交")
    @ExcelProperty("状态文本")
    private String statusText;

    @Schema(description = "维修费用", example = "1299.00")
    @ExcelProperty("维修费用")
    private BigDecimal cost;

    @Schema(description = "提交时间")
    @ExcelProperty("提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "预计完成时间")
    @ExcelProperty("预计完成时间")
    private LocalDateTime estimatedCompletionTime;

    @Schema(description = "实际完成时间")
    @ExcelProperty("实际完成时间")
    private LocalDateTime actualCompletionTime;

    @Schema(description = "配送方式", example = "pickup")
    @ExcelProperty("配送方式")
    private String deliveryOption;

    @Schema(description = "自提码", example = "123456")
    @ExcelProperty("自提码")
    private String pickupCode;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;


    @Schema(description = "负责员工ID", example = "1")
    @ExcelProperty("负责员工ID")
    private Long staffId;

    @Schema(description = "负责员工姓名", example = "李师傅")
    @ExcelProperty("负责员工姓名")
    private String staffName;

    @Schema(description = "部门ID（门店ID）", example = "1")
    @ExcelProperty("门店ID")
    private Long deptId;

    @Schema(description = "门店名称", example = "北京朝阳区门店")
    @ExcelProperty("门店名称")
    private String storeName;

}


package cn.phone.repair.module.repair.enums;

import cn.phone.repair.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode REPAIR_ORDER_NOT_EXISTS = new ErrorCode(1_003_000_001, "维修单不存在");
    ErrorCode REPAIR_ORDER_STATUS_ERROR = new ErrorCode(1_003_000_002, "维修单状态错误");
    ErrorCode APPOINTMENT_NOT_EXISTS = new ErrorCode(1_003_000_003, "预约单不存在");
    ErrorCode STORE_NOT_EXISTS = new ErrorCode(1_003_000_004, "门店不存在");
    ErrorCode PROGRESS_NOT_EXISTS = new ErrorCode(1_003_000_005, "维修进度不存在");

    // 门店相关错误码
    ErrorCode STORE_CODE_DUPLICATE = new ErrorCode(1_003_000_006, "门店编码已存在");
    ErrorCode STORE_STAFF_NOT_EXISTS = new ErrorCode(1_003_000_007, "门店员工不存在");
    ErrorCode STORE_INVENTORY_NOT_EXISTS = new ErrorCode(1_003_000_008, "门店库存不存在");
    ErrorCode PART_CATEGORY_NOT_EXISTS = new ErrorCode(1_003_000_009, "配件分类不存在");
    ErrorCode PART_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_003_000_010, "配件分类名称已存在");
    ErrorCode STORE_INVENTORY_QUANTITY_INSUFFICIENT = new ErrorCode(1_003_000_011, "门店库存数量不足");
}

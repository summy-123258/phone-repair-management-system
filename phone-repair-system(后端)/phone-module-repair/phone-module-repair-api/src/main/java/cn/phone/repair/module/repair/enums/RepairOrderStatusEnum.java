package cn.phone.repair.module.repair.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepairOrderStatusEnum {

    ACCEPTED("accepted", "已受理"),
    DIAGNOSED("diagnosed", "诊断中"),
    DIAGNOSED_DONE("diagnosed_done", "已诊断"),
    REPAIRING("repairing", "维修中"),
    REPAIR_DONE("repair_done", "已维修"),
    COMPLETED("completed", "已完成");

    private final String status;
    private final String name;

    public static String getNameByStatus(String status) {
        for (RepairOrderStatusEnum anEnum : values()) {
            if (anEnum.getStatus().equals(status)) {
                return anEnum.getName();
            }
        }
        return null;
    }
}

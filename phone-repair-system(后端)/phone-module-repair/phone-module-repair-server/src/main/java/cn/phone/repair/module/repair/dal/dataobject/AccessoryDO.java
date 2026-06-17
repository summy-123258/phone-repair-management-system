package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 配件基本信息数据模型
 * 存储配件的基本信息，如名称、规格、单位、安全库存阈值等
 */
@TableName("accessory")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class AccessoryDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 配件名称
     */
    private String name;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 安全库存阈值
     */
    private Integer safeStock;

    /**
     * 最大库存上限
     */
    private Integer maxStock;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 备注
     */
    private String remark;
}

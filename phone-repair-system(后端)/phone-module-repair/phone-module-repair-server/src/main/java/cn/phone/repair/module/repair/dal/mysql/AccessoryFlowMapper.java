package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryFlowDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 配件流水数据访问接口
 * 提供配件流水的CRUD操作和查询功能
 */
@Mapper
public interface AccessoryFlowMapper extends BaseMapperX<AccessoryFlowDO> {

    /**
     * 根据维修单号查询流水
     * @param repairOrderNo 维修单号
     * @return 流水列表
     */
    List<AccessoryFlowDO> selectListByRepairOrderNo(String repairOrderNo);

    /**
     * 根据配件ID查询流水
     * @param accessoryId 配件ID
     * @return 流水列表
     */
    List<AccessoryFlowDO> selectListByAccessoryId(Long accessoryId);

    /**
     * 根据操作类型查询流水
     * @param type 操作类型
     * @return 流水列表
     */
    List<AccessoryFlowDO> selectListByType(String type);
}

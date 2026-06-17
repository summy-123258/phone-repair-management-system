package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDeptStockDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccessoryDeptStockMapper extends BaseMapperX<AccessoryDeptStockDO> {

    void updateByAccessoryIdAndDeptId(AccessoryDeptStockDO stock);

    AccessoryDeptStockDO selectByAccessoryIdAndDeptId(@Param("accessoryId") Long accessoryId,
                                                      @Param("deptId") Long deptId);

    List<AccessoryDeptStockDO> selectListByAccessoryId(Long accessoryId);

    List<AccessoryDeptStockDO> selectListByDeptId(Long deptId);
}
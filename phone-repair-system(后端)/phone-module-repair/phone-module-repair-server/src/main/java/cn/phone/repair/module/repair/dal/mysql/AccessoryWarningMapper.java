package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryWarningDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 配件预警数据访问接口
 * 提供配件预警的CRUD操作和查询功能
 */
@Mapper
public interface AccessoryWarningMapper extends BaseMapperX<AccessoryWarningDO> {

    /**
     * 根据配件ID更新预警
     * @param warning 预警信息
     */
    void updateByAccessoryId(AccessoryWarningDO warning);

    /**
     * 根据配件ID查询预警
     * @param accessoryId 配件ID
     * @return 预警信息
     */
    AccessoryWarningDO selectByAccessoryId(Long accessoryId);

    /**
     * 根据状态查询预警
     * @param status 状态
     * @return 预警列表
     */
    List<AccessoryWarningDO> selectListByStatus(String status);
}

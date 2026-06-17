package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.datapermission.core.annotation.DataPermission;
import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderPageReqVO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepairOrderMapper extends BaseMapperX<RepairOrderDO> {
    @DataPermission
    default PageResult<RepairOrderDO> selectPage(RepairOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RepairOrderDO>()
                .likeIfPresent(RepairOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(RepairOrderDO::getPhoneModel, reqVO.getPhoneModel())
                .eqIfPresent(RepairOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(RepairOrderDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(RepairOrderDO::getStaffId, reqVO.getStaffId())
                .betweenIfPresent(RepairOrderDO::getSubmitTime, reqVO.getSubmitTimeBegin(), reqVO.getSubmitTimeEnd())
                .orderByDesc(RepairOrderDO::getId));
    }
}

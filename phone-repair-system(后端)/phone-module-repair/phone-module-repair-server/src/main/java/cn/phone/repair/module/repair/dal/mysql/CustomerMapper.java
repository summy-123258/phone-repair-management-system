package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.CustomerDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapperX<CustomerDO> {

    default CustomerDO selectByMobile(String mobile) {
        return selectOne(CustomerDO::getMobile, mobile);
    }

}

package cn.phone.repair.module.repair.service;

import cn.phone.repair.module.repair.dal.dataobject.CustomerDO;

import java.util.List;

public interface CustomerService {

    /**
     * 根据手机号获取客户信息，如果不存在则创建
     * @param mobile 客户手机号
     * @param name 客户姓名
     * @return 客户信息
     */
    CustomerDO getOrCreateCustomer(String mobile, String name);

    /**
     * 根据ID获取客户信息
     * @param id 客户ID
     * @return 客户信息
     */
    CustomerDO getCustomerById(Long id);

    List<CustomerDO> searchCustomersByName(String name);

}

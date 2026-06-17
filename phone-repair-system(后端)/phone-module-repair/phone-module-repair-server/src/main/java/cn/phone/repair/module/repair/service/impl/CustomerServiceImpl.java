package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.phone.repair.framework.security.core.LoginUser;
import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.dal.dataobject.CustomerDO;
import cn.phone.repair.module.repair.dal.mysql.CustomerMapper;
import cn.phone.repair.module.repair.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public CustomerDO getOrCreateCustomer(String mobile, String name) {
        // 根据手机号查询客户
        CustomerDO existingCustomer = customerMapper.selectByMobile(mobile);
        if (existingCustomer != null) {
            return existingCustomer;
        }

        // 创建新客户
        CustomerDO newCustomer = new CustomerDO();
        newCustomer.setName(name != null ? name : "客户");
        newCustomer.setMobile(mobile);
        
        // 设置创建者和更新者
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            newCustomer.setCreator(String.valueOf(loginUser.getId()));
            newCustomer.setUpdater(String.valueOf(loginUser.getId()));
        }

        // 保存客户信息
        customerMapper.insert(newCustomer);
        log.info("自动创建客户成功, mobile={}, customerId={}", mobile, newCustomer.getId());
        return newCustomer;
    }

    @Override
    public CustomerDO getCustomerById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public List<CustomerDO> searchCustomersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return customerMapper.selectList(new LambdaQueryWrapperX<CustomerDO>()
                .like(CustomerDO::getName, name));
    }

}

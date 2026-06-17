package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.RepairProgressDO;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepairProgressMapper extends BaseMapperX<RepairProgressDO> {
    default List<RepairProgressDO> selectListByField(SFunction<RepairProgressDO, ?> field, Object value) {
        return selectList(field, value);
    }
}

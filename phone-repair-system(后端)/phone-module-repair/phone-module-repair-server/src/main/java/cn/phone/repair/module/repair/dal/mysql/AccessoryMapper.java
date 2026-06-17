package cn.phone.repair.module.repair.dal.mysql;

import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccessoryMapper extends BaseMapperX<AccessoryDO> {

    List<AccessoryDO> selectList();

    List<AccessoryDO> selectListByKeyword(String keyword);

    List<AccessoryDO> selectListPage(@Param("offset") int offset, @Param("limit") int limit);

    int selectCountByKeyword(@Param("keyword") String keyword);

    List<AccessoryDO> selectListByKeywordPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
}

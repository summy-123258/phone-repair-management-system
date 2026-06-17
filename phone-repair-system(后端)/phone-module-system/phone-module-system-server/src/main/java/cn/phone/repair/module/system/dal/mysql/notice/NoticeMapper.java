package cn.phone.repair.module.system.dal.mysql.notice;

import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.mybatis.core.mapper.BaseMapperX;
import cn.phone.repair.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.phone.repair.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.phone.repair.module.system.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId));
    }

}

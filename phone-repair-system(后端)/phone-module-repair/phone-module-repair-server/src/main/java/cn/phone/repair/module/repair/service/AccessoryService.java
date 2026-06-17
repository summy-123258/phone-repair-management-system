package cn.phone.repair.module.repair.service;

import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessorySaveReqVO;

import java.util.List;

/**
 * 配件服务接口
 * 提供配件的管理、库存操作和预警检查功能
 */
public interface AccessoryService {

    /**
     * 创建配件
     * @param createReqVO 配件信息
     * @return 配件ID
     */
    Long createAccessory(AccessorySaveReqVO createReqVO);

    /**
     * 更新配件
     * @param createReqVO 配件信息
     */
    void updateAccessory(AccessorySaveReqVO createReqVO);

    /**
     * 删除配件
     * @param id 配件ID
     */
    void deleteAccessory(Long id);

    /**
     * 根据ID获取配件详情
     * @param id 配件ID
     * @return 配件详情
     */
    AccessoryRespVO getAccessoryById(Long id);

    /**
     * 获取配件列表
     * @return 配件列表
     */
    List<AccessoryRespVO> getAccessoryList();

    /**
     * 分页获取配件列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页配件列表
     */
    PageResult<AccessoryRespVO> getAccessoryListPage(Integer pageNum, Integer pageSize);

    /**
     * 根据关键词搜索配件
     * @param keyword 关键词
     * @return 配件列表
     */
    List<AccessoryRespVO> getAccessoryListByKeyword(String keyword);

    /**
     * 分页根据关键词搜索配件
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页配件列表
     */
    PageResult<AccessoryRespVO> getAccessoryListByKeywordPage(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取配件库存
     * @param accessoryId 配件ID
     * @return 库存信息
     */
//    AccessoryStockDO getAccessoryStock(Long accessoryId);

    /**
     * 更新配件库存
     * @param accessoryId 配件ID
     * @param quantity 数量
     * @param isIncrease 是否增加
     */
    void updateAccessoryStock(Long accessoryId, Integer quantity, boolean isIncrease, Long deptId);

    /**
     * 检查并更新预警状态
     * @param accessoryId 配件ID
     */
    void checkAndUpdateWarningStatus(Long accessoryId, Long deptId);

    /**
     * 配件补货
     * @param accessoryId 配件ID
     * @param quantity 补货数量
     */
    void replenishAccessory(Long accessoryId, Integer quantity, Long deptId);
}

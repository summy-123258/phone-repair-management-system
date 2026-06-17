package cn.phone.repair.module.repair.controller.admin;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessorySaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDO;
import cn.phone.repair.module.repair.service.AccessoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 配件管理")
@RestController
@RequestMapping("/repair/accessory")
@Validated
public class AccessoryController {

    @Resource
    private AccessoryService accessoryService;

    @PostMapping("/create")
    @Operation(summary = "创建配件")
    @PreAuthorize("@ss.hasPermission('repair:accessory:create')")
    public CommonResult<Long> createAccessory(@Valid @RequestBody AccessorySaveReqVO createReqVO) {
        return success(accessoryService.createAccessory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新配件")
    @PreAuthorize("@ss.hasPermission('repair:accessory:update')")
    public CommonResult<Boolean> updateAccessory(@Valid @RequestBody AccessorySaveReqVO createReqVO) {
        accessoryService.updateAccessory(createReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除配件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:delete')")
    public CommonResult<Boolean> deleteAccessory(@RequestParam("id") Long id) {
        accessoryService.deleteAccessory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取配件详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<AccessoryRespVO> getAccessory(@RequestParam("id") Long id) {
        return success(accessoryService.getAccessoryById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取配件列表")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryRespVO>> getAccessoryList() {
        return success(accessoryService.getAccessoryList());
    }

    @GetMapping("/list/page")
    @Operation(summary = "分页获取配件列表")
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页大小", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<PageResult<AccessoryRespVO>> getAccessoryListPage(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        return success(accessoryService.getAccessoryListPage(pageNum, pageSize));
    }

    @GetMapping("/list-by-keyword")
    @Operation(summary = "根据关键词搜索配件")
    @Parameter(name = "keyword", description = "关键词", required = true)
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<List<AccessoryRespVO>> getAccessoryListByKeyword(@RequestParam("keyword") String keyword) {
        return success(accessoryService.getAccessoryListByKeyword(keyword));
    }

    @GetMapping("/list-by-keyword/page")
    @Operation(summary = "分页根据关键词搜索配件")
    @Parameter(name = "keyword", description = "关键词", required = true)
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页大小", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('repair:accessory:query')")
    public CommonResult<PageResult<AccessoryRespVO>> getAccessoryListByKeywordPage(
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        return success(accessoryService.getAccessoryListByKeywordPage(keyword, pageNum, pageSize));
    }

    @PostMapping("/replenish")
    @Operation(summary = "配件补货")
    @Parameter(name = "id", description = "配件ID", required = true)
    @Parameter(name = "quantity", description = "补货数量", required = true)
    @Parameter(name = "deptId", description = "门店ID（管理员必填）")
    @PreAuthorize("@ss.hasPermission('repair:accessory:update')")
    public CommonResult<Boolean> replenishAccessory(
            @RequestParam("id") Long id,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "deptId", required = false) Long deptId) {
        accessoryService.replenishAccessory(id, quantity, deptId);
        return success(true);
    }
}

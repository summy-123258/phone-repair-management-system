<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="预警状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择预警状态" clearable style="width: 180px">
          <el-option label="活跃" value="active" />
          <el-option label="已解决" value="resolved" />
        </el-select>
      </el-form-item>
      <el-form-item style="margin-top: 0 !important; margin-bottom: 0;">
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="checkAllWarnings"
                   v-hasPermi="['repair:accessory:warning:check']">检查预警</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="warningList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配件名称" align="center" prop="accessoryName" v-if="columns[0].visible" width="140" />
      <el-table-column label="所属门店" align="center" prop="deptName" width="120" />
      <el-table-column label="预警级别" align="center" prop="level" v-if="columns[2].visible" width="100">
        <template v-slot="scope">
          <el-tag :type="getLevelType(scope.row.level)">
            {{ getLevelText(scope.row.level) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前库存" align="center" prop="currentStock" v-if="columns[3].visible" width="100" />
      <el-table-column label="安全库存" align="center" prop="safeStock" v-if="columns[4].visible" width="100" />
      <el-table-column label="预警时间" align="center" prop="createTime" v-if="columns[5].visible" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" v-if="columns[6].visible" width="100">
        <template v-slot="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleProcess(scope.row)"
                     v-if="scope.row.status === 'active'">处理</el-button>
<!--          <el-button size="mini" type="text" icon="el-icon-close" @click="handleIgnore(scope.row)"-->
<!--                     v-if="scope.row.status === 'active'">忽略</el-button>-->
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看预警详情对话框 -->
    <el-dialog title="预警详情" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="配件名称">{{ viewData.accessoryName }}</el-descriptions-item>
        <el-descriptions-item label="所属门店">{{ viewData.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预警级别">{{ getLevelText(viewData.level) }}</el-descriptions-item>
        <el-descriptions-item label="当前库存">{{ viewData.currentStock }}</el-descriptions-item>
        <el-descriptions-item label="安全库存">{{ viewData.safeStock }}</el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ parseTime(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(viewData.status) }}</el-descriptions-item>
        <el-descriptions-item label="最后通知时间">{{ viewData.lastNotifyTime ? parseTime(viewData.lastNotifyTime) : '-' }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 配件补货对话框 -->
    <el-dialog title="配件补货" :visible.sync="replenishOpen" width="400px" append-to-body>
      <el-form ref="replenishForm" :model="replenishForm" :rules="replenishRules" label-width="100px">
        <el-form-item label="配件名称">
          <span>{{ replenishForm.name }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ replenishForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="安全库存">
          <span>{{ replenishForm.safeStock }}</span>
        </el-form-item>
        <el-form-item label="补货数量" prop="quantity">
          <el-input-number v-model="replenishForm.quantity" :min="1" placeholder="请输入补货数量" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReplenish">确 定</el-button>
        <el-button @click="cancelReplenish">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccessoryWarning, listAccessoryWarningByStatus, updateWarningStatus, checkAllAccessoryWarnings, replenishAccessory } from '@/api/repair/accessory'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AccessoryWarning',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      warningList: [],
      viewOpen: false,
      viewData: {},
      // 补货相关
      replenishOpen: false,
      replenishForm: {
        id: undefined,
        name: '',
        currentStock: 0,
        safeStock: 0,
        quantity: 1
      },
      replenishRules: {
        quantity: [{ required: true, message: '补货数量不能为空', trigger: 'blur' }]
      },
      columns: [
        { key: 0, label: '配件名称', visible: true },
        { key: 1, label: '所属门店', visible: true },
        { key: 2, label: '预警级别', visible: true },
        { key: 3, label: '当前库存', visible: true },
        { key: 4, label: '安全库存', visible: true },
        { key: 5, label: '预警时间', visible: true },
        { key: 6, label: '状态', visible: true }
      ],
      queryParams: {
        status: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    /** 查询预警列表 */
    getList() {
      this.loading = true
      if (this.queryParams.status) {
        listAccessoryWarningByStatus(this.queryParams.status).then(response => {
          this.warningList = response.data
          this.loading = false
        })
      } else {
        listAccessoryWarning().then(response => {
          this.warningList = response.data
          this.loading = false
        })
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.getList()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    /** 处理预警（弹出补货对话框） */
    handleProcess(row) {
      this.replenishForm = {
        id: row.accessoryId,
        name: row.accessoryName,
        currentStock: row.currentStock,
        safeStock: row.safeStock,
        quantity: 1
      }
      this.replenishOpen = true
    },
    /** 忽略预警 */
    handleIgnore(row) {
      this.$modal.confirm('是否确认忽略该预警？').then(() => {
        return updateWarningStatus(row.id, 'resolved')
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('忽略成功')
      }).catch(() => {})
    },
    /** 提交补货表单 */
    submitReplenish() {
      this.$refs.replenishForm.validate(valid => {
        if (valid) {
          replenishAccessory(this.replenishForm).then(() => {
            this.$modal.msgSuccess('补货成功')
            this.replenishOpen = false
            // 补货成功后直接调用API检查预警状态
            checkAllAccessoryWarnings().then(() => {
              this.getList()
            })
          })
        }
      })
    },
    /** 取消补货 */
    cancelReplenish() {
      this.replenishOpen = false
      this.replenishForm = {
        id: undefined,
        name: '',
        currentStock: 0,
        safeStock: 0,
        quantity: 1
      }
      this.resetForm('replenishForm')
    },
    /** 检查所有预警 */
    checkAllWarnings() {
      this.$modal.confirm('是否确认检查所有配件的预警状态？').then(() => {
        return checkAllAccessoryWarnings()
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('检查完成')
      }).catch(() => {})
    },
    /** 获取预警级别类型 */
    getLevelType(level) {
      switch (level) {
        case 'severe': return 'danger'
        case 'warning': return 'warning'
        case 'normal': return 'success'
        default: return 'default'
      }
    },
    /** 获取预警级别文本 */
    getLevelText(level) {
      switch (level) {
        case 'severe': return '缺货'
        case 'warning': return '库存不足'
        case 'normal': return '正常'
        default: return '未知'
      }
    },
    /** 获取状态类型 */
    getStatusType(status) {
      switch (status) {
        case 'active': return 'warning'
        case 'resolved': return 'success'
        default: return 'default'
      }
    },
    /** 获取状态文本 */
    getStatusText(status) {
      switch (status) {
        case 'active': return '活跃'
        case 'resolved': return '已解决'
        default: return '未知'
      }
    }
  }
}
</script>

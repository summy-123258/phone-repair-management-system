<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="维修单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入维修单号" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="客户" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入客户姓名" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="维修人员" prop="staffName">
        <el-input v-model="queryParams.staffName" placeholder="请输入维修人员姓名" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item style="margin-top: 0 !important; margin-bottom: 0;">
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['repair:repair-order:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['repair:repair-order:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="repairOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="维修单号" align="center" prop="orderNo" v-if="columns[0].visible" width="160" />
      <el-table-column label="客户" align="center" prop="name" v-if="columns[1].visible" width="100">
        <template v-slot="scope">
          <span>{{ scope.row.name || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="门店" align="center" prop="storeName" v-if="isAdmin && columns[2].visible" width="140" />
      <el-table-column label="维修人员" align="center" prop="staffName" v-if="columns[3].visible" width="100">
        <template v-slot="scope">
          <span>{{ scope.row.staffName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机型号" align="center" prop="phoneModel" v-if="columns[4].visible" width="140" />
      <el-table-column label="故障描述" align="center" prop="faultDescription" v-if="columns[5].visible" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" prop="status" v-if="columns[6].visible" width="100">
        <template v-slot="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维修费用" align="center" prop="cost" v-if="columns[7].visible" width="100">
        <template v-slot="scope">
          <span v-if="scope.row.status === 'completed' && scope.row.cost">¥{{ scope.row.cost }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="配送方式" align="center" prop="deliveryOption" v-if="columns[8].visible" width="120">
        <template v-slot="scope">
          <span>到店自提</span>
        </template>
      </el-table-column>
      <el-table-column label="自提码" align="center" prop="pickupCode" v-if="columns[9].visible" width="100" />
      <el-table-column label="提交时间" align="center" prop="submitTime" v-if="columns[10].visible" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.submitTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预计完成时间" align="center" prop="estimatedCompletionTime" v-if="columns[11].visible" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.estimatedCompletionTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['repair:repair-order:update']">修改</el-button>
          <el-dropdown @command="(command) => handleCommand(command, scope.row)"
                       v-hasPermi="['repair:repair-order:update']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">状态变更</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleStatus" v-if="scope.row.status === 'accepted'" size="mini" type="text" icon="el-icon-view"
                                v-hasPermi="['repair:repair-order:update']">诊断</el-dropdown-item>
              <el-dropdown-item command="handleDiagnose" v-if="scope.row.status === 'diagnosed'" size="mini" type="text" icon="el-icon-check"
                                v-hasPermi="['repair:repair-order:update']">完成诊断</el-dropdown-item>
              <el-dropdown-item command="handleRepair" v-if="scope.row.status === 'diagnosed_done'" size="mini" type="text" icon="el-icon-tools"
                                v-hasPermi="['repair:repair-order:update']">开始维修</el-dropdown-item>
              <el-dropdown-item command="handleComplete" v-if="scope.row.status === 'repairing'" size="mini" type="text" icon="el-icon-circle-check"
                                v-hasPermi="['repair:repair-order:update']">完成</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['repair:repair-order:delete']" style="margin-left: 10px;">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 添加或修改维修单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入客户姓名" :disabled="isRepairStaff && form.id !== undefined" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入客户手机号" :disabled="isRepairStaff && form.id !== undefined" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" v-if="isStoreManager || isAdmin">
            <el-form-item label="维修人员" prop="staffId">
              <el-select v-model="form.staffId" placeholder="请选择维修人员" clearable style="width: 100%">
                <el-option v-for="staff in staffList" :key="staff.id" :label="staff.nickname" :value="staff.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-else>
            <el-form-item label="维修人员" prop="staffId">
              <el-input v-model="form.staffName" placeholder="当前登录用户" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机型号" prop="phoneModel">
              <el-input v-model="form.phoneModel" placeholder="请输入手机型号" :disabled="isRepairStaff && form.id !== undefined" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="故障描述" prop="faultDescription">
              <el-input v-model="form.faultDescription" type="textarea" :rows="3" placeholder="请输入故障描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" v-if="form.status === 'completed'">
            <el-form-item label="维修费用" prop="cost">
              <el-input-number v-model="form.cost" :min="0" :precision="2" placeholder="请输入维修费用"  :disabled="isRepairStaff"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计完成时间" prop="estimatedCompletionTime">
              <el-date-picker v-model="form.estimatedCompletionTime" type="datetime" value-format="timestamp" placeholder="选择预计完成时间" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="配送方式" prop="deliveryOption">
              <el-input value="到店自提" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="自提码" prop="pickupCode">
              <el-input v-model="form.pickupCode" placeholder="请输入自提码" :disabled="isRepairStaff && form.id !== undefined" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <!-- 查看维修单详情对话框 -->
    <el-dialog title="维修单详情" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="维修单号">{{ viewData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ viewData.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="门店" v-if="isAdmin">{{ viewData.storeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ viewData.staffName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机型号">{{ viewData.phoneModel }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{ viewData.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ viewData.faultDescription }}</el-descriptions-item>
        <el-descriptions-item label="维修费用" v-if="viewData.status === 'completed'">
          <span v-if="viewData.cost">¥{{ viewData.cost }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="配送方式">
          <span>到店自提</span>
        </el-descriptions-item>
        <el-descriptions-item label="自提码">{{ viewData.pickupCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(viewData.submitTime) }}</el-descriptions-item>
        <el-descriptions-item label="预计完成时间">{{ parseTime(viewData.estimatedCompletionTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际完成时间">{{ parseTime(viewData.actualCompletionTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewData.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">维修进度</el-divider>
      <el-timeline v-if="progressList.length > 0">
        <el-timeline-item v-for="(progress, index) in progressList" :key="index" :timestamp="parseTime(progress.time)" placement="top">
          <el-card>
            <h4>{{ progress.statusText }}</h4>
            <p>{{ progress.description }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无维修进度"></el-empty>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加维修进度对话框 -->
    <el-dialog title="添加维修进度" :visible.sync="progressOpen" width="600px" append-to-body>
      <el-form ref="progressForm" :model="progressForm" :rules="progressRules" label-width="100px">
        <el-form-item label="维修单ID">
          <el-input v-model="progressForm.orderId" disabled />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="progressForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="已受理" value="accepted" />
            <el-option label="诊断中" value="diagnosed" />
            <el-option label="已诊断" value="diagnosed_done" />
            <el-option label="维修中" value="repairing" />
            <el-option label="已维修" value="repair_done" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态文本" prop="statusText">
          <el-input v-model="progressForm.statusText" placeholder="请输入状态文本" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="progressForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitProgressForm">确 定</el-button>
        <el-button @click="progressOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRepairOrder, getRepairOrder, addRepairOrder, updateRepairOrder, delRepairOrder, updateRepairOrderStatus, updateRepairOrderCost, listRepairProgress, addRepairProgress, exportRepairOrder} from '@/api/repair/repairOrder'
import { listUser } from '@/api/system/user'
import {parseTime} from '@/utils/ruoyi'
import { mapGetters } from 'vuex'

export default {
  name: 'RepairOrder',
  computed: {
    ...mapGetters(['roles', 'userId', 'nickname', 'deptId']),
    isAdmin() {
      return this.roles.includes('super_admin')
    },
    isStoreManager() {
      return this.roles.includes('manager')
    },
    isRepairStaff() {
      return this.roles.includes('repair')
    }
  },
  data() {
    return {
      loading: true,
      exportLoading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      repairOrderList: [],
      title: '',
      open: false,
      viewOpen: false,
      progressOpen: false,
      viewData: {},
      progressList: [],
      staffList: [],
      columns: [
        { key: 0, label: '维修单号', visible: true },
        { key: 1, label: '客户', visible: true },
        { key: 2, label: '门店', visible: true },
        { key: 3, label: '维修人员', visible: true },
        { key: 4, label: '手机型号', visible: true },
        { key: 5, label: '故障描述', visible: true },
        { key: 6, label: '状态', visible: true },
        { key: 7, label: '维修费用', visible: true },
        { key: 8, label: '配送方式', visible: true },
        { key: 9, label: '自提码', visible: true },
        { key: 10, label: '提交时间', visible: true },
        { key: 11, label: '预计完成时间', visible: true }
      ],
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        orderNo: undefined,
        name: undefined,
        staffName: undefined,
      },
      form: {},
      rules: {
        name: [{ required: true, message: '客户姓名不能为空', trigger: 'blur' }],
        phone: [{ required: true, message: '客户手机不能为空', trigger: 'blur' }],
        phoneModel: [{ required: true, message: '手机型号不能为空', trigger: 'blur' }],
        faultDescription: [{ required: true, message: '故障描述不能为空', trigger: 'blur' }]
      },
      progressForm: {},
      progressRules: {
        status: [{ required: true, message: '状态不能为空', trigger: 'change' }],
        statusText: [{ required: true, message: '状态文本不能为空', trigger: 'blur' }],
        description: [{ required: true, message: '描述不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    // this.getStaffList()
  },
  methods: {
    parseTime,
    isEmpty(val) {
      return isEmpty(val)
    },
    /** 查询维修单列表 */
    getList() {
      this.loading = true
      if (this.queryParams.submitTime && this.queryParams.submitTime.length === 2) {
        this.queryParams.submitTimeBegin = this.queryParams.submitTime[0]
        this.queryParams.submitTimeEnd = this.queryParams.submitTime[1]
      } else {
        this.queryParams.submitTimeBegin = undefined
        this.queryParams.submitTimeEnd = undefined
      }
      listRepairOrder(this.queryParams).then(response => {
        this.repairOrderList = response.data.list
        this.total = response.data.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.submitTime = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      // 当登录用户是维修人员时，自动设置维修人员信息
      if (this.isRepairStaff) {
        this.form.staffId = this.userId
        this.form.staffName = this.nickname
      } else if (this.isStoreManager || this.isAdmin) {
        // 只有店长或管理员才需要查询维修人员列表
        this.getStaffList()
      }
      this.open = true
      this.title = '添加维修单'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getRepairOrder(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改维修单'
      })
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
      this.getProgressList(row.id, row.status)
    },
    /** 获取进度列表 */
    getProgressList(orderId, status) {
      listRepairProgress(orderId).then(response => {
        let progressList = response.data || []
        // 根据当前状态过滤进度列表
        if (status) {
          // 定义状态顺序
          const statusOrder = {
            'accepted': 1,
            'diagnosed': 2,
            'diagnosed_done': 3,
            'repairing': 4,
            'repair_done': 5,
            'completed': 6
          }
          // 获取当前状态的顺序值
          const currentStatusOrder = statusOrder[status] || 0
          // 过滤出顺序值小于或等于当前状态顺序值的进度
          progressList = progressList.filter(progress => {
            const progressStatusOrder = statusOrder[progress.status] || 0
            return progressStatusOrder <= currentStatusOrder
          })
        }
        this.progressList = progressList
      })
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'handleStatus':
          this.handleStatusChange(row, 'diagnosed', '诊断中')
          break
        case 'handleDiagnose':
          this.handleStatusChange(row, 'diagnosed_done', '已诊断')
          break
        case 'handleRepair':
          this.handleStatusChange(row, 'repairing', '维修中')
          break
        case 'handleComplete':
          this.handleComplete(row)
          break
        case 'handleProgress':
          this.handleProgress(row)
          break
        case 'handleDelete':
          this.handleDelete(row)
          break
        default:
          break
      }
    },
    /** 状态变更 */
    handleStatusChange(row, status, statusText) {
      this.$prompt('请输入备注（可选）', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '备注信息'
      }).then(({ value }) => {
        const description = value || statusText
        updateRepairOrderStatus(row.id, status, statusText).then(() => {
          addRepairProgress({
            orderId: row.id,
            status: status,
            statusText: statusText,
            description: description,
            time: new Date()
          }).then(() => {
            this.$message.success('状态更新成功')
            this.getList()
          })
        })
      })
    },
    /** 完成维修 */
    handleComplete(row) {
      this.$prompt('请输入维修费用', '完成维修', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+(\.\d{0,2})?$/,
        inputErrorMessage: '请输入有效的金额'
      }).then(({ value }) => {
        // 先更新维修费用
        updateRepairOrderCost(row.id, parseFloat(value)).then(() => {
          // 再更新状态为已维修
          updateRepairOrderStatus(row.id, 'repair_done', '已维修').then(() => {
            // 添加已维修进度
            addRepairProgress({
              orderId: row.id,
              status: 'repair_done',
              statusText: '已维修',
              description: '维修已完成，费用：' + value + '元',
              time: new Date()
            }).then(() => {
              // 再更新状态为已完成
              updateRepairOrderStatus(row.id, 'completed', '已完成').then(() => {
                // 添加已完成进度
                addRepairProgress({
                  orderId: row.id,
                  status: 'completed',
                  statusText: '已完成',
                  description: '维修已完成，费用：' + value + '元',
                  time: new Date()
                }).then(() => {
                  this.$message.success('维修完成')
                  this.getList()
                })
              })
            })
          })
        })
      })
    },
    /** 添加进度 */
    handleProgress(row) {
      this.progressForm = {
        orderId: row.id,
        status: '',
        statusText: '',
        description: '',
        time: new Date()
      }
      this.progressOpen = true
    },
    /** 提交进度 */
    submitProgressForm() {
      this.$refs.progressForm.validate(valid => {
        if (valid) {
          addRepairProgress(this.progressForm).then(() => {
            this.$message.success('添加进度成功')
            this.progressOpen = false
            this.getList()
          })
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除维修单编号为"' + ids + '"的数据项？').then(() => {
        return delRepairOrder(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 提交表单 */
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.id !== undefined) {
            updateRepairOrder(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addRepairOrder(this.form).then(response => {
              // 添加已受理进度记录
              addRepairProgress({
                orderId: response.data,
                status: 'accepted',
                statusText: '已受理',
                description: '维修单已创建并受理',
                time: new Date()
              }).then(() => {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getList()
              })
            })
          }
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false
      this.reset()
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        phone: undefined,
        staffId: undefined,
        staffName: undefined,
        phoneModel: undefined,
        faultDescription: undefined,
        cost: undefined,
        estimatedCompletionTime: undefined,
        deliveryOption: undefined,
        pickupCode: undefined
      }
      this.resetForm('form')
    },
    /** 获取维修人员列表 */
    getStaffList() {
      // 构建查询参数
      const query = { pageNo: 1, pageSize: 100 }
      // 如果是店长，只获取对应门店的维修人员
      if (this.isStoreManager && this.deptId) {
        query.deptId = this.deptId
      }
      listUser(query).then(response => {
        // 过滤出维修人员角色的用户
        this.staffList = response.data.list
      })
    },
    /** 获取状态标签类型 */
    getStatusType(status) {
      const statusMap = {
        'accepted': 'warning',
        'diagnosed': 'warning',
        'diagnosed_done': 'warning',
        'repairing': 'primary',
        'repair_done': 'primary',
        'completed': 'success'
      }
      return statusMap[status] || 'info'
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有维修单数据项?').then(() => {
        let params = {...this.queryParams};
        params.pageNo = undefined;
        params.pageSize = undefined;
        this.exportLoading = true;
        return exportRepairOrder(params);
      }).then(response => {
        this.$download.excel(response, '维修单数据.xls');
      }).finally(() => {
        this.exportLoading = false;
      });
    },
  }
}
</script>

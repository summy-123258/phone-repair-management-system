<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="配件名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入配件名称" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="规格型号" prop="spec">
        <el-input v-model="queryParams.spec" placeholder="请输入规格型号" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item style="margin-top: 0 !important; margin-bottom: 0;">
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['repair:accessory:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['repair:accessory:delete']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 横向滚动容器 -->
    <div style="overflow-x: auto;">
      <el-table v-loading="loading" :data="accessoryList" @selection-change="handleSelectionChange" style="min-width: 1200px;">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="配件名称" align="center" prop="name" v-if="columns[0].visible" width="140" />
        <el-table-column label="规格型号" align="center" prop="spec" v-if="columns[1].visible" width="160" />
        <el-table-column label="单位" align="center" prop="unit" v-if="columns[2].visible" width="80" />
        <el-table-column label="安全库存" align="center" prop="safeStock" v-if="columns[3].visible" width="100" />
        <el-table-column label="最大库存" align="center" prop="maxStock" v-if="columns[4].visible" width="100" />
        <!-- 添加库存字段 -->
        <el-table-column prop="totalStock" align="center" label="总库存" width="100" />
        <el-table-column prop="availableStock" align="center" label="可用库存" width="100" />
        <el-table-column label="供应商" align="center" prop="supplier" v-if="columns[5].visible" width="160" />
        <el-table-column label="备注" align="center" prop="remark" v-if="columns[6].visible" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[7].visible" width="160">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-plus" @click="handleReplenish(scope.row)"
                       v-hasPermi="['repair:accessory:update']">补货</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                       v-hasPermi="['repair:accessory:update']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                       v-hasPermi="['repair:accessory:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加或修改配件对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配件名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入配件名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="spec">
              <el-input v-model="form.spec" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安全库存" prop="safeStock">
              <el-input-number v-model="form.safeStock" :min="0" placeholder="请输入安全库存" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最大库存" prop="maxStock">
              <el-input-number v-model="form.maxStock" :min="0" placeholder="请输入最大库存" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isAdmin">
            <el-form-item label="所属门店" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择门店" style="width: 100%">
                <el-option
                  v-for="dept in deptList"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input v-model="form.supplier" placeholder="请输入供应商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总库存" prop="totalStock">
              <el-input-number v-model="form.totalStock" :min="0" placeholder="请输入总库存" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可用库存" prop="availableStock">
              <el-input-number v-model="form.availableStock" :min="0" placeholder="请输入可用库存" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看配件详情对话框 -->
    <el-dialog title="配件详情" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="配件名称">{{ viewData.name }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ viewData.spec }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ viewData.unit }}</el-descriptions-item>
        <el-descriptions-item label="安全库存">{{ viewData.safeStock }}</el-descriptions-item>
        <el-descriptions-item label="最大库存">{{ viewData.maxStock }}</el-descriptions-item>
        <!-- 添加库存字段 -->
        <el-descriptions-item label="总库存">{{ viewData.totalStock }}</el-descriptions-item>
        <el-descriptions-item label="可用库存">{{ viewData.availableStock }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ viewData.supplier || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(viewData.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewData.creator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新人">{{ viewData.updater || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="配件补货" :visible.sync="replenishOpen" width="400px" append-to-body>
      <el-form ref="replenishForm" :model="replenishForm" :rules="replenishRules" label-width="100px">
        <el-form-item label="配件名称" disabled>
          <el-input v-model="replenishForm.name" placeholder="配件名称" />
        </el-form-item>
        <el-form-item label="当前库存" disabled>
          <el-input v-model="replenishForm.currentStock" placeholder="当前库存" />
        </el-form-item>
        <el-form-item label="补货门店" prop="deptId"  v-if="isAdmin">
          <el-select v-model="replenishForm.deptId" placeholder="请选择门店" style="width: 100%">
            <el-option
              v-for="dept in deptList"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="补货数量" prop="quantity">
          <el-input-number v-model="replenishForm.quantity" :min="1" placeholder="请输入补货数量" />
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
import { listAccessory, listAccessoryPage, listAccessoryByKeyword, listAccessoryByKeywordPage, getAccessory, addAccessory, updateAccessory, delAccessory, replenishAccessory } from '@/api/repair/accessory'
import { parseTime } from '@/utils/ruoyi'
import { listSimpleDepts } from '@/api/system/dept'
import { mapGetters } from 'vuex'

export default {
  name: 'Accessory',
  computed: {
    ...mapGetters(['roles', 'deptId']),
    isAdmin() {
      return this.roles.includes('super_admin')
    }
  },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      accessoryList: [],
      total: 0,
      title: '',
      open: false,
      viewOpen: false,
      viewData: {},
      columns: [
        { key: 0, label: '配件名称', visible: true },
        { key: 1, label: '规格型号', visible: true },
        { key: 2, label: '单位', visible: true },
        { key: 3, label: '安全库存', visible: true },
        { key: 4, label: '最大库存', visible: true },
        { key: 5, label: '供应商', visible: true },
        { key: 6, label: '备注', visible: true },
        { key: 7, label: '创建时间', visible: true }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        spec: undefined,
      },
      form: {},
      deptList: [],
      rules: {
        name: [{ required: true, message: '配件名称不能为空', trigger: 'blur' }],
        spec: [{ required: true, message: '规格型号不能为空', trigger: 'blur' }],
        unit: [{ required: true, message: '单位不能为空', trigger: 'blur' }],
        safeStock: [{ required: true, message: '安全库存不能为空', trigger: 'blur' }],
        maxStock: [{ required: true, message: '最大库存不能为空', trigger: 'blur' }]
      },
      // 补货相关
      replenishOpen: false,
      replenishForm: {
        id: undefined,
        name: '',
        currentStock: 0,
        quantity: 1
      },
      replenishRules: {
        quantity: [{ required: true, message: '补货数量不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    /** 查询配件列表 */
    getList() {
      this.loading = true
      listAccessoryPage(this.queryParams).then(response => {
        this.accessoryList = response.data.list
        this.total = response.data.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      const keyword = this.queryParams.name || this.queryParams.spec
      if (keyword) {
        this.loading = true
        listAccessoryByKeywordPage({ ...this.queryParams, keyword }).then(response => {
          this.accessoryList = response.data.list
          this.total = response.data.total
          this.loading = false
        })
      } else {
        this.getList()
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 分页大小改变 */
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.getList()
    },
    /** 分页页码改变 */
    handleCurrentChange(current) {
      this.queryParams.pageNum = current
      this.getList()
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
      this.listSimpleDepts()
      this.open = true
      this.title = '添加配件'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAccessory(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改配件'
      })
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除配件编号为"' + ids + '"的数据项？').then(() => {
        return delAccessory(ids)
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
            updateAccessory(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addAccessory(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
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
        spec: undefined,
        unit: undefined,
        safeStock: 0,
        maxStock: 0,
        totalStock: 0,
        availableStock: 0,
        supplier: undefined,
        remark: undefined,
        deptId: undefined
      }
      this.resetForm('form')
    },
    /** 获取门店列表 */
    listSimpleDepts() {
      listSimpleDepts().then(response => {
        this.deptList = response.data
      })
    },
    /** 补货按钮操作 */
    handleReplenish(row) {
      this.replenishForm = {
        id: row.id,
        name: row.name,
        currentStock: row.totalStock,
        quantity: 1
      }
      this.listSimpleDepts()
      this.replenishOpen = true
    },
    /** 提交补货表单 */
    submitReplenish() {
      this.$refs.replenishForm.validate(valid => {
        if (valid) {
          replenishAccessory(this.replenishForm).then(() => {
            this.$modal.msgSuccess('补货成功')
            this.replenishOpen = false
            this.getList()
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
        quantity: 1
      }
      this.resetForm('replenishForm')
    }
  }
}
</script>

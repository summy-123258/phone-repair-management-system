<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="维修单号" prop="repairOrderNo">
        <el-input v-model="queryParams.repairOrderNo" placeholder="请输入维修单号" clearable style="width: 180px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item style="margin-top: 0 !important; margin-bottom: 0;">
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleUse"
                   v-hasPermi="['repair:accessory:use']">领用配件</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-back" size="mini" @click="handleReturn"
                   v-hasPermi="['repair:accessory:return']">退库配件</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="flowList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配件名称" align="center" prop="accessoryName" v-if="columns[0].visible" width="140" />
      <el-table-column label="维修单号" align="center" prop="repairOrderNo" v-if="columns[1].visible" width="160" />
      <el-table-column label="操作类型" align="center" prop="type" v-if="columns[2].visible" width="100">
        <template v-slot="scope">
          <el-tag :type="scope.row.type === 'use' ? 'primary' : 'success'">
            {{ scope.row.type === 'use' ? '领用' : '退库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="数量" align="center" prop="quantity" v-if="columns[3].visible" width="80" />
      <el-table-column label="操作人" align="center" prop="operator" v-if="columns[4].visible" width="120" />
      <el-table-column label="原因" align="center" prop="reason" v-if="columns[5].visible" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" v-if="columns[6].visible" :show-overflow-tooltip="true" />
      <el-table-column label="操作时间" align="center" prop="createTime" v-if="columns[7].visible" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 领用配件对话框 -->
    <el-dialog title="领用配件" :visible.sync="useOpen" width="600px" append-to-body>
      <el-form ref="useForm" :model="useForm" :rules="useRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配件" prop="accessoryId">
              <el-select v-model="useForm.accessoryId" placeholder="请选择配件" style="width: 100%">
                <el-option
                  v-for="accessory in accessoryList"
                  :key="accessory.id"
                  :label="accessory.name + '(' + accessory.spec + ') - 可用:' + accessory.availableStock"
                  :value="accessory.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修单号" prop="repairOrderNo">
              <el-input v-model="useForm.repairOrderNo" placeholder="请输入维修单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="useForm.quantity" :min="1" placeholder="请输入领用数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="原因" prop="reason">
              <el-input v-model="useForm.reason" type="textarea" :rows="2" placeholder="请输入领用原因" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="useForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUseForm">确 定</el-button>
        <el-button @click="useOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 退库配件对话框 -->
    <el-dialog title="退库配件" :visible.sync="returnOpen" width="600px" append-to-body>
      <el-form ref="returnForm" :model="returnForm" :rules="returnRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配件" prop="accessoryId">
              <el-select v-model="returnForm.accessoryId" placeholder="请选择配件" style="width: 100%">
                <el-option
                  v-for="accessory in accessoryList"
                  :key="accessory.id"
                  :label="accessory.name + '(' + accessory.spec + ') - 可用:' + accessory.availableStock"
                  :value="accessory.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修单号" prop="repairOrderNo">
              <el-input v-model="returnForm.repairOrderNo" placeholder="请输入维修单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="returnForm.quantity" :min="1" placeholder="请输入退库数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="质量问题" prop="isQualityIssue">
              <el-switch v-model="returnForm.isQualityIssue" active-text="是" inactive-text="否" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="原因" prop="reason">
              <el-input v-model="returnForm.reason" type="textarea" :rows="2" placeholder="请输入退库原因" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="returnForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReturnForm">确 定</el-button>
        <el-button @click="returnOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createAccessoryUse, createAccessoryReturn, listAccessoryFlowByRepairOrder, listAccessoryFlowByAccessory, listAllAccessoryFlow, listAccessory } from '@/api/repair/accessory'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AccessoryFlow',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      flowList: [],
      useOpen: false,
      returnOpen: false,
      accessoryList: [], // 配件列表
      columns: [
        { key: 0, label: '配件名称', visible: true },
        { key: 1, label: '维修单号', visible: true },
        { key: 2, label: '操作类型', visible: true },
        { key: 3, label: '数量', visible: true },
        { key: 4, label: '操作人', visible: true },
        { key: 5, label: '原因', visible: true },
        { key: 6, label: '备注', visible: true },
        { key: 7, label: '操作时间', visible: true }
      ],
      queryParams: {
        repairOrderNo: undefined,
        accessoryId: undefined,
        type: undefined
      },
      useForm: {
        accessoryId: undefined,
        repairOrderNo: undefined,
        quantity: 1,
        reason: undefined,
        remark: undefined
      },
      useRules: {
        accessoryId: [{ required: true, message: '配件不能为空', trigger: 'blur' }],
        repairOrderNo: [{ required: true, message: '维修单号不能为空', trigger: 'blur' }],
        quantity: [{ required: true, message: '数量不能为空', trigger: 'blur' }],
        reason: [{ required: true, message: '领用原因不能为空', trigger: 'blur' }]
      },
      returnForm: {
        accessoryId: undefined,
        repairOrderNo: undefined,
        quantity: 1,
        reason: undefined,
        isQualityIssue: false,
        remark: undefined
      },
      returnRules: {
        accessoryId: [{ required: true, message: '配件不能为空', trigger: 'blur' }],
        repairOrderNo: [{ required: true, message: '维修单号不能为空', trigger: 'blur' }],
        quantity: [{ required: true, message: '数量不能为空', trigger: 'blur' }],
        reason: [{ required: true, message: '退库原因不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    /** 查询流水列表 */
    getList() {
      this.loading = true
      if (this.queryParams.repairOrderNo) {
        listAccessoryFlowByRepairOrder(this.queryParams.repairOrderNo).then(response => {
          this.flowList = response.data
          this.loading = false
        })
      } else if (this.queryParams.accessoryId) {
        listAccessoryFlowByAccessory(this.queryParams.accessoryId).then(response => {
          this.flowList = response.data
          this.loading = false
        })
      } else {
        // 默认显示所有流水
        listAllAccessoryFlow().then(response => {
          this.flowList = response.data
          this.loading = false
        })
      }
    },
    /** 获取配件列表 */
    getAccessoryList() {
      listAccessory().then(response => {
        // 过滤出有可用库存的配件
        this.accessoryList = response.data.filter(item => item.availableStock > 0)
      })
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
    /** 领用配件按钮操作 */
    handleUse() {
      this.resetUseForm()
      this.getAccessoryList() // 重新获取配件列表，确保库存信息最新
      this.useOpen = true
    },
    /** 退库配件按钮操作 */
    handleReturn() {
      this.resetReturnForm()
      this.getAccessoryList() // 重新获取配件列表，确保库存信息最新
      this.returnOpen = true
    },
    /** 提交领用表单 */
    submitUseForm() {
      this.$refs.useForm.validate(valid => {
        if (valid) {
          createAccessoryUse(this.useForm).then(() => {
            this.$modal.msgSuccess('领用成功')
            this.useOpen = false
            this.getList()
            this.getAccessoryList() // 刷新配件列表，更新库存信息
          })
        }
      })
    },
    /** 提交退库表单 */
    submitReturnForm() {
      this.$refs.returnForm.validate(valid => {
        if (valid) {
          createAccessoryReturn(this.returnForm).then(() => {
            this.$modal.msgSuccess('退库成功')
            this.returnOpen = false
            this.getList()
            this.getAccessoryList() // 刷新配件列表，更新库存信息
          })
        }
      })
    },
    /** 重置领用表单 */
    resetUseForm() {
      this.useForm = {
        accessoryId: undefined,
        repairOrderNo: undefined,
        quantity: 1,
        reason: undefined,
        remark: undefined
      }
      this.resetForm('useForm')
    },
    /** 重置退库表单 */
    resetReturnForm() {
      this.returnForm = {
        accessoryId: undefined,
        repairOrderNo: undefined,
        quantity: 1,
        reason: undefined,
        isQualityIssue: false,
        remark: undefined
      }
      this.resetForm('returnForm')
    }
  }
}
</script>

<template>
  <div class="configuration-tab">
    <el-card shadow="never" body-class="search-card">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="配置键" prop="configKey">
          <el-input
            v-model="queryParams.configKey"
            placeholder="请输入配置键"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input
            v-model="queryParams.configValue"
            placeholder="请输入配置值"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="是否激活" prop="isActive">
          <el-select v-model="queryParams.isActive" placeholder="请选择" clearable>
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="mt10">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['setting:configuration:add']"
          >新增配置</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['setting:configuration:remove']"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="configurationList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="配置键" align="center" prop="configKey" show-overflow-tooltip />
        <el-table-column label="配置值" align="center" prop="configValue" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.isSensitive">******</span>
            <span v-else>{{ scope.row.configValue }}</span>
          </template>
        </el-table-column>
        <el-table-column label="配置描述" align="center" prop="description" show-overflow-tooltip />
        <el-table-column label="是否激活" align="center" prop="isActive">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="敏感配置" align="center" prop="isSensitive">
          <template #default="scope">
            <el-tag :type="scope.row.isSensitive ? 'warning' : 'info'">
              {{ scope.row.isSensitive ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="版本号" align="center" prop="version" width="80" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['setting:configuration:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['setting:configuration:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="configurationRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="是否激活" prop="isActive">
          <el-switch v-model="form.isActive" />
        </el-form-item>
        <el-form-item label="敏感配置" prop="isSensitive">
          <el-switch v-model="form.isSensitive" />
          <div class="form-tip">敏感配置的值会被隐藏显示</div>
        </el-form-item>
        <el-form-item label="加密存储" prop="isEncrypted">
          <el-switch v-model="form.isEncrypted" />
          <div class="form-tip">加密存储的配置值会在数据库中加密保存</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { listConfiguration, getConfiguration, addConfiguration, updateConfiguration, delConfiguration } from "@/api/setting/configuration";
  import { ElMessage, ElMessageBox } from 'element-plus';

  const props = defineProps({
    currentEnvironment: {
      type: Object,
      default: null
    }
  });

  const { proxy } = getCurrentInstance();

  const configurationList = ref([]);
  const open = ref(false);
  const loading = ref(true);
  const showSearch = ref(true);
  const ids = ref([]);
  const single = ref(true);
  const multiple = ref(true);
  const total = ref(0);
  const title = ref("");

  const queryParams = ref({
    pageNum: 1,
    pageSize: 10,
    configKey: null,
    configValue: null,
    isActive: null
  });

  const form = ref({});

  const rules = {
    configKey: [
      { required: true, message: "配置键不能为空", trigger: "blur" }
    ],
    configValue: [
      { required: true, message: "配置值不能为空", trigger: "blur" }
    ]
  };

  const getList = async () => {
    loading.value = true;
    try {
      const response = await listConfiguration(queryParams.value);
      if (response.code === 200) {
        configurationList.value = response.rows || [];
        total.value = response.total || 0;
      }
    } catch (error) {
      console.error('获取配置列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  const handleQuery = () => {
    queryParams.value.pageNum = 1;
    getList();
  };

  const resetQuery = () => {
    proxy.resetForm("queryRef");
    handleQuery();
  };

  const handleSelectionChange = (selection) => {
    ids.value = selection.map(item => item.id);
    single.value = selection.length !== 1;
    multiple.value = !selection.length;
  };

  const handleAdd = () => {
    reset();
    open.value = true;
    title.value = "新增配置";
  };

  const handleUpdate = async (row) => {
    reset();
    const id = row.id;
    try {
      const response = await getConfiguration(id);
      if (response.code === 200) {
        form.value = response.data;
        open.value = true;
        title.value = "修改配置";
      }
    } catch (error) {
      console.error('获取配置详情失败:', error);
    }
  };

  const handleDelete = async (row) => {
    const deleteIds = row.id ? [row.id] : ids.value;
    try {
      await ElMessageBox.confirm('是否确认删除选中的配置项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      const response = await delConfiguration(deleteIds);
      if (response.code === 200) {
        getList();
        ElMessage.success("删除成功");
      } else {
        ElMessage.error(response.msg || '删除失败');
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error(error.msg || '删除失败');
      }
    }
  };

  const submitForm = async () => {
    proxy.$refs.configurationRef.validate(async (valid) => {
      if (valid) {
        try {
          let response;
          if (form.value.id != null) {
            response = await updateConfiguration(form.value);
          } else {
            response = await addConfiguration(form.value);
          }
          if (response.code === 200) {
            ElMessage.success("操作成功");
            open.value = false;
            getList();
          } else {
            ElMessage.error(response.msg || '操作失败');
          }
        } catch (error) {
          ElMessage.error(error.msg || '操作失败');
        }
      }
    });
  };

  const cancel = () => {
    open.value = false;
    reset();
  };

  const reset = () => {
    form.value = {
      id: null,
      configKey: null,
      configValue: null,
      description: null,
      isActive: true,
      isSensitive: false,
      isEncrypted: false,
      version: 1,
      remark: null
    };
    proxy.resetForm("configurationRef");
  };

  onMounted(() => {
    getList();
  });
</script>

<style scoped>
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.mt10 {
  margin-top: 10px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>

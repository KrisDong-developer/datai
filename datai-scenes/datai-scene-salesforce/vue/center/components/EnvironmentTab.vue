<template>
  <div class="environment-tab">
    <el-card shadow="never" class="current-env-card">
      <template #header>
        <div class="card-header">
          <span>当前环境</span>
          <el-tag v-if="currentEnvironment" type="success">{{ currentEnvironment.environmentName }}</el-tag>
          <el-tag v-else type="info">未设置</el-tag>
        </div>
      </template>
      
      <el-descriptions v-if="currentEnvironment" :column="2" border>
        <el-descriptions-item label="环境名称">
          {{ currentEnvironment.environmentName }}
        </el-descriptions-item>
        <el-descriptions-item label="环境编码">
          {{ currentEnvironment.environmentCode }}
        </el-descriptions-item>
        <el-descriptions-item label="环境描述" :span="2">
          {{ currentEnvironment.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="是否激活">
          <el-tag :type="currentEnvironment.isActive ? 'success' : 'danger'">
            {{ currentEnvironment.isActive ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(currentEnvironment.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <el-empty v-else description="暂无当前环境" />
    </el-card>

    <el-card shadow="never" class="mt10">
      <template #header>
        <div class="card-header">
          <span>环境列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['setting:environment:add']">新增环境</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="environmentList">
        <el-table-column label="环境名称" align="center" prop="environmentName" />
        <el-table-column label="环境编码" align="center" prop="environmentCode" />
        <el-table-column label="环境描述" align="center" prop="description" show-overflow-tooltip />
        <el-table-column label="是否激活" align="center" prop="isActive">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="240" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.isActive && (!currentEnvironment || currentEnvironment.id !== scope.row.id)"
              link
              type="primary"
              icon="Switch"
              @click="handleSwitch(scope.row)"
              v-hasPermi="['setting:environment:switch']"
            >
              切换
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['setting:environment:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['setting:environment:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="environmentRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="环境名称" prop="environmentName">
          <el-input v-model="form.environmentName" placeholder="请输入环境名称" />
        </el-form-item>
        <el-form-item label="环境编码" prop="environmentCode">
          <el-input v-model="form.environmentCode" placeholder="请输入环境编码（如：dev、test、prod）" />
        </el-form-item>
        <el-form-item label="环境描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入环境描述" />
        </el-form-item>
        <el-form-item label="是否激活" prop="isActive">
          <el-switch v-model="form.isActive" />
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

    <el-dialog title="切换环境" v-model="switchOpen" width="500px" append-to-body>
      <el-form ref="switchRef" :model="switchForm" :rules="switchRules" label-width="100px">
        <el-form-item label="目标环境" prop="environmentCode">
          <el-input v-model="switchForm.environmentCode" disabled />
        </el-form-item>
        <el-form-item label="切换原因" prop="switchReason">
          <el-input v-model="switchForm.switchReason" type="textarea" placeholder="请输入切换原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitSwitch">确 定</el-button>
          <el-button @click="switchOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { listEnvironment, getEnvironment, addEnvironment, updateEnvironment, delEnvironment, switchEnvironment, getCurrentEnvironment } from "@/api/setting/environment";
  import { ElMessage, ElMessageBox } from 'element-plus';

  const { proxy } = getCurrentInstance();
  const emit = defineEmits(['environment-switched']);

  const environmentList = ref([]);
  const currentEnvironment = ref(null);
  const loading = ref(true);
  const open = ref(false);
  const switchOpen = ref(false);
  const title = ref("");

  const form = ref({});
  const switchForm = ref({
    environmentCode: '',
    switchReason: '手动切换'
  });

  const rules = {
    environmentName: [
      { required: true, message: "环境名称不能为空", trigger: "blur" }
    ],
    environmentCode: [
      { required: true, message: "环境编码不能为空", trigger: "blur" }
    ]
  };

  const switchRules = {
    switchReason: [
      { required: true, message: "切换原因不能为空", trigger: "blur" }
    ]
  };

  const getList = async () => {
    loading.value = true;
    try {
      const response = await listEnvironment({});
      if (response.code === 200) {
        environmentList.value = response.rows || [];
      }
    } catch (error) {
      console.error('获取环境列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  const loadCurrentEnvironment = async () => {
    try {
      const response = await getCurrentEnvironment();
      if (response.code === 200) {
        currentEnvironment.value = response.data;
      }
    } catch (error) {
      console.error('获取当前环境失败:', error);
    }
  };

  const handleAdd = () => {
    reset();
    open.value = true;
    title.value = "新增环境";
  };

  const handleUpdate = async (row) => {
    reset();
    const id = row.id;
    try {
      const response = await getEnvironment(id);
      if (response.code === 200) {
        form.value = response.data;
        open.value = true;
        title.value = "修改环境";
      }
    } catch (error) {
      console.error('获取环境详情失败:', error);
    }
  };

  const handleDelete = async (row) => {
    try {
      await ElMessageBox.confirm('是否确认删除环境名称为"' + row.environmentName + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      const response = await delEnvironment(row.id);
      if (response.code === 200) {
        getList();
        loadCurrentEnvironment();
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

  const handleSwitch = (row) => {
    switchForm.value.environmentCode = row.environmentCode;
    switchForm.value.switchReason = '手动切换';
    switchOpen.value = true;
  };

  const submitSwitch = async () => {
    try {
      const response = await switchEnvironment(switchForm.value);
      if (response.code === 200) {
        ElMessage.success(response.msg || '环境切换成功');
        switchOpen.value = false;
        loadCurrentEnvironment();
        emit('environment-switched');
      } else {
        ElMessage.error(response.msg || '环境切换失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '环境切换失败');
    }
  };

  const submitForm = async () => {
    proxy.$refs.environmentRef.validate(async (valid) => {
      if (valid) {
        try {
          let response;
          if (form.value.id != null) {
            response = await updateEnvironment(form.value);
          } else {
            response = await addEnvironment(form.value);
          }
          if (response.code === 200) {
            ElMessage.success("操作成功");
            open.value = false;
            getList();
            loadCurrentEnvironment();
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
      environmentName: null,
      environmentCode: null,
      description: null,
      isActive: true,
      remark: null
    };
    proxy.resetForm("environmentRef");
  };

  onMounted(() => {
    getList();
    loadCurrentEnvironment();
  });
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.current-env-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
}

.current-env-card :deep(.el-card__header) {
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.current-env-card :deep(.el-card__body) {
  padding: 20px;
}

.current-env-card :deep(.el-descriptions__label) {
  background: #f5f7fa;
  font-weight: 500;
}

.current-env-card :deep(.el-descriptions__content) {
  background: #ffffff;
}

.mt10 {
  margin-top: 10px;
}
</style>

<template>
  <div class="snapshot-tab">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleCreateSnapshot">创建快照</el-button>
      <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
    </div>

    <el-table :data="snapshotList" v-loading="loading" border stripe>
      <el-table-column prop="id" label="快照ID" width="80" />
      <el-table-column prop="snapshotName" label="快照名称" min-width="150" />
      <el-table-column prop="environmentName" label="环境" width="120" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="configCount" label="配置数量" width="100" align="center" />
      <el-table-column prop="createdBy" label="创建人" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="View" @click="handleViewDetail(row)">查看</el-button>
          <el-button type="success" link :icon="RefreshRight" @click="handleRestore(row)">恢复</el-button>
          <el-button type="warning" link :icon="DocumentCopy" @click="handleCompare(row)">对比</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="createDialogVisible" title="创建快照" width="600px" @close="resetCreateForm">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="快照名称" prop="snapshotName">
          <el-input v-model="createForm.snapshotName" placeholder="请输入快照名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="环境" prop="environmentId">
          <el-select v-model="createForm.environmentId" placeholder="请选择环境" style="width: 100%">
            <el-option
              v-for="env in environmentList"
              :key="env.id"
              :label="env.environmentName"
              :value="env.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入快照描述"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCreate" :loading="createLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="快照详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="快照ID">{{ currentSnapshot.id }}</el-descriptions-item>
        <el-descriptions-item label="快照名称">{{ currentSnapshot.snapshotName }}</el-descriptions-item>
        <el-descriptions-item label="环境">{{ currentSnapshot.environmentName }}</el-descriptions-item>
        <el-descriptions-item label="配置数量">{{ currentSnapshot.configCount }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ currentSnapshot.createdBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentSnapshot.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentSnapshot.description }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>配置详情</el-divider>

      <el-table :data="snapshotConfigList" border stripe max-height="400">
        <el-table-column prop="configKey" label="配置键" min-width="200" />
        <el-table-column prop="configValue" label="配置值" min-width="250" show-overflow-tooltip />
        <el-table-column prop="isSensitive" label="敏感配置" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isSensitive ? 'danger' : 'success'">
              {{ row.isSensitive ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="compareDialogVisible" title="快照对比" width="900px">
      <div class="compare-header">
        <el-select v-model="compareForm.snapshotId1" placeholder="选择快照1" style="width: 250px">
          <el-option
            v-for="snap in snapshotList"
            :key="snap.id"
            :label="snap.snapshotName"
            :value="snap.id"
          />
        </el-select>
        <span class="compare-divider">vs</span>
        <el-select v-model="compareForm.snapshotId2" placeholder="选择快照2" style="width: 250px">
          <el-option
            v-for="snap in snapshotList"
            :key="snap.id"
            :label="snap.snapshotName"
            :value="snap.id"
          />
        </el-select>
        <el-button type="primary" @click="handleExecuteCompare" :loading="compareLoading">对比</el-button>
      </div>

      <el-table :data="compareResult" border stripe v-if="compareResult.length > 0">
        <el-table-column prop="configKey" label="配置键" min-width="200" />
        <el-table-column prop="value1" label="快照1值" min-width="200" show-overflow-tooltip />
        <el-table-column prop="value2" label="快照2值" min-width="200" show-overflow-tooltip />
        <el-table-column label="差异" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isDifferent ? 'danger' : 'success'">
              {{ row.isDifferent ? '不同' : '相同' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="SnapshotTab">
  import {
    listSnapshot,
    createSnapshot,
    restoreSnapshot,
    getSnapshotDetail,
    compareSnapshots,
    delSnapshot
  } from '@/api/setting/snapshot';
  import { listEnvironment } from '@/api/setting/environment';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import {
    Plus,
    Refresh,
    View,
    RefreshRight,
    DocumentCopy,
    Delete
  } from '@element-plus/icons-vue';

  const props = defineProps({
    currentEnvironment: {
      type: Object,
      default: null
    }
  });

  const loading = ref(false);
  const createLoading = ref(false);
  const compareLoading = ref(false);
  const snapshotList = ref([]);
  const total = ref(0);
  const environmentList = ref([]);
  const snapshotConfigList = ref([]);
  const compareResult = ref([]);

  const queryParams = ref({
    pageNum: 1,
    pageSize: 10,
    environmentId: null
  });

  const createDialogVisible = ref(false);
  const detailDialogVisible = ref(false);
  const compareDialogVisible = ref(false);

  const createFormRef = ref(null);
  const currentSnapshot = ref({});

  const createForm = ref({
    snapshotName: '',
    environmentId: null,
    description: ''
  });

  const createRules = {
    snapshotName: [
      { required: true, message: '请输入快照名称', trigger: 'blur' }
    ],
    environmentId: [
      { required: true, message: '请选择环境', trigger: 'change' }
    ]
  };

  const compareForm = ref({
    snapshotId1: null,
    snapshotId2: null
  });

  const getList = async () => {
    loading.value = true;
    try {
      const response = await listSnapshot(queryParams.value);
      if (response.code === 200) {
        snapshotList.value = response.rows;
        total.value = response.total;
      }
    } catch (error) {
      ElMessage.error(error.msg || '获取快照列表失败');
    } finally {
      loading.value = false;
    }
  };

  const getEnvironmentList = async () => {
    try {
      const response = await listEnvironment({});
      if (response.code === 200) {
        environmentList.value = response.rows;
      }
    } catch (error) {
      console.error('获取环境列表失败:', error);
    }
  };

  const handleRefresh = () => {
    getList();
  };

  const handleCreateSnapshot = () => {
    if (props.currentEnvironment) {
      createForm.value.environmentId = props.currentEnvironment.id;
    }
    createDialogVisible.value = true;
  };

  const resetCreateForm = () => {
    createForm.value = {
      snapshotName: '',
      environmentId: null,
      description: ''
    };
    createFormRef.value?.clearValidate();
  };

  const handleConfirmCreate = async () => {
    if (!createFormRef.value) return;

    await createFormRef.value.validate(async (valid) => {
      if (valid) {
        createLoading.value = true;
        try {
          const response = await createSnapshot(createForm.value);
          if (response.code === 200) {
            ElMessage.success('快照创建成功');
            createDialogVisible.value = false;
            getList();
          } else {
            ElMessage.error(response.msg || '创建快照失败');
          }
        } catch (error) {
          ElMessage.error(error.msg || '创建快照失败');
        } finally {
          createLoading.value = false;
        }
      }
    });
  };

  const handleViewDetail = async (row) => {
    try {
      const response = await getSnapshotDetail(row.id);
      if (response.code === 200) {
        currentSnapshot.value = response.data.snapshot;
        snapshotConfigList.value = response.data.configs || [];
        detailDialogVisible.value = true;
      } else {
        ElMessage.error(response.msg || '获取快照详情失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '获取快照详情失败');
    }
  };

  const handleRestore = (row) => {
    ElMessageBox.confirm(
      `确定要恢复快照"${row.snapshotName}"吗？此操作将覆盖当前环境配置，请谨慎操作。`,
      '恢复确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        const response = await restoreSnapshot(row.id, {});
        if (response.code === 200) {
          ElMessage.success('快照恢复成功');
          getList();
        } else {
          ElMessage.error(response.msg || '恢复快照失败');
        }
      } catch (error) {
        ElMessage.error(error.msg || '恢复快照失败');
      }
    }).catch(() => {});
  };

  const handleCompare = (row) => {
    compareForm.value.snapshotId1 = row.id;
    compareDialogVisible.value = true;
  };

  const handleExecuteCompare = async () => {
    if (!compareForm.value.snapshotId1 || !compareForm.value.snapshotId2) {
      ElMessage.warning('请选择两个快照进行对比');
      return;
    }

    if (compareForm.value.snapshotId1 === compareForm.value.snapshotId2) {
      ElMessage.warning('请选择不同的快照进行对比');
      return;
    }

    compareLoading.value = true;
    try {
      const response = await compareSnapshots(
        compareForm.value.snapshotId1,
        compareForm.value.snapshotId2
      );
      if (response.code === 200) {
        compareResult.value = response.data || [];
      } else {
        ElMessage.error(response.msg || '对比快照失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '对比快照失败');
    } finally {
      compareLoading.value = false;
    }
  };

  const handleDelete = (row) => {
    ElMessageBox.confirm(
      `确定要删除快照"${row.snapshotName}"吗？删除后无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        const response = await delSnapshot(row.id);
        if (response.code === 200) {
          ElMessage.success('删除成功');
          getList();
        } else {
          ElMessage.error(response.msg || '删除失败');
        }
      } catch (error) {
        ElMessage.error(error.msg || '删除失败');
      }
    }).catch(() => {});
  };

  watch(() => props.currentEnvironment, (newVal) => {
    if (newVal) {
      queryParams.value.environmentId = newVal.id;
      getList();
    }
  }, { immediate: true });

  onMounted(() => {
    getEnvironmentList();
  });
</script>

<style scoped>
.snapshot-tab {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
}

.compare-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.compare-divider {
  font-weight: bold;
  color: #909399;
}

:deep(.el-table) {
  margin-top: 10px;
}

:deep(.el-divider) {
  margin: 20px 0;
}

:deep(.el-dialog__body) {
  padding: 20px;
}
</style>

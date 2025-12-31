<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>配置中心</span>
          <div class="header-actions">
            <el-button type="primary" :loading="refreshLoading" @click="handleRefreshCache">
              <el-icon><Refresh /></el-icon>
              刷新缓存
            </el-button>
            <el-button @click="handleCheckCacheStatus">
              <el-icon><InfoFilled /></el-icon>
              缓存状态
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="环境管理" name="environment">
          <EnvironmentTab @environment-switched="handleEnvironmentSwitched" />
        </el-tab-pane>

        <el-tab-pane label="配置管理" name="configuration">
          <ConfigurationTab :current-environment="currentEnvironment" />
        </el-tab-pane>

        <el-tab-pane label="快照管理" name="snapshot">
          <SnapshotTab :current-environment="currentEnvironment" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup name="ConfigCenter">
  import { refreshConfigCache, getConfigCacheStatus } from "@/api/setting/configuration";
  import { getCurrentEnvironment } from "@/api/setting/environment";
  import { ElMessage } from 'element-plus';
  import { Refresh, InfoFilled } from '@element-plus/icons-vue';
  import EnvironmentTab from './components/EnvironmentTab.vue';
  import ConfigurationTab from './components/ConfigurationTab.vue';
  import SnapshotTab from './components/SnapshotTab.vue';

  const activeTab = ref('environment');
  const refreshLoading = ref(false);
  const currentEnvironment = ref(null);

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

  const handleRefreshCache = async () => {
    refreshLoading.value = true;
    try {
      const response = await refreshConfigCache();
      if (response.code === 200) {
        ElMessage.success('配置缓存刷新成功');
      } else {
        ElMessage.error(response.msg || '刷新缓存失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '刷新缓存失败');
    } finally {
      refreshLoading.value = false;
    }
  };

  const handleCheckCacheStatus = async () => {
    try {
      const response = await getConfigCacheStatus();
      if (response.code === 200) {
        ElMessage.success(response.data || '配置缓存状态正常');
      } else {
        ElMessage.error(response.msg || '获取缓存状态失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '获取缓存状态失败');
    }
  };

  const handleEnvironmentSwitched = () => {
    loadCurrentEnvironment();
  };

  onMounted(() => {
    loadCurrentEnvironment();
  });
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}

:deep(.el-card__header) {
  padding: 16px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-tabs--border-card) {
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

:deep(.el-tabs__header) {
  background: #f5f7fa;
  margin: 0;
}
</style>

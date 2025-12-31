<template>
  <div class="app-container">
    <el-row :gutter="24" class="main-row">
      <el-col :span="9" :xs="24">
        <el-card shadow="hover" class="operation-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">Salesforce 认证中心</span>
              <el-tag type="info" size="small" effect="plain">Auth v2.0</el-tag>
            </div>
          </template>
          
          <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-position="top" size="large">
            <el-form-item label="登录方式" prop="loginType">
              <el-radio-group v-model="loginForm.loginType" @change="handleLoginTypeChange" class="full-width-radio">
                <el-radio-button label="oauth2">OAuth 2.0</el-radio-button>
                <el-radio-button label="legacy_credential">账密</el-radio-button>
                <el-radio-button label="salesforce_cli">CLI</el-radio-button>
                <el-radio-button label="session_id">Session</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item v-if="loginForm.loginType === 'oauth2'" label="授权模式" prop="grantType">
              <el-select v-model="loginForm.grantType" placeholder="请选择授权类型" @change="handleGrantTypeChange">
                <el-option label="密码模式 (Password)" value="password" />
                <el-option label="客户端凭证 (Client Credentials)" value="client_credentials" />
                <el-option label="授权码模式 (Authorization Code)" value="authorization_code" />
              </el-select>
            </el-form-item>

            
            <el-form-item v-if="showLoginUrl" label="登录地址 (Login URL)" prop="loginUrl">
              <el-input 
                v-model="loginForm.loginUrl" 
                prefix-icon="Link" 
                placeholder="https://login.salesforce.com" 
                clearable
                :readonly="!isCustomLoginUrl"
              >
                <template #append>
                  <el-dropdown @command="handleLoginUrlPreset">
                    <el-button>
                      <el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="https://login.salesforce.com">生产环境</el-dropdown-item>
                        <el-dropdown-item command="https://test.salesforce.com">沙盒环境</el-dropdown-item>
                        <el-dropdown-item command="custom" divided>自定义</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </template>
              </el-input>
              <div class="form-tip">留空则使用系统默认配置</div>
            </el-form-item>

            <el-form-item v-if="showUsername" label="用户名" prop="username">
              <el-input v-model="loginForm.username" prefix-icon="User" placeholder="name@company.com" clearable />
            </el-form-item>

            <el-form-item v-if="showPassword" label="密码" prop="password">
              <el-input v-model="loginForm.password" prefix-icon="Lock" type="password" placeholder="Salesforce 密码" show-password clearable />
            </el-form-item>

            <el-form-item v-if="showSecurityToken" label="安全令牌 (Security Token)" prop="securityToken">
              <el-input v-model="loginForm.securityToken" prefix-icon="Key" type="password" placeholder="若 IP 未加白名单则必填" show-password clearable />
            </el-form-item>

            <el-form-item v-if="showClientId" label="Consumer Key (Client ID)" prop="clientId">
              <el-input v-model="loginForm.clientId" prefix-icon="Ticket" placeholder="OAuth Consumer Key" clearable />
            </el-form-item>

            <el-form-item v-if="showClientSecret" label="Consumer Secret (Client Secret)" prop="clientSecret">
              <el-input v-model="loginForm.clientSecret" prefix-icon="Hide" type="password" placeholder="OAuth Consumer Secret" show-password clearable />
            </el-form-item>

            <el-form-item v-if="showCode" label="Authorization Code" prop="code">
              <el-input v-model="loginForm.code" placeholder="Callback Code" clearable />
            </el-form-item>

            <el-form-item v-if="showState" label="State" prop="state">
              <el-input v-model="loginForm.state" placeholder="State Parameter" clearable />
            </el-form-item>

            <el-form-item v-if="showOrgAlias" label="SFDX 组织别名" prop="orgAlias">
              <el-input v-model="loginForm.orgAlias" prefix-icon="Monitor" placeholder="CLI Org Alias" clearable />
            </el-form-item>

            <el-form-item v-if="showSessionId" label="Session ID" prop="sessionId">
              <el-input v-model="loginForm.sessionId" type="textarea" :rows="3" placeholder="Paste Session ID here" clearable />
            </el-form-item>


            <el-button type="primary" :loading="loginLoading" @click="handleLogin" class="main-login-btn">
              <el-icon class="el-icon--left"><Switch /></el-icon>
              发起登录请求
            </el-button>

            <div class="divider-text">快捷操作</div>

            <div class="action-row">
              <el-tooltip content="使用预设配置自动登录" placement="bottom">
                <el-button type="success" plain :loading="autoLoginLoading" @click="handleAutoLogin" class="flex-btn">
                  <el-icon class="el-icon--left"><RefreshRight /></el-icon>
                  自动登录
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="刷新查看当前登录信息" placement="bottom">
                <el-button type="info" plain :loading="currentLoading" @click="handleGetCurrentInfo" class="flex-btn">
                  <el-icon class="el-icon--left"><User /></el-icon>
                  刷新信息
                </el-button>
              </el-tooltip>

              <el-tooltip content="销毁当前登录信息" placement="bottom">
                <el-button type="warning" plain :loading="logoutLoading" @click="handleLogout" class="flex-btn">
                  <el-icon class="el-icon--left"><SwitchButton /></el-icon>
                  登出
                </el-button>
              </el-tooltip>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="15" :xs="24">
        <el-card shadow="hover" class="result-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">当前会话状态</span>
              <transition name="el-fade-in">
                <el-tag v-if="loginResult" :type="loginResult.success ? 'success' : 'danger'" effect="dark">
                  {{ loginResult.success ? '已连接' : '连接失败' }}
                </el-tag>
                <el-tag v-else type="info" effect="plain">未连接</el-tag>
              </transition>
            </div>
          </template>

          <transition name="el-zoom-in-top">
            <div v-if="loginResult" class="result-content">
              <el-alert
                v-if="!loginResult.success && loginResult.errorCode"
                :title="loginResult.errorMessage || '认证失败'"
                type="error"
                show-icon
                :description="`Error Code: ${loginResult.errorCode}`"
                :closable="false"
                class="mb-4"
              />

              <div v-else class="info-grid">
                <el-descriptions :column="1" border size="large">
                  <el-descriptions-item label="Session ID" label-class-name="desc-label">
                    <div class="code-block">
                      {{ loginResult.sessionId }}
                      <el-button link type="primary" @click="copyText(loginResult.sessionId)">
                        <el-icon><DocumentCopy /></el-icon>
                      </el-button>
                    </div>
                  </el-descriptions-item>
                </el-descriptions>

                <div class="grid-layout">
                  <el-descriptions :column="2" border direction="vertical" class="mt-4">
                    <el-descriptions-item label="Instance URL">
                      <el-link :href="loginResult.instanceUrl" target="_blank" type="primary" :underline="false">
                        {{ loginResult.instanceUrl }} <el-icon><TopRight /></el-icon>
                      </el-link>
                    </el-descriptions-item>
                    <el-descriptions-item label="User ID">
                      <span class="mono-text">{{ loginResult.userId }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="Org ID">
                      <span class="mono-text">{{ loginResult.organizationId }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="Token Type">
                      <el-tag size="small">{{ loginResult.tokenType }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="Environment">
                      <el-tag :type="loginResult.sandbox ? 'warning' : 'success'" size="small" effect="light">
                        {{ loginResult.sandbox ? 'Sandbox' : 'Production' }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="Expires In">
                      {{ loginResult.expiresIn ? loginResult.expiresIn + ' s' : 'Unknown' }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
                
                <el-divider content-position="left">Metadata Server</el-divider>
                <div class="metadata-link" v-if="loginResult.metadataServerUrl">
                   <el-link :href="loginResult.metadataServerUrl" target="_blank" type="info">
                    {{ loginResult.metadataServerUrl }}
                  </el-link>
                </div>
              </div>
            </div>

            <div v-else class="empty-state">
              <el-empty description="暂无有效的会话信息">
                <template #image>
                  <el-icon :size="60" color="#909399"><Connection /></el-icon>
                </template>
                <template #description>
                   <p class="empty-text">请在左侧选择方式登录，或点击"自动登录"</p>
                </template>
              </el-empty>
            </div>
          </transition>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="SalesforceLogin">
  import { ref, computed, getCurrentInstance, onMounted } from 'vue';
  import { doLogin, logout, getCurrentLoginInfo, autoLogin } from "@/api/auth/register";
  import { ElMessage, ElMessageBox } from 'element-plus';
  import { 
    RefreshRight, User, SwitchButton, DocumentCopy, Switch, 
    Connection, Key, Lock, Ticket, Hide, Monitor, TopRight, ArrowDown, Link
  } from '@element-plus/icons-vue';

  const { proxy } = getCurrentInstance();

  const loginFormRef = ref(null);
  const loginLoading = ref(false);
  const autoLoginLoading = ref(false);
  const currentLoading = ref(false);
  const logoutLoading = ref(false);
  const loginResult = ref(null);
  // 定义一个状态变量，默认设为 false (即默认只读/锁定，或者根据初始值判断)
  const isCustomLoginUrl = ref(false);

  const loginForm = ref({
    loginType: 'oauth2',
    grantType: 'password',
    username: '',
    password: '',
    securityToken: '',
    clientId: '',
    clientSecret: '',
    code: '',
    state: '',
    orgAlias: '',
    sessionId: '',
    loginUrl: ''
  });

  // --- 字段显示逻辑 Computed ---
  const showUsername = computed(() => {
    return ['oauth2', 'legacy_credential'].includes(loginForm.value.loginType) && 
           (loginForm.value.loginType !== 'oauth2' || loginForm.value.grantType === 'password');
  });

  const showPassword = computed(() => {
    return ['oauth2', 'legacy_credential'].includes(loginForm.value.loginType) && 
           (loginForm.value.loginType !== 'oauth2' || loginForm.value.grantType === 'password');
  });

  const showSecurityToken = computed(() => {
    return ['oauth2', 'legacy_credential'].includes(loginForm.value.loginType) && 
           (loginForm.value.loginType !== 'oauth2' || loginForm.value.grantType === 'password');
  });

  const showClientId = computed(() => {
    return loginForm.value.loginType === 'oauth2' && 
           ['client_credentials', 'authorization_code'].includes(loginForm.value.grantType);
  });

  const showClientSecret = computed(() => {
    return loginForm.value.loginType === 'oauth2' && 
           ['client_credentials', 'authorization_code'].includes(loginForm.value.grantType);
  });

  const showCode = computed(() => {
    return loginForm.value.loginType === 'oauth2' && loginForm.value.grantType === 'authorization_code';
  });

  const showState = computed(() => {
    return loginForm.value.loginType === 'oauth2' && loginForm.value.grantType === 'authorization_code';
  });

  const showOrgAlias = computed(() => {
    return loginForm.value.loginType === 'salesforce_cli';
  });

  const showSessionId = computed(() => {
    return loginForm.value.loginType === 'session_id';
  });

  const showLoginUrl = computed(() => {
    return ['oauth2', 'legacy_credential', 'session_id'].includes(loginForm.value.loginType);
  });

    // 处理下拉菜单点击
  const handleLoginUrlPreset = (command) => {
    if (command === 'custom') {
      // 场景：用户选择了“自定义”
      isCustomLoginUrl.value = true; // 解锁输入框
      loginForm.loginUrl = '';       // 可选：清空当前值方便用户输入，或者保留原值
    } else {
      // 场景：用户选择了“生产”或“沙盒”
      isCustomLoginUrl.value = false; // 锁定输入框
      loginForm.loginUrl = command;   // 填入预设 URL
    }
  };

  // --- 校验规则 ---
  const rules = computed(() => {
    const baseRules = {
      loginType: [{ required: true, message: '请选择登录类型', trigger: 'change' }]
    };

    if (loginForm.value.loginType === 'oauth2') {
      baseRules.grantType = [{ required: true, message: '请选择授权类型', trigger: 'change' }];
      
      if (loginForm.value.grantType === 'password') {
        baseRules.username = [{ required: true, message: '请输入用户名', trigger: 'blur' }];
        baseRules.password = [{ required: true, message: '请输入密码', trigger: 'blur' }];
      } else if (loginForm.value.grantType === 'client_credentials') {
        baseRules.clientId = [{ required: true, message: '请输入客户端ID', trigger: 'blur' }];
        baseRules.clientSecret = [{ required: true, message: '请输入客户端密钥', trigger: 'blur' }];
      } else if (loginForm.value.grantType === 'authorization_code') {
        baseRules.clientId = [{ required: true, message: '请输入客户端ID', trigger: 'blur' }];
        baseRules.clientSecret = [{ required: true, message: '请输入客户端密钥', trigger: 'blur' }];
        baseRules.code = [{ required: true, message: '请输入授权码', trigger: 'blur' }];
      }
    } else if (loginForm.value.loginType === 'legacy_credential') {
      baseRules.username = [{ required: true, message: '请输入用户名', trigger: 'blur' }];
      baseRules.password = [{ required: true, message: '请输入密码', trigger: 'blur' }];
    } else if (loginForm.value.loginType === 'salesforce_cli') {
      baseRules.orgAlias = [{ required: true, message: '请输入组织别名', trigger: 'blur' }];
    } else if (loginForm.value.loginType === 'session_id') {
      baseRules.sessionId = [{ required: true, message: '请输入Session ID', trigger: 'blur' }];
    }

    return baseRules;
  });

  // --- 生命周期 ---
  // 需求点2：点击进入页面时，自动获取当前登录信息
  onMounted(() => {
    handleGetCurrentInfo();
  });

  // --- 事件处理 ---
  const handleLoginTypeChange = () => {
    loginForm.value.grantType = 'password';
    resetFormFields();
  };

  const handleGrantTypeChange = () => {
    resetFormFields();
  };


  const resetFormFields = () => {
    loginForm.value.username = '';
    loginForm.value.password = '';
    loginForm.value.securityToken = '';
    loginForm.value.clientId = '';
    loginForm.value.clientSecret = '';
    loginForm.value.code = '';
    loginForm.value.state = '';
    loginForm.value.orgAlias = '';
    loginForm.value.sessionId = '';
    loginForm.value.loginUrl = '';
  };

  const handleLogin = async () => {
    if (!loginFormRef.value) return;
    
    await loginFormRef.value.validate(async (valid) => {
      if (valid) {
        loginLoading.value = true;
        try {
          const requestData = {
            loginType: loginForm.value.loginType,
            ...loginForm.value // 简单展开，后端通常会忽略多余字段，或者手动构建如原代码
          };
          
          // 手动清理多余字段以保持请求体干净 (可选，视后端宽容度而定，这里保留原逻辑结构但简化写法)
          // 实际项目中建议深拷贝并删除无效key，或者按原代码逐个赋值
          
          const response = await doLogin(requestData);
          
          if (response.code === 200) {
            loginResult.value = response.data;
            ElMessage.success('登录成功');
          } else {
            loginResult.value = { success: false, errorCode: response.code, errorMessage: response.msg };
            ElMessage.error(response.msg || '登录失败');
          }
        } catch (error) {
          loginResult.value = { success: false, errorCode: error.code || 'ERROR', errorMessage: error.msg || error.message };
          ElMessage.error(error.msg || '登录失败');
        } finally {
          loginLoading.value = false;
        }
      }
    });
  };

  const handleAutoLogin = async () => {
    autoLoginLoading.value = true;
    try {
      const response = await autoLogin();
      if (response.code === 200) {
        loginResult.value = response.data;
        ElMessage.success('自动登录成功');
      } else {
        ElMessage.error(response.msg || '自动登录失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '自动登录失败');
    } finally {
      autoLoginLoading.value = false;
    }
  };

  const handleGetCurrentInfo = async () => {
    currentLoading.value = true;
    try {
      const response = await getCurrentLoginInfo();
      if (response.code === 200 && response.data) {
        loginResult.value = response.data;
        // 如果静默获取成功，可以不弹窗打扰用户，或者仅在手动点击时弹窗
        // ElMessage.success('已加载当前会话'); 
      } else {
        // 获取失败通常意味着未登录，置空即可
        loginResult.value = null;
      }
    } catch (error) {
      // 忽略自动获取时的错误，避免页面一进来就飘红
      console.warn('Auto fetch info failed:', error);
    } finally {
      currentLoading.value = false;
    }
  };

  const handleLogout = async () => {
    try {
      await ElMessageBox.confirm('确定要销毁当前会话吗?', '警告', {
        confirmButtonText: '确定登出',
        cancelButtonText: '取消',
        type: 'warning'
      });
      
      logoutLoading.value = true;
      const response = await logout();
      if (response.code === 200) {
        loginResult.value = null;
        ElMessage.success('会话已销毁');
      } else {
        ElMessage.error(response.msg || '登出失败');
      }
    } catch (error) {
      if (error !== 'cancel') ElMessage.error(error.msg || '操作失败');
    } finally {
      logoutLoading.value = false;
    }
  };

  const copyText = (text) => {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(text).then(() => ElMessage.success('已复制 Session ID'));
    } else {
      // Fallback
      const input = document.createElement('input');
      input.setAttribute('value', text);
      document.body.appendChild(input);
      input.select();
      document.execCommand('copy');
      document.body.removeChild(input);
      ElMessage.success('已复制 Session ID');
    }
  };
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.main-row {
  max-width: 1400px;
  margin: 0 auto;
}

/* 卡片通用样式 */
.operation-card, .result-card {
  border-radius: 8px;
  border: none;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 左侧表单样式 */
.full-width-radio {
  display: flex;
  width: 100%;
}
.full-width-radio :deep(.el-radio-button__inner) {
  flex: 1;
  width: 100%;
}
.full-width-radio :deep(.el-radio-button) {
  flex: 1;
  display: flex;
}

.main-login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
  font-weight: 500;
  letter-spacing: 1px;
}

.divider-text {
  margin: 24px 0 12px;
  font-size: 12px;
  color: #909399;
  text-align: center;
  position: relative;
}
.divider-text::before, .divider-text::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 35%;
  height: 1px;
  background: #e4e7ed;
}
.divider-text::before { left: 0; }
.divider-text::after { right: 0; }

/* 底部按钮行 - 需求点3 */
.action-row {
  display: flex;
  gap: 12px;
  justify-content: space-between;
}

.flex-btn {
  flex: 1;
  margin: 0 !important; /* 覆盖element默认margin */
}

/* 右侧结果样式 */
.result-content {
  padding: 10px;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.code-block {
  font-family: monospace;
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  word-break: break-all;
}

.mono-text {
  font-family: monospace;
  color: #606266;
}

.mb-4 {
  margin-bottom: 16px;
}
.mt-4 {
  margin-top: 16px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-text {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .main-row {
    margin: 0;
  }
  .operation-card {
    margin-bottom: 20px;
  }
}
</style>
<!-- src/components/mypage/PasswordChange.vue -->
<template>
  <div class="mypage-section">
    <h2 class="section-title">비밀번호 변경</h2>
    <div class="form-wrapper">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handlePasswordChange(formRef)">
        <el-form-item label="기존 비밀번호" prop="currentPassword">
          <el-input 
            v-model="form.currentPassword" 
            type="password" 
            show-password 
            size="large"
          />
        </el-form-item>
        <el-form-item label="새로운 비밀번호" prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            show-password 
            size="large"
          />
        </el-form-item>
        <el-form-item label="비밀번호 확인" prop="passwordConfirm">
          <el-input 
            v-model="form.passwordConfirm" 
            type="password" 
            show-password 
            size="large"
          />
          <!-- <p class="error-message">*비밀번호가 일치하지 않습니다.</p> -->
        </el-form-item>
        <el-form-item>
          <el-button 
            class="action-btn" 
            type="primary" 
            size="large" 
            native-type="submit" 
            :loading="isLoading"
            :disabled="isLoading"
          >
            변경
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElNotification } from 'element-plus';
import api from '@/api/axios';

const formRef = ref();
const isLoading = ref(false);

const form = reactive({
  currentPassword: '',
  newPassword: '',
  passwordConfirm: '',
});

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('새 비밀번호를 입력해주세요.'))
  } else {
    const passPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
    if (!passPattern.test(value)) {
      callback(new Error('8~16자 영문, 숫자, 특수문자를 포함해야 합니다.'))
    }
    if (formRef.value) {
      formRef.value.validateField('passwordConfirm', () => null)
    }
    callback()
  }
}
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('비밀번호를 다시 입력해주세요.'))
  } else if (value !== form.newPassword) {
    callback(new Error("비밀번호가 일치하지 않습니다."))
  } else {
    callback()
  }
}

const rules = reactive({
  currentPassword: [
    { required: true, message: '기존 비밀번호를 입력해주세요.', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  passwordConfirm: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
});

const handlePasswordChange = async (formEl) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      isLoading.value = true;
      try {
        const response = await api.patch('/api/v1/user/me/password', {
          oldPassword: form.currentPassword,
          newPassword: form.newPassword,
        });
        if (response.data.success) {
          ElNotification({ title: '성공', message: '비밀번호가 성공적으로 변경되었습니다.', type: 'success' });
          // Clear form fields
          form.currentPassword = '';
          form.newPassword = '';
          form.passwordConfirm = '';
          formEl.resetFields(); // Reset validation states
        } else {
          ElNotification({ title: '오류', message: response.data.message || '비밀번호 변경 실패', type: 'error' });
        }
      } catch (error) {
        ElNotification({ title: '오류', message: error.response?.data?.message || '비밀번호 변경 중 오류 발생', type: 'error' });
      } finally {
        isLoading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.mypage-section {
  margin-bottom: 60px;
}
.form-wrapper {
  max-width: 500px;
  margin: 0 auto;
}
.section-title {
  color: #f5eede;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(245, 238, 222, 0.1);
}
.action-btn {
  width: 100%; /* Make button full width for a cleaner appearance */
  background-color: #e4b13e;
  border-color: #e4b13e;
  color: #1c1710;
  font-weight: bold;
}
.action-btn:hover {
  background-color: #f8c355;
  border-color: #f8c355;
}
:deep(.el-form-item__label) {
  color: #d1c7b8 !important;
  font-weight: 500;
}
:deep(.el-input__wrapper) {
  background-color: #2b231a;
  box-shadow: none;
  border: 1px solid #4a4238;
}
:deep(.el-input__inner) {
  color: #f5eede;
}
</style>

<!-- src/views/auth/FindPasswordView.vue -->
<template>
  <AuthLayout>
    <AuthCard>
      <h1 class="title">비밀번호찾기</h1>
      <el-form
        ref="formRef"
        :model="resetForm"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleResetPassword(formRef)"
      >
        <el-form-item label="이메일" prop="email">
          <el-input 
            v-model="resetForm.email" 
            placeholder="이메일을 입력하세요" 
            size="large"
            :disabled="isSendingCode || verificationSent"
          >
            <template #append>
              <el-button @click="handleSendCode" :loading="isSendingCode" :disabled="verificationSent">인증</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="인증번호" prop="verificationCode">
          <el-input 
            v-model="resetForm.verificationCode" 
            placeholder="인증번호를 입력하세요" 
            size="large"
            :disabled="!verificationSent || codeVerified"
          >
            <template #append>
              <el-button @click="handleVerifyCode" :loading="isVerifyingCode" :disabled="!verificationSent || codeVerified">
                {{ codeVerified ? '확인완료' : '확인' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="새로운 비밀번호" prop="newPassword">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="8~16자 영문/숫자/특수문자 조합"
            show-password
            size="large"
            :disabled="!codeVerified"
          />
        </el-form-item>

        <el-form-item label="비밀번호 확인" prop="passwordConfirm">
          <el-input
            v-model="resetForm.passwordConfirm"
            type="password"
            placeholder="비밀번호를 다시 입력하세요"
            show-password
            size="large"
            :disabled="!codeVerified"
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            class="complete-btn" 
            type="primary" 
            native-type="submit" 
            :loading="isResettingPassword"
            :disabled="!codeVerified"
            size="large"
          >
            완료
          </el-button>
        </el-form-item>
      </el-form>
    </AuthCard>
  </AuthLayout>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElNotification } from 'element-plus';
import AuthLayout from "@/layouts/AuthLayout.vue";
import AuthCard from "@/components/auth/AuthCard.vue";
import api from "@/api/axios";

const router = useRouter();
const formRef = ref();

const isSendingCode = ref(false);
const isVerifyingCode = ref(false);
const isResettingPassword = ref(false);
const verificationSent = ref(false);
const codeVerified = ref(false);

const resetForm = reactive({
  email: "",
  verificationCode: "",
  newPassword: "",
  passwordConfirm: "",
});

// --- Validation Rules ---
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
  } else if (value !== resetForm.newPassword) {
    callback(new Error("비밀번호가 일치하지 않습니다."))
  } else {
    callback()
  }
}

const rules = reactive({
  email: [
    { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
    { type: 'email', message: '유효한 이메일 주소를 입력해주세요.', trigger: ['blur', 'change'] }
  ],
  verificationCode: [{ required: true, message: '인증번호를 입력해주세요.', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  passwordConfirm: [{ required: true, validator: validatePass2, trigger: 'blur' }],
});


// --- Handlers ---
const handleSendCode = async () => {
  await formRef.value?.validateField('email', async (isValid) => {
    if (isValid) {
      isSendingCode.value = true;
      verificationSent.value = false;
      codeVerified.value = false;
      try {
        await api.post('/api/v1/auth/password/send-code', { email: resetForm.email });
        ElNotification({ title: '성공', message: '인증번호가 발송되었습니다.', type: 'success' });
        verificationSent.value = true;
      } catch (error) {
        ElNotification({ title: '오류', message: error.response?.data?.message || '인증번호 발송 실패', type: 'error' });
      } finally {
        isSendingCode.value = false;
      }
    }
  });
};

const handleVerifyCode = async () => {
  await formRef.value?.validateField(['email', 'verificationCode'], async (isValid) => {
    if (isValid) {
      isVerifyingCode.value = true;
      try {
        await api.post('/api/v1/auth/password/verify-code', { email: resetForm.email, code: resetForm.verificationCode });
        ElNotification({ title: '성공', message: '인증번호가 확인되었습니다.', type: 'success' });
        codeVerified.value = true;
      } catch (error) {
        ElNotification({ title: '오류', message: error.response?.data?.message || '인증번호 확인 실패', type: 'error' });
        codeVerified.value = false;
      } finally {
        isVerifyingCode.value = false;
      }
    }
  });
};

const handleResetPassword = async (formEl) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      if (!codeVerified.value) {
        ElNotification({ title: '오류', message: '인증번호 확인을 먼저 완료해주세요.', type: 'error' });
        return;
      }
      isResettingPassword.value = true;
      try {
        await api.post('/api/v1/auth/password/reset', {
          email: resetForm.email,
          newPassword: resetForm.newPassword,
          confirmPassword: resetForm.passwordConfirm
        });
        ElNotification({ title: '성공', message: '비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.', type: 'success' });
        router.push('/login');
      } catch (error) {
        ElNotification({ title: '오류', message: error.response?.data?.message || '비밀번호 변경 실패', type: 'error' });
      } finally {
        isResettingPassword.value = false;
      }
    }
  });
};
</script>

<style scoped>
.title {
  color: white;
  text-align: center;
  margin-bottom: 30px;
}

:deep(.el-form-item__label) {
  color: white !important;
  margin-bottom: 5px !important;
}

.el-form-item {
  margin-bottom: 25px;
}

.complete-btn {
  width: 100%;
  height: 45px;
  margin-top: 20px;
  font-weight: bold;
  background-color: #ffc933;
  border-color: #ffc933;
}
</style>

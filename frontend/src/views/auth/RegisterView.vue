<!-- src/views/auth/RegisterView.vue -->
<template>
  <AuthLayout>
    <AuthCard>
      <h1 class="title">회원가입</h1>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister(registerFormRef)"
      >
        <el-form-item label="이메일" prop="userEmail">
          <el-input 
            v-model="registerForm.userEmail" 
            placeholder="이메일을 입력하세요" 
            size="large"
            :disabled="isEmailVerified"
          >
            <template #append>
              <el-button @click="handleSendCode" :loading="isSendingCode" :disabled="isEmailVerified">인증</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="인증번호" prop="verificationCode">
          <el-input 
            v-model="registerForm.verificationCode" 
            placeholder="인증번호를 입력하세요" 
            size="large"
            :disabled="isEmailVerified"
          >
            <template #append>
              <el-button @click="handleVerifyCode" :loading="isVerifyingCode" :disabled="isEmailVerified">
                {{ isEmailVerified ? '인증완료' : '확인' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="닉네임" prop="userNickname">
          <el-input v-model="registerForm.userNickname" placeholder="2~10자 이내의 한글, 영문, 숫자" size="large" />
        </el-form-item>

        <el-form-item label="비밀번호" prop="userPassword">
          <el-input
            v-model="registerForm.userPassword"
            type="password"
            placeholder="8~16자 영문/숫자/특수문자 조합"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item label="비밀번호 확인" prop="passwordConfirm">
          <el-input
            v-model="registerForm.passwordConfirm"
            type="password"
            placeholder="비밀번호를 다시 입력하세요"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            class="register-btn" 
            type="primary" 
            native-type="submit" 
            :loading="isRegistering"
            size="large"
          >
            회원가입
          </el-button>
        </el-form-item>
      </el-form>
    </AuthCard>
  </AuthLayout>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElNotification } from 'element-plus'
import AuthLayout from "@/layouts/AuthLayout.vue";
import AuthCard from "@/components/auth/AuthCard.vue";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const registerFormRef = ref();

const isSendingCode = ref(false);
const isVerifyingCode = ref(false);
const isEmailVerified = ref(false);
const isRegistering = ref(false);

const registerForm = reactive({
  userEmail: "",
  verificationCode: "",
  userNickname: "",
  userPassword: "",
  passwordConfirm: "",
});

// --- Validation Rules ---

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('비밀번호를 입력해주세요.'))
  } else {
    const passPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
    if (!passPattern.test(value)) {
      callback(new Error('8~16자 영문, 숫자, 특수문자를 포함해야 합니다.'))
    }
    if (registerFormRef.value) {
      registerFormRef.value.validateField('passwordConfirm', () => null)
    }
    callback()
  }
}
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('비밀번호를 다시 입력해주세요.'))
  } else if (value !== registerForm.userPassword) {
    callback(new Error("비밀번호가 일치하지 않습니다."))
  } else {
    callback()
  }
}

const rules = reactive({
  userEmail: [
    { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
    { type: 'email', message: '유효한 이메일 주소를 입력해주세요.', trigger: ['blur', 'change'] }
  ],
  verificationCode: [{ required: true, message: '인증번호를 입력해주세요.', trigger: 'blur' }],
  userNickname: [
    { required: true, message: '닉네임을 입력해주세요.', trigger: 'blur' },
    { min: 2, max: 10, message: '2~10자 이내로 입력해주세요.', trigger: 'blur' },
    { pattern: /^[가-힣A-Za-z0-9]+$/, message: '특수문자와 공백은 사용할 수 없습니다.', trigger: 'blur'}
  ],
  userPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  passwordConfirm: [{ required: true, validator: validatePass2, trigger: 'blur' }],
});


// --- Handlers ---

const handleSendCode = async () => {
  await registerFormRef.value?.validateField('userEmail', async (isValid) => {
    if (isValid) {
      isSendingCode.value = true;
      const success = await authStore.sendVerificationCode(registerForm.userEmail);
      isSendingCode.value = false;
      if (success) {
        ElNotification({ title: '성공', message: '인증번호가 발송되었습니다.', type: 'success' });
      } else {
        ElNotification({ title: '오류', message: '인증번호 발송에 실패했습니다.', type: 'error' });
      }
    }
  });
};

const handleVerifyCode = async () => {
  await registerFormRef.value?.validateField(['userEmail', 'verificationCode'], async (isValid) => {
    if (isValid) {
      isVerifyingCode.value = true;
      const success = await authStore.verifyEmail({
        email: registerForm.userEmail,
        code: registerForm.verificationCode,
      });
      isVerifyingCode.value = false;
      if (success) {
        isEmailVerified.value = true;
        ElNotification({ title: '성공', message: '이메일 인증이 완료되었습니다.', type: 'success' });
      } else {
        ElNotification({ title: '오류', message: '인증번호가 올바르지 않습니다.', type: 'error' });
      }
    }
  });
};

const handleRegister = async (formEl) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      if (!isEmailVerified.value) {
        ElNotification({ title: '오류', message: '이메일 인증을 먼저 완료해주세요.', type: 'error' });
        return;
      }
      isRegistering.value = true;
      try {
        await authStore.signup({
          userEmail: registerForm.userEmail,
          userPassword: registerForm.userPassword,
          userNickname: registerForm.userNickname,
        });
      } finally {
        isRegistering.value = false;
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

.register-btn {
  width: 100%;
  height: 45px;
  margin-top: 20px;
  font-weight: bold;
  background-color: #ffc933;
  border-color: #ffc933;
}
</style>

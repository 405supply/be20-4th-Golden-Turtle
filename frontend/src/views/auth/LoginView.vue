<!-- src/views/LoginView.vue -->
<template>
  <AuthLayout>
    <AuthCard>
      <h1 class="title">로그인</h1>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin(loginFormRef)"
      >
        <el-form-item label="이메일" prop="userEmail">
          <el-input v-model="loginForm.userEmail" placeholder="이메일을 입력하세요" size="large"/>
        </el-form-item>

        <el-form-item label="비밀번호" prop="userPassword">
          <el-input 
            v-model="loginForm.userPassword" 
            type="password" 
            placeholder="비밀번호를 입력하세요" 
            show-password 
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            class="login-btn" 
            type="primary" 
            native-type="submit"
            :loading="isLoading"
            size="large"
          >
            로그인
          </el-button>
        </el-form-item>
      </el-form>

      <div class="links">
        <RouterLink to="/register">회원가입</RouterLink>
        <RouterLink to="/find-password">비밀번호찾기</RouterLink>
      </div>
    </AuthCard>
  </AuthLayout>
</template>

<script setup>
import { reactive, ref } from "vue";
import AuthLayout from "@/layouts/AuthLayout.vue";
import AuthCard from "@/components/auth/AuthCard.vue";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const loginFormRef = ref();
const isLoading = ref(false);

const loginForm = reactive({
  userEmail: "",
  userPassword: "",
});

const rules = reactive({
  userEmail: [
    { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
    { type: 'email', message: '유효한 이메일 주소를 입력해주세요.', trigger: ['blur', 'change'] }
  ],
  userPassword: [
    { required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' },
  ]
});

const handleLogin = async (formEl) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      isLoading.value = true;
      try {
        await authStore.login({
          userEmail: loginForm.userEmail,
          userPassword: loginForm.userPassword,
        });
      } finally {
        isLoading.value = false;
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

/* Element Plus의 Form Item 기본 스타일을 오버라이드 */
.el-form-item {
  margin-bottom: 25px;
}

/* Element Plus의 label 스타일을 오버라이드 */
:deep(.el-form-item__label) {
  color: white !important;
  margin-bottom: 5px !important;
}

.login-btn {
  width: 100%;
  height: 45px;
  margin-top: 20px;
  font-weight: bold;
  background-color: #ffc933;
  border-color: #ffc933;
}

.links {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.links a {
  color: white;
  text-decoration: none;
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }

  .links {
    font-size: 12px;
  }
}
</style>

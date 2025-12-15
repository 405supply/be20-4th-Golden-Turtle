<!-- src/layouts/AuthLayout.vue -->
<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goToLogin = () => {
  router.push('/login');
};
</script>

<template>
  <div class="auth-wrapper">
    <div class="left-area" @click="goToLogin">
      <img src="@/assets/GOLDENTURTLE.svg" class="logo" />
      <h1 class="brand">Golden Turtle</h1>
    </div>

    <div class="right-area">
      <slot /> <!-- 로그인/회원가입/비번찾기 내용이 여기에 들어옴 -->
    </div>
  </div>
</template>

<style scoped>
.auth-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;
  background: #1f1b13;
}

.left-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ffc933;
  cursor: pointer; /* Add cursor pointer to indicate clickability */
}

.logo {
  width: 400px; /* Increased size */
  margin-left: 50px; /* Moved slightly to the right */
}

.brand {
  margin-top: 20px;
  font-size: 60px; /* Increased font size */
  font-weight: bold;
}

.right-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .auth-wrapper {
    flex-direction: column;
  }

  .logo {
    width: 300px; /* Adjusted responsive size */
    margin-left: 0; /* Reset margin for smaller screens */
  }

  .brand {
    font-size: 48px; /* Adjusted responsive font size */
  }
}
</style>

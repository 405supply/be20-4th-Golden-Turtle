<!-- src/views/mypage/MyPageView.vue -->
<template>
  <div class="mypage-container">
            <div class="mypage-inner-content">
              <MyInfo />
              <PasswordChange />
              <AccountWithdrawal />
            </div>  </div>
</template>

<script setup>
import MyInfo from '@/components/mypage/MyInfo.vue';
import PasswordChange from '@/components/mypage/PasswordChange.vue';
import AccountWithdrawal from '@/components/mypage/AccountWithdrawal.vue';
</script>

<style scoped>
.mypage-container {
  padding: 40px;
  max-width: 800px; /* Limit the max width for better readability on large screens */
  width: 100%; /* Ensure it takes full available width within its parent */
  margin: 0 auto; /* Center the container */
}

.mypage-inner-content {
  width: 100%; /* Take full width of its parent (.mypage-container) */
  padding: 20px; /* Add some padding inside the content area */
  margin: 0 auto; /* Center the inner content if it doesn't take full width (e.g., if max-width were applied here) */
}

.mypage-section {
  margin: 0 auto; /* Center individual sections horizontally */
}
</style>

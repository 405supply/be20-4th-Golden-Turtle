<!-- src/components/mypage/AccountWithdrawal.vue -->
<template>
  <div class="mypage-section">
    <h2 class="section-title">회원 탈퇴</h2>
    <div class="form-wrapper">
      <p class="withdrawal-description">
        회원 탈퇴 시 계정은 영구적으로 삭제되며 복구할 수 없습니다.
      </p>
      <el-button
        class="withdrawal-btn"
        type="danger"
        plain
        @click="handleWithdrawal"
        :loading="isLoading"
        :disabled="isLoading"
      >
        회원탈퇴
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElNotification, ElMessageBox } from 'element-plus';
import api from '@/api/axios';
import { useAuthStore } from '@/stores/auth'; // Import auth store for logout

const authStore = useAuthStore();
const isLoading = ref(false);

const handleWithdrawal = async () => {
  try {
    await ElMessageBox.confirm('정말로 회원탈퇴 하시겠습니까? 이 작업은 되돌릴 수 없습니다.', '경고', {
      confirmButtonText: '탈퇴',
      cancelButtonText: '취소',
      type: 'warning',
    });

    isLoading.value = true;
    const response = await api.delete('/api/v1/user/me/delete');

    if (response.data.success) {
      ElNotification({ title: '성공', message: '회원탈퇴가 완료되었습니다.', type: 'success' });
      await authStore.logout(); // Logout and redirect to login page
    } else {
      ElNotification({ title: '오류', message: response.data.message || '회원탈퇴 실패', type: 'error' });
    }
  } catch (error) {
    if (error === 'cancel') {
      ElNotification({ title: '취소', message: '회원탈퇴가 취소되었습니다.', type: 'info' });
    } else {
      ElNotification({ title: '오류', message: error.response?.data?.message || '회원탈퇴 중 오류 발생', type: 'error' });
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* This section is designed to be the last on the page, so no margin-bottom is needed. */
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
.withdrawal-description {
  margin-bottom: 24px;
  color: #d1c7b8;
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
  padding: 16px;
  background-color: rgba(240, 68, 68, 0.05);
  border: 1px solid rgba(240, 68, 68, 0.2);
  border-radius: 8px;
}
.withdrawal-btn {
  width: 100%;
  font-weight: bold;
}
</style>


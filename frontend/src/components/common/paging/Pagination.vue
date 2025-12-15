<template>
  <div class="pagination-number">
    <!-- 이전 버튼 -->
    <span
        class="page-arrow"
        :class="{ disabled: currentPage === 1 }"
        @click="goPrev"
    >
      &lt;
    </span>

    <!-- 페이지 번호들 -->
    <span
        v-for="page in totalPages"
        :key="page"
        class="page-num"
        :class="{ active: currentPage === page }"
        @click="goPage(page)"
    >
      {{ page }}
    </span>

    <!-- 다음 버튼 -->
    <span
        class="page-arrow"
        :class="{ disabled: currentPage === totalPages }"
        @click="goNext"
    >
      &gt;
    </span>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['update:currentPage']);

const goPrev = () => {
  if (props.currentPage > 1) {
    emit('update:currentPage', props.currentPage - 1);
  }
};

const goNext = () => {
  if (props.currentPage < props.totalPages) {
    emit('update:currentPage', props.currentPage + 1);
  }
};

const goPage = (page) => {
  emit('update:currentPage', page);
};
</script>

<style scoped>
/* MainContent.css에서 페이징 관련 스타일을 가져옴 */
.pagination-number {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  user-select: none;
}

.page-num,
.page-arrow {
  margin: 0 10px;
  cursor: pointer;
  color: #ccc;
  font-size: 1.2rem;
  font-weight: bold;
}

.page-num.active {
  color: #FFD700; /* 활성 페이지 색상 */
}

.page-arrow.disabled {
  color: #444; /* 비활성 화살표 색상 */
  cursor: not-allowed;
}
</style>

<template>
  <div class="layout">

    <Header />

    <div class="layout-body">
      <div class="layout-sidebar">
        <Sidebar @open-modal="openModal" />
      </div>

      <main class="layout-main">
        <router-view />
      </main>
    </div>

    <ResultModal
        v-if="showModal"
        :result="modalData"
        @close="closeModal"
        class="modal-root"
    />
  </div>
</template>

<script setup>
import { ref } from "vue"
import Header from "@/components/layout/header/Header.vue"
import Sidebar from "@/components/layout/sidebar/Sidebar.vue"
import ResultModal from "@/components/modal/ModalResult.vue"

const showModal = ref(false)

const modalData = ref({
  rank: 1,
  nickname: "김폭주기관차",
  totalAsset: 38732000,
  returnRate: 387.32
})

const openModal = (data) => {
  modalData.value = data
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  background-color: #211a0f; /* Darker, richer background */
  position: relative; /* For modal positioning */
}

.layout-body {
  display: flex;
  flex: 1;
  padding: 20px; /* Spacing around the main layout elements */
  gap: 20px; /* Space between sidebar and content */
  height: calc(100vh - 84px); /* Assumes header is 84px */
  overflow: hidden;
}

.layout-sidebar {
  width: 272px;
  flex-shrink: 0;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #2f281e; /* A slightly lighter, distinct panel color */
  border-radius: 16px; /* Modern rounded corners */
  transition: background-color 0.3s ease;
}

.layout-sidebar::-webkit-scrollbar {
  width: 6px;
}

.layout-sidebar::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.layout-main {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 40px; /* Generous padding for content */
  overflow-y: auto;
  min-width: 0;
  background-color: #2f281e; /* Same panel color for a unified look */
  border-radius: 16px; /* Match sidebar rounding */
}

.layout-main::-webkit-scrollbar {
  width: 8px;
}

.layout-main::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
}

/* 모달이 항상 레이아웃 맨 위에 오도록 */
.modal-root {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 9999;
}
</style>

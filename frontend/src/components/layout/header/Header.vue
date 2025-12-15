<template>
  <header class="gt-header">

    <!-- Left Logo -->
    <div class="gt-header-left" @click="goToMain">
      <img src="@/assets/GOLDENTURTLE.svg" alt="logo" class="gt-logo" />
      <span class="gt-title">Golden Turtle</span>
    </div>

    <!-- Right Navigation -->
    <nav class="gt-nav">
      <a href="/mypage">Mypage</a>
      <a @click="goHistory" href="#">History</a>
      <router-link to="/stocklist">Market</router-link>
      <a href="/Portfolio">Portfolio</a>
      <!-- Modified logout link to trigger handleLogout function -->
      <a @click.prevent="handleLogout" href="#">Logout</a>
    </nav>

  </header>

  <div class="gt-header-divider"></div>
</template>

<script setup>
import "@/assets/header/Header.css"
import {useRoute, useRouter} from "vue-router";
import { useAuthStore } from '@/stores/auth'; // New import

const authStore = useAuthStore(); // Initialize auth store
const router = useRouter();
const goHistory = () =>{
  router.push({name: 'gamesession'})
}

const handleLogout = async () => {
  await authStore.logout();
};

const goToMain = () => {
  router.push('/');
};
</script>

<template>
  <aside class="sidebar-wrapper">

    <div>
      <div class="card-panel timer-panel">
        <div class="timer-text">{{ timer }}</div>

        <div class="button-row">
          <CommonButton
              :buttonClass="isGameActive ? 'btn-disabled' : 'btn-green'"
              :disabled="isGameActive"
              @click="!isGameActive && startGame()"
          >
            시작
          </CommonButton>

          <CommonButton
              :buttonClass="isGameActive ? 'btn-red' : 'btn-disabled'"
              :disabled="!isGameActive"
              @click="emitEnd"
          >
            종료
          </CommonButton>
        </div>
      </div>

      <p class="section-title-bold">잔고</p>
      <div class="card-panel balance-panel">
        {{ cashBalance }}$
      </div>
    </div>
    <p class="section-title-bold">보유 현황</p>
    <Sidebarholdings/>


    <!-- 현재 순위 -->
    <div>
      <p class="section-title-bold">현재 순위</p>

      <div class="card-panel ranking-panel">

        <div
            v-for="rankItem in rankStore.getDisplayRanking"
            :key="rankItem.sessionId"
        >

          <div
              class="rank-item"
              :class="{
          'rank-low': rankItem.rank > 3,
    'rank-me': rankItem.sessionId === rankStore.mySessionId,
    [rankStore.rankAnimation]:
      rankStore.mySessionId &&
      rankItem.sessionId === rankStore.mySessionId
  }"
          >
            <div class="rank-left-col">
              <span v-if="rankItem.rank === 1">🥇</span>
              <span v-else-if="rankItem.rank === 2">🥈</span>
              <span v-else-if="rankItem.rank === 3">🥉</span>
              <span v-else class="rank-number">{{ rankItem.rank }}등</span>

              <span class="rank-nickname">
                {{ rankItem.nickname }}

                <span
                    v-if="rankItem.isMe"
                    class="me-badge"
                    style="color: #2ecc71; font-weight: bold; margin-left: 5px;"
                >
                  (나)
                </span>
              </span>
            </div>

            <div class="rank-money">
              $ {{ formatNumber(rankItem.totalAsset) }}
            </div>
          </div>

        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import "@/assets/sidebar/Sidebar.css";
import {onMounted, ref} from "vue"
import {useAccountStore} from '@/stores/Account.js'
import {storeToRefs} from 'pinia'
import CommonButton from "@/components/common/button/CommonButton.vue"
import Sidebarholdings from "@/components/layout/sidebar/Sidebarholdings.vue"
import "@/assets/sidebar/Sidebar.css"
import api from "@/api/axios";
import {useRouter} from "vue-router";

const router = useRouter();
const emit = defineEmits(["open-modal"])
const isGameActive = ref(false)
const accountStore = useAccountStore()
// ========= TIMER ==========
const totalSeconds = ref(600); // 10분 = 600초
import {useRankStore} from "@/stores/rank.js";

const rankStore = useRankStore();

const sessionId = ref(null);

// 천단위 콤마
const formatNumber = (n) =>
    n?.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? "0";

// ===== 타이머 =====
const timer = ref("00 : 10 : 00");
let timerInterval = null;

const {cashBalance} = storeToRefs(accountStore)

const formatTime = (sec) =>
    `${String(Math.floor(sec / 3600)).padStart(2, "0")} : ` +
    `${String(Math.floor((sec % 3600) / 60)).padStart(2, "0")} : ` +
    `${String(sec % 60).padStart(2, "0")}`;

const startCountdown = () => {
  timerInterval = setInterval(() => {
    if (totalSeconds.value <= 0) {
      clearInterval(timerInterval);
      endGame();
      return;
    }
    totalSeconds.value--;
    timer.value = formatTime(totalSeconds.value);
  }, 1000);
};
// ===== 게임 시작 =====
const startGame = async () => {
  try {
    const res = await api.post(`/api/v1/game-session/start`);
    sessionId.value = res.data.data;

    localStorage.setItem("gameSessionId", sessionId.value.toString());

    isGameActive.value = true;
    totalSeconds.value = 600;
    timer.value = formatTime(600);

    localStorage.setItem("gameStartTime", Date.now().toString());
    setTimeout(() => {
      window.location.href = "/stocklist";
    },);

    startCountdown();
    rankStore.connectStomp(sessionId.value); // STOMP 연결
    accountStore.loadCashBalance();
  } catch (e) {
    console.error("게임 시작 실패", e);
  }
};

const endGame = async () => {
  try {
    const res = await api.post(`/api/v1/game-session/end`);
    emit("open-modal", res.data.data);

    clearInterval(timerInterval);

    cashBalance.value = 0;
    totalSeconds.value = 600;
    timer.value = formatTime(600);
    isGameActive.value = false;

    localStorage.removeItem("gameSessionId");
    localStorage.removeItem("gameStartTime");

    setTimeout(() => {
      window.location.href = "/";
    }, 3000);

  } catch (e) {
    console.error("게임 종료 실패", e);
  }
};

const emitEnd = () => endGame();

const loadPage = () => {
  accountStore.loadCashBalance()
}

// Mock 화면용 데이터

// ===== 새로고침 시 타이머 복구 =====
onMounted(() => {
  loadPage()
  const savedSessionId = localStorage.getItem("gameSessionId");
  if (savedSessionId) {
    sessionId.value = Number(savedSessionId);

    rankStore.connectStomp(sessionId.value);
  }

  const saved = localStorage.getItem("gameStartTime");
  if (saved) {
    const elapsed = Math.floor((Date.now() - Number(saved)) / 1000);
    const remain = 600 - elapsed;

    if (remain > 0) {
      totalSeconds.value = remain;
      timer.value = formatTime(remain);
      isGameActive.value = true;
      startCountdown();
    } else {
      localStorage.removeItem("gameStartTime");
    }
  }

  rankStore.loadRanking(api);
});
</script>

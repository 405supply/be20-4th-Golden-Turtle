import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

import GameSessionFrame from "@/pages/Account/GameSessionFrame.vue";
import GameSessionDetail from "@/pages/Account/GameSessionDetail.vue";
import MainLayout from '@/layouts/MainLayout.vue';
import MainContent from '@/views/main/MainContent.vue';
import LoginView from '@/views/auth/LoginView.vue';
import RegisterView from '@/views/auth/RegisterView.vue';
import FindPasswordView from '@/views/auth/FindPasswordView.vue';
import AdminStockManager from "@/views/admin/AdminStockManager.vue";
import Portfolio from "@/pages/Account/Portfolio.vue";
import MyPageView from "@/views/mypage/MyPageView.vue";
import StockDetailView from '@/views/stock/StockDetailView.vue';
import StockListPage from '@/views/stock/StockListPage.vue';

const routes = [
    // --- Public Routes ---
    {
        path: '/login',
        name: 'login',
        component: LoginView,
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterView,
    },
    {
        path: '/find-password',
        name: 'find-password',
        component: FindPasswordView,
    },
    // 관리자 전용 페이지
    {
        path: '/adminstock',
        name: 'adminstock',
        component: AdminStockManager,
    },

    // --- Authenticated Routes ---
    {
        path: '/',
        component: MainLayout,
        children: [
            {
                path: '',
                name: 'home',
                component: MainContent,
            },
            // 마이페이지
            {
                path: '/mypage',
                name: 'mypage',
                component: MyPageView,
            },
            //Account 페이지
            {
                path: 'gamesession',
                name: 'gamesession',
                component: GameSessionFrame
            },
            {
                path: 'gamesession/:sessionId/detail',
                name: 'gamesession-detail',
                component: GameSessionDetail
            },
            //Portfolio 페이지
            {
                path: 'portfolio',
                name: 'portfolio',
                component: Portfolio
            },
            // 필요하면 여기에 다른 MainLayout 페이지들 추가
            // 종목 상세 페이지
            {
                path: 'stock/:symbol',
                name: 'stock-detail',
                component: StockDetailView,
                props: true
            },
            // 종목 리스트 페이지
            {
                path: 'stocklist',
                name: 'stock-list',
                component: StockListPage
            },
        ]
    }
];


const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();

    // 앱 로드 시 localStorage에서 인증 상태를 복원
    if (!authStore.isInitialized) {
        authStore.loadFromStorage();
    }

    const isLoggedIn = authStore.isLoggedIn;
    const isAdmin = authStore.isAdmin;

    const publicPages = ['/login', '/register', '/find-password'];  // 이 페이지들은 public (토큰 필요 없음)
    const authRequired = !publicPages.includes(to.path); // publicPages에 없으면 인증이 필요함

    // 1. 로그인이 필요한 페이지에 접근하려 하지만, 로그인되지 않은 경우
    if (authRequired && !isLoggedIn) {
        return next({
            path: '/login',
            query: { redirect: to.fullPath } // 원래 가려던 경로를 쿼리로 전달
        });
    }

    // 2. 로그인된 사용자가 로그인/회원가입 페이지에 접근하려는 경우
    if (isLoggedIn && publicPages.includes(to.path)) {
        return next('/');
    }

    // 3. 관리자 페이지에 접근하려는 경우, 관리자 권한 확인
    if (to.name === 'adminstock' && !isAdmin) {
        alert('관리자만 접근 가능합니다.');
        return next('/');
    }

    // 그 외 모든 경우, 정상적으로 이동
    next();
});

export default router;

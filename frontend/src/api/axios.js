import axios from "axios";
import { useAuthStore } from "@/stores/auth";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
    headers: {
        'Content-Type': 'application/json',
    },
});

let isRefreshing = false;
// 재시도 요청들을 저장할 배열
let failedQueue = [];

// 큐에 쌓인 요청들을 처리하는 함수
const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });

    failedQueue = [];
};

// --- Request Interceptor ---
api.interceptors.request.use(
    (config) => {
        const authStore = useAuthStore();
        const authUrls = ['/api/v1/auth/login', '/api/v1/auth/signup', '/api/v1/auth/refresh'];

        // 인증이 필요없는 요청은 그냥 보냄
        if (authUrls.some(url => config.url.includes(url))) {
            return config;
        }

        // 인증이 필요한 요청에 토큰 추가
        if (authStore.accessToken) {
            config.headers['Authorization'] = `Bearer ${authStore.accessToken}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// --- Response Interceptor ---
api.interceptors.response.use(
    (response) => response, // 성공적인 응답은 그대로 반환
    async (error) => {
        const originalRequest = error.config;
        const authStore = useAuthStore();

        // 401 또는 403 에러이고, 재시도한 요청이 아닐 경우
        if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
            
            // 로그인/리프레시 요청 자체에서 401이 발생한 경우는 재발급 로직을 타지 않음
            if (originalRequest.url.includes('/api/v1/auth/login') || originalRequest.url.includes('/api/v1/auth/refresh')) {
                return Promise.reject(error);
            }

            if (isRefreshing) {
                // 토큰 재발급이 진행 중이면, 현재 요청을 큐에 추가하고 대기
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                }).then(token => {
                    originalRequest.headers['Authorization'] = 'Bearer ' + token;
                    return api(originalRequest);
                }).catch(err => {
                    return Promise.reject(err);
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;
            console.log("Token refresh initiated..."); // Log refresh initiation

            try {
                // 토큰 재발급 시도
                const newAccessToken = await authStore.refreshTokens();
                console.log("Token refresh successful. New access token (first 10 chars):", newAccessToken.substring(0, 10) + '...'); // Log success
                // 재발급 성공 시, 실패했던 모든 요청 재실행
                processQueue(null, newAccessToken);
                // 현재 실패한 원래 요청도 재실행
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                console.error("Token refresh failed:", refreshError); // Log failure
                // 재발급 실패 시, 큐에 있던 모든 요청 실패 처리 및 로그아웃
                processQueue(refreshError, null);
                authStore.logout();
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
                console.log("Token refresh process finished."); // Log process conclusion
            }
        }

        return Promise.reject(error);
    }
);

export default api;
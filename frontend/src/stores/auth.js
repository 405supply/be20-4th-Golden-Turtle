import { defineStore } from 'pinia';
import api from '@/api/axios';
import router from '@/router';

// JWT payload를 디코딩하는 헬퍼 함수
function decodeJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Invalid token:", e);
    return null;
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null, // 사용자 정보 (디코딩된 토큰)
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    isInitialized: false, // 스토어 초기화 여부
  }),

  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    isAdmin: (state) => state.user?.role === 'ADMIN', // JWT payload의 'role' 필드를 확인
  },

  actions: {
    // 토큰과 사용자 정보를 상태와 localStorage에 설정
    _setAuthData(accessToken, refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.user = decodeJwt(accessToken);

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    },

    // 이메일 인증 코드 발송 액션
    async sendVerificationCode(email) {
      try {
        const response = await api.post('/api/v1/auth/send-verification', { email });
        if (response.data && response.data.success) {
          alert(response.data.data || '인증번호가 발송되었습니다.');
          return true;
        } else {
          alert(response.data.message || '인증번호 발송에 실패했습니다.');
          return false;
        }
      } catch (error) {
        console.error('Send verification code error:', error);
        alert('인증번호 발송 중 오류가 발생했습니다.');
        return false;
      }
    },

    // 이메일 인증 코드 검증 액션
    async verifyEmail({ email, code }) {
      try {
        const response = await api.post('/api/v1/auth/verify-email', { email, code });
        if (response.data && response.data.success) {
          alert(response.data.data || '이메일 인증이 완료되었습니다.');
          return true;
        } else {
          alert(response.data.message || '이메일 인증에 실패했습니다.');
          return false;
        }
      } catch (error) {
        console.error('Verify email error:', error);
        alert('이메일 인증 중 오류가 발생했습니다.');
        return false;
      }
    },

    // 로그인
    async login(credentials) {
      try {
        const response = await api.post('/api/v1/auth/login', {
          userEmail: credentials.userEmail.trim(),
          userPassword: credentials.userPassword.trim(),
        });

        if (response.data && response.data.success) {
          const { accessToken, refreshToken } = response.data.data;
          this._setAuthData(accessToken, refreshToken);

          await router.push('/');
          return true;
        } else {
          alert(response.data.message || '로그인에 실패했습니다.');
          return false;
        }
      } catch (error) {
        console.error('Login error:', error);

        if (error.response && error.response.status === 403) {
            alert('비활성화된 계정이거나 접근 권한이 없습니다.')
        } else {
            alert('이메일 또는 비밀번호가 잘못되었습니다.');
        }
        return false;
      }
    },

    // 로그아웃 (상태 초기화)
    async logout() {
      // 서버에 로그아웃 요청 (선택 사항)
      try {
        if(this.refreshToken) {
            await api.post('/api/v1/auth/logout', { refreshToken: this.refreshToken });
        }
      } catch (error) {
        console.error('Logout API call failed:', error);
      } finally {
        this.clearAuthState();
        alert('로그아웃 되었습니다.');
        await router.push('/login');
      }
    },

    // 토큰 재발급
    async refreshTokens() {
        if (!this.refreshToken) {
            throw new Error("No refresh token available");
        }
        try {
            const response = await api.post('/api/v1/auth/refresh', { refreshToken: this.refreshToken });
            const { accessToken, refreshToken: newRefreshToken } = response.data.data;
            this._setAuthData(accessToken, newRefreshToken);
            return accessToken;
        } catch (error) {
            console.error("Failed to refresh token:", error);
            this.clearAuthState(); // 재발급 실패 시 상태 초기화
            throw error;
        }
    },
    
    // 모든 인증 관련 상태 및 데이터를 완전히 제거
    clearAuthState() {
        this.user = null;
        this.accessToken = null;
        this.refreshToken = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    },

    // 페이지 로드 시 localStorage에서 상태 복원
    loadFromStorage() {
      const accessToken = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');

      if (accessToken && refreshToken) {
        this._setAuthData(accessToken, refreshToken);
      }
      this.isInitialized = true;
    },

    // 회원가입
    async signup(userInfo) {
        try {
          const response = await api.post('/api/v1/auth/signup', userInfo);
          if (response.data && response.data.success) {
            alert('회원가입에 성공했습니다! 로그인 페이지로 이동합니다.');
            await router.push('/login'); 
            return true;
          } else {
            alert(response.data.message || '회원가입에 실패했습니다.');
            return false;
          }
        } catch (error) {
          console.error('Signup error:', error);
          alert('회원가입 중 오류가 발생했습니다.');
          return false;
        }
      },
  }
});

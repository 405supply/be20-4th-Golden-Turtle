import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vueDevTools(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    define: {
        'global': 'window',
    },
    server: {
        // host: '0.0.0.0',
        port: 5173,
        //vue router history 모드 지원
        /*http://localhost:5173/oauth/callback?token=xxx 이러한 경로로 접근할 때
        * 실제 파일이 없네?
        * Vue SPA(index.html)로 보내서 라우터가 처리하게 하자ㅃ
        * -> /oauth/callback 라우트로 이동*/
        historyApiFallback: true
    },
})

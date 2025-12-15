import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

// 새로운 `api/axios.js`와 `router/index.js`에서 인증 관련 초기화를 자동으로 처리하므로
// main.js에서의 수동 설정은 더 이상 필요하지 않습니다.

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')

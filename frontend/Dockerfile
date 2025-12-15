# 1) Build stage (Node LTS)
FROM node:20-alpine AS build

WORKDIR /app

# 의존성 먼저 설치 (캐시 효율)
COPY package*.json ./
RUN npm ci

# 소스 복사 후 빌드
COPY . .
RUN npm run build


# 2) Run stage (Nginx로 dist 서빙)
FROM nginx:alpine

# 빌드 결과물 배포
COPY --from=build /app/dist /usr/share/nginx/html

# (선택) SPA 라우팅 필요하면 nginx.conf도 추가해야 함
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

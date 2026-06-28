# Copyright (c) Khaled Shawki. All rights reserved.
# syntax=docker/dockerfile:1.7

FROM maven:3.9.11-eclipse-temurin-25 AS api-build
WORKDIR /workspace
COPY apps/api/pom.xml apps/api/pom.xml
COPY apps/api/src apps/api/src
RUN --mount=type=cache,target=/root/.m2 cd apps/api && mvn -q -DskipTests package

FROM node:22-alpine AS web-build
WORKDIR /workspace/apps/web
COPY apps/web/package.json apps/web/package-lock.json* ./
RUN if [ -f package-lock.json ]; then npm ci --no-audit --no-fund; else npm install --no-audit --no-fund; fi
COPY apps/web/ ./
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build

FROM eclipse-temurin:25-jre-alpine AS api-runtime
WORKDIR /app
RUN addgroup -S contactcore && adduser -S contactcore -G contactcore
COPY --from=api-build /workspace/apps/api/target/contactcore-api-*.jar /app/contactcore-api.jar
USER contactcore
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/contactcore-api.jar"]

FROM nginx:1.27-alpine AS web-runtime
COPY apps/web/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=web-build /workspace/apps/web/dist /usr/share/nginx/html
EXPOSE 80

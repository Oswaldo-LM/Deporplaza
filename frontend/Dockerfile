# =========================================================
# ETAPA 1 - COMPILAR ANGULAR
# =========================================================

FROM node:22-alpine AS build

WORKDIR /app


COPY package.json package-lock.json ./

RUN npm ci


COPY . .


RUN npm run build \
    && mkdir -p /app/output \
    && INDEX_FILE="$(find /app/dist -type f -name index.html | head -n 1)" \
    && test -n "$INDEX_FILE" \
    && cp -a "$(dirname "$INDEX_FILE")/." /app/output/


# =========================================================
# ETAPA 2 - NGINX
# =========================================================

FROM nginx:alpine


COPY nginx.conf /etc/nginx/conf.d/default.conf


COPY --from=build /app/output/ /usr/share/nginx/html/


EXPOSE 80


CMD ["nginx", "-g", "daemon off;"]
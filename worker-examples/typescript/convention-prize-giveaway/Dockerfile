FROM node:18-slim as builder

WORKDIR /usr/src/app

COPY src src
COPY package.json package.json
COPY package-lock.json package-lock.json
COPY tsconfig.json tsconfig.json

RUN npm ci

RUN npm run build

FROM node:18-slim

RUN apt-get update && apt-get install -y openssl ca-certificates

WORKDIR /usr/src/app

ARG PORT=8000
ENV PORT=$PORT

COPY --from=builder /usr/src/app/package.json package.json
COPY --from=builder /usr/src/app/node_modules node_modules
COPY --from=builder /usr/src/app/lib lib

EXPOSE $PORT

CMD npm run start.watch

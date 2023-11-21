FROM node:18-alpine

WORKDIR /app

COPY . .

RUN npm ci

# RUN npm run dev

EXPOSE 5173

CMD ["npx", "serve", "dev"]

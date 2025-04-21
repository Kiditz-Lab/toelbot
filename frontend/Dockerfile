FROM node:23-slim

WORKDIR /app

# Install "serve" globally
RUN npm install -g serve

# Copy only the built files
COPY dist/ .

ARG VITE_API_URL
ARG VITE_WS_URL

ENV VITE_API_URL=$VITE_API_URL
ENV VITE_WS_URL=$VITE_WS_URL

EXPOSE 3000

CMD ["serve", "-s", ".", "-l", "3000"]

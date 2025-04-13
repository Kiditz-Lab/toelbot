# Step 1: Build the app
FROM node:23-slim as build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the app and build it
COPY . .
RUN npm run build

# Step 2: Serve the app using "serve"
FROM node:23-slim

WORKDIR /app

# Install "serve" globally
RUN npm install -g serve

# Copy the built app from previous stage
COPY --from=build /app/dist .

# Expose port and run the app
EXPOSE 3000
CMD ["serve", "-s", ".", "-l", "3000"]

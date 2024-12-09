# GoldenNest Project

GoldenNest is a backend application developed with **Java Spring Boot**, integrating modern technologies such as **JWT**, **Docker**, **MinIO**, **Flyway**, **Mail SMTP**, and **WebSocket**. The application connects to a frontend built with **ReactJS**, supporting **CORS** to ensure secure connections.

---

## 🚀 Technologies Used

### Backend
- **Java Spring Boot**: Handles business logic and provides REST APIs.
- **JWT**: Authentication and session security.
- **Flyway**: Manages and deploys database migrations.
- **MinIO**: Stores files and object-based resources.
- **Mail SMTP**: Sends account confirmation and notification emails.
- **WebSocket**: Enables real-time communication.
- **Docker**: Packages and deploys the application efficiently.
- **CORS**: Ensures secure connections between backend and frontend.

### Frontend
- **ReactJS**: Creates the user interface and connects to the backend via APIs.

---

## 🌟 Key Features

### 1. User Authentication
- Login and registration with JWT-based authentication.
- Refresh Token support for session continuity.
- Secures API endpoints with Bearer Token.
- . . . . . . . .

### 2. File Management
- Stores and manages files (images, documents) on MinIO.
- Retrieves files through secure URLs.
- . . . . . . . .

### 3. Database Management
- Automates database migrations with Flyway.
- Manages schemas according to application versions.
- . . . . . . . .

### 4. Email System
- Sends account verification and notification emails.
- Configures SMTP for secure email delivery.
- . . . . . . . .

### 5. Real-Time Communication
- Uses WebSocket for one-on-one chat functionality.
- Sends real-time user status updates.
- . . . . . . . .

### 6. CORS Support
- Connects the frontend (ReactJS) with the backend using CORS mechanism.
- . . . . . . . .

---

## 🛠 Installation Guide

### System Requirements
- **Java**: Version 17 or higher.
- **Maven**: Version 3.8.1 or higher.
- **Docker**: Latest version.
- **Node.js & npm**: For running the ReactJS frontend.

### 1. Setting up MinIO
- Use the Docker command to start MinIO:
  ```bash
  docker-compose up -d

- Check running containers:
  ```bash
  docker ps

- Stop running containers:
  ```bash
  docker-compose down

### 2. Running the Application
- Use the Maven command to start the application:
  ```bash
  mvn spring-boot:run

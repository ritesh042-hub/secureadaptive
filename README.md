# SecureAdaptive

**Adaptive Authentication and Intelligent Login Security System with Context-Aware Theme Management**

This project provides an advanced authentication system that dynamically tracks login context (IP, device, time, location) and requires OTP verification for new or suspicious login attempts. It features a modern, responsive UI with light/dark theme support.

## Technology Stack

- **Frontend**: React, Vite, TypeScript, Tailwind CSS v4, React Router, Axios
- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA, Spring Security, Maven
- **Database**: PostgreSQL

## Prerequisites

- Node.js (v18+)
- Java Development Kit (JDK) 21
- Maven (or use the provided Maven Wrapper)
- PostgreSQL (v14+)

## PostgreSQL Setup Requirements

1. Install and start PostgreSQL.
2. Create a database named `secureadaptive`:
   ```sql
   CREATE DATABASE secureadaptive;
   ```
3. Update the credentials in `backend/src/main/resources/application.properties` or configure environment variables as described in `backend/.env.example`.

## How to Start the Frontend

1. Open a terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Access the frontend at `http://localhost:5173`.

## How to Start the Backend

1. Open a terminal and navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080`.
   - *Test Health Endpoint*: `http://localhost:8080/api/health`

## Implementation Status

**Fully Implemented Features**:
- **Core Authentication**: User Registration, Login, and JWT-based session management.
- **Adaptive Security**: Tracks IP, City, State, Country, Browser, OS, and Device Model.
- **Gmail OTP**: Real OTP emails sent via Gmail SMTP for new/suspicious login contexts.
- **Trusted Devices**: Users can trust a device to bypass OTP for a limited time.
- **Security Dashboard**: View active sessions, login history (with accurate geolocation coordinates), and revoke access remotely.
- **Intelligent Theme Management**: Light, Dark, and AUTO theme (automatically selects mode based on strict Indian Standard Time logic), with instant local persistence.
- **Deployment Ready**: Fully configured for production environments (Vercel, Render/Cloud, PostgreSQL) using secure environment variables.

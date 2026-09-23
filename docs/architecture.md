# Adaptive Authentication and Intelligent Login Security System

## Project Purpose
This project aims to build a college-level full-stack security application providing advanced adaptive authentication. The system dynamically responds to login context (IP, device, time, location) and provides a secure, intuitive experience with context-aware themes.

## Technology Stack
- **Frontend**: React, Vite, TypeScript, Tailwind CSS v4, React Router, Axios
- **Backend**: Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA, Spring Security
- **Database**: PostgreSQL
- **Build Tools**: Maven (Backend), npm (Frontend)

## Frontend Architecture
The frontend is a Single Page Application (SPA) built with React and Vite.
- **Routing**: `react-router-dom` is used to manage navigation between Dashboard, Login, Register, Security, Profile, Settings, and OTP Verification pages.
- **Styling**: Tailwind CSS v4 ensures responsive and modern designs with dark/light mode support.
- **State/API**: Axios is used for API communication. The architecture separates UI components from pages for clean maintainability.

## Backend Architecture
The backend follows a layered Spring Boot architecture:
- **Controllers** (`com.example.secureadaptive.controller`): Handle incoming HTTP requests and map them to service layer.
- **Services** (`com.example.secureadaptive.service`): Contain business logic (e.g., adaptive auth rules, OTP generation).
- **Repositories** (`com.example.secureadaptive.repository`): Spring Data JPA interfaces for database access.
- **Entities** (`com.example.secureadaptive.entity`): JPA models representing database tables.
- **Security** (`com.example.secureadaptive.security`): Custom authentication providers, JWT filters, and security configurations.
- **DTOs** (`com.example.secureadaptive.dto`): Data Transfer Objects for client-server communication.

## Database Architecture
The primary database is PostgreSQL.
Future entities will include:
- `User`: Core user information and credentials.
- `LoginHistory`: Tracking IP, device, browser, and location for each login attempt.
- `TrustedDevice`: Managing devices that bypass OTP requirements.
- `Session`: Active sessions for the user.

## Authentication Flow (Future Phase)
1. User submits credentials.
2. Backend verifies hash against stored BCrypt password.
3. If valid, backend evaluates context (IP, location, device, browser).
4. If context is new/suspicious, backend issues an OTP challenge to the registered email and halts login.
5. If context is trusted or OTP is verified, backend generates a JWT and initiates a session.
6. Frontend stores the JWT securely and redirects to the Dashboard.

## Security Flow (Future Phase)
- **Context-Aware Rules**: Identifies anomalies such as "login from a new city" or "different browser."
- **Trusted Devices**: Users can mark devices as trusted for a configurable expiration period.
- **Dashboard**: Users can view their login history, revoke sessions, and monitor security events.

## Major Development Phases
- **Phase 1**: Initial project architecture, technology setup, basic routing, and health checks. (CURRENT)
- **Phase 2**: User Registration, Core Authentication, and JWT setup.
- **Phase 3**: Context-Aware Engine (IP, Device tracking) and OTP Verification.
- **Phase 4**: Security Dashboard and Trusted Device Management.
- **Phase 5**: UI Polish, Theme Management, and Final Integration.

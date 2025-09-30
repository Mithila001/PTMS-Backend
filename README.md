# Public Transport Management System - Backend API

A robust Spring Boot 3 RESTful API backend for digitalizing and managing public bus transport operations in Sri Lanka. This system focuses on comprehensive Bus, Route, and Employee assignment management, backed by strong professional practices and advanced data handling.

## 🚌 Overview

The PTMS Backend API is built on **Spring Boot (Java 21)** and provides a complete RESTful interface for core transport management. It features:

- **Advanced Data Handling:** Utilizes **PostgreSQL** with **Hibernate Spatial** for future-proofing geospatial capabilities and **Hibernate Envers** for comprehensive data auditing.
- **Secure & Role-Based Access:** Integrated **Spring Security** for role-based authentication and secure API access.
- **Structured Management:** Supports detailed management of **Buses**, **Routes**, **Employees** (Drivers/Conductors), and **Scheduled Assignments**.
- **Development Ready:** Includes automated data loading and startup diagnostics for a smooth local setup.

> [!NOTE]
> This API is designed to work with the separate **PTMS Frontend React** application, available in the **[PTMS-FrontEnd Repository](https://github.com/Mithila001/PTMS-frontEnd)**.

## 🏗️ Architecture & Technology Stack

- **Framework**: Spring Boot 3.x
- **Java Version**: JDK 21
- **Database**: PostgreSQL with PostGIS extension (leveraging Hibernate Spatial)
- **Security**: Spring Security (Custom Authentication, Role-Based Access Control)
- **ORM**: Hibernate with JPA
- **Auditing**: Hibernate Envers for comprehensive change tracking
- **Build Tool**: Maven 3.8+
- **Containerization**: Docker support with multi-stage builds
- **API Documentation**: RESTful endpoints with comprehensive validation

## 🚀 Core Features & Professional Practices

This system is designed not just for basic transport management functionality, but also to demonstrate adherence to industry-standard development practices, including robust security, data integrity, and maintainability.

---

### 🛡️ Secure & Structured API Design

The backend implements a clear, structured architecture with a focus on security and data integrity:

- **RESTful Service Design:** Standardized CRUD operations across all core domain entities (**Routes**, **Buses**, **Employees**, and **Scheduled Assignments**).
- **API Validation:** Comprehensive input validation using **Jakarta Bean Validation** (e.g., `@NotBlank`, `@Size`), ensuring data integrity at the model layer.
- **Authentication & Access Control:**
  - **Session-based Authentication** managed by **Spring Security**, utilizing a custom **JSON Login Filter** for secure username/password handling.
  - **Role-Based Access Control (RBAC)** enforced for `ADMIN`, `OPERATIONS_MANAGER`, and `USER` roles to secure specific API endpoints.
  - Secure endpoints for user registration, login, and current user retrieval.

---

### 💾 Advanced Data Handling & Auditing

The system utilizes advanced persistence features to ensure data quality, history, and future-proofing:

- **Geospatial Capabilities:** Integration of **PostgreSQL** with the **PostGIS** extension, leveraging **Hibernate Spatial** for managing and querying geographic route data using JTS data types (Confirmed in `Route.java`).
- **Comprehensive Audit Trail:** Implements **Hibernate Envers** to automatically track and version all changes to critical entities. A dedicated `AuditService` provides a centralized, detailed, and historically attributable audit log with field-level change detection (Confirmed by `AuditService.java`).
- **Employee Hierarchy:** Models a real-world employee structure, managing both **Drivers** and **Conductors** using an inheritance pattern.
- **Operational Management:** Enables dynamic **Assignment** of personnel and buses to **Scheduled Trips** for real-time operation tracking.

---

### ⚙️ Development, Deployment, & Production Readiness

Practices put in place to ensure the project is easy to set up, deploy, and maintain in production:

- **Containerized Deployment (Docker):** A **multi-stage Dockerfile** is used to create minimal, hardened images, featuring:
  - Maven build stage for caching and speed.
  - **Non-root user** execution (`appuser`) for enhanced security.
  - Optimized **JVM settings** for container environments (`-XX:MaxRAMPercentage=75.0`).
  - **Spring Boot Actuator** and **curl** used for robust container **HEALTHCHECK**.
- **Custom Startup Error Check:** Implements **fail-fast diagnostics** to prevent runtime failures:
  - **Pre-Context Validator (`StartupPropertyValidator`)** checks critical configuration properties (like `DB_URL`) before the Spring context loads.
  - **Production Diagnostics (`StartupDiagnostics`)** runs a `CommandLineRunner` in the `prod` profile to ensure the database connection is live and valid before accepting traffic.
- **Automated Data Seeding:** Configurable data loaders (`@Profile("dev")`) are used to automatically generate sample data on startup for rapid local environment setup.
- **Clear Separation of Concerns:** Adherence to MVC principles with distinct packages for Controllers, Services, Repositories, and DTOs.

## 📋 Prerequisites

To run and develop the **Public Transport Management System** locally, you will need the following tools and services installed and configured on your machine.

---

### 💻 Development Environment

- **Java Development Kit (JDK) 21 or newer:** The project is built using Java 21. Ensure the correct version is installed and set in your environment variables.
- **Maven 3.6+:** Used for building the project, managing dependencies, and running tests.
- **Git:** Required for cloning the repository.
- **An IDE of choice** (e.g., IntelliJ IDEA, VS Code, Eclipse) configured for Spring Boot and Java development.

---

### 🗃️ Database & Geospatial Services

The system requires a running PostgreSQL instance with the PostGIS extension enabled to support the geospatial routing features.

- **PostgreSQL 17 or newer:** The database server required for persistence.
  > [!WARNING]
  > As of September 2025, the **PostGIS Extension** installation option may not yet be available in the Stack Builder for **PostgreSQL 18**. **PostgreSQL 17** is recommended for now to ensure smooth installation of the PostGIS extension.
- **PostGIS Extension:** Must be installed and enabled on the target database schema (e.g., via `CREATE EXTENSION postgis;`).
- **Database Credentials:** You will need the following environment variables set for the application to connect:
  - `DB_URL` (e.g., `jdbc:postgresql://localhost:5432/ptms_db`)
  - `DB_USERNAME`
  - `DB_PASSWORD`

---

### 🐋 Containerization (Recommended Setup)

The most consistent way to run the application and its dependencies is using Docker.

- **Docker Engine:** Required to build the application container and run the PostgreSQL database.
- **Docker Compose:** Highly recommended for managing the application and database containers together with a single command.
  > [!TIP]
  > To simplify the setup, create a single **`docker-compose.yml`** file to build the backend (using the provided `Dockerfile`), frontend, and database services all at once. This enables one-command startup for the entire system.

---

## 🛠️ Installation & Setup (Non-Docker)

Follow these steps to set up and run the **Public Transport Management System** backend locally without using Docker containers, utilizing the custom PowerShell startup script.

### 1. Database Setup (PostgreSQL with PostGIS)

You must have **PostgreSQL 17+** installed and running before starting the application.

1.  **Install PostgreSQL:** Download and install a recommended version (e.g., PostgreSQL 17) for your operating system.
2.  **Create Database:** Open the PostgreSQL interactive terminal (psql) or a tool like PgAdmin and create the database specified in the environment file.
    ```sql
    CREATE DATABASE bus_transport_db;
    ```
3.  **Enable PostGIS:** Connect to the newly created database (`\c bus_transport_db`) and enable the required geospatial extension.
    ```sql
    CREATE EXTENSION postgis;
    ```

---

### 2. Backend Application Setup

1.  **Clone the Repository:**
    ```bash
    git clone [Your Repository URL Here]
    cd public-transport-management-system
    ```
2.  **Create Environment File:** Create a file named **`.env.dev`** in the project root based on an `.env.example` template and populate it with your desired credentials.

    | Variable                  | Example Value                                         | Description                                              |
    | :------------------------ | :---------------------------------------------------- | :------------------------------------------------------- |
    | `DB_URL`                  | `"jdbc:postgresql://localhost:5432/bus_transport_db"` | Database connection URL.                                 |
    | `DB_USERNAME`             | `"postgres"`                                          | Database user.                                           |
    | `DB_PASSWORD`             | `"root"`                                              | Database password.                                       |
    | `SPRING_PROFILES_ACTIVE`  | `dev`                                                 | Sets the profile to load **application-dev.properties**. |
    | `DEV_DATA_LOADER_ENABLED` | `false`                                               | Set to `true` to enable sample data seeding on startup.  |
    | `SERVER_PORT`             | `8080`                                                | Application port.                                        |
    | `COOKIE_SECURE`           | `false`                                               | Security setting for session cookie.                     |
    | `CORS_ALLOWED_ORIGINS`    | `"http://localhost:5173,http://localhost:3000"`       | Allowed frontend origins.                                |

    > [!TIP]
    > If you prefer running the application directly through an IDE (like the Spring Boot Dashboard) and want to skip using the PowerShell script, you can modify the **fallback credentials** in **`src/main/resources/application-dev.properties`**. Any properties set directly in this file will be used if the corresponding environment variable is not provided.

3.  **Run the Application (Recommended):** Use the provided PowerShell script to load environment variables and start the application. This approach ensures all necessary configurations are correctly applied.

    - **Requires:** **PowerShell** on your system.
    - **Command:**
      ```bash
      .\scripts\run-dev.ps1
      ```

4.  **Wait for Startup:** The application startup is successful when the console displays a log message similar to: `Started PublicTransportManagementSystem... in X.XXX seconds`.

---

### 3. Post-Setup & Access

Once the application is running:

1.  **API Access:** The backend API will be available at:
    - **Base URL:** `http://localhost:8080`
2.  **Data Seeding:** If `DEV_DATA_LOADER_ENABLED` was set to `true`, the database will be populated with sample data (Routes, Buses, etc.). Check your data seeding logic for default login credentials.
3.  **Next Steps:**
    - **Frontend Connection:** Ensure your frontend application is running on one of the **CORS-allowed origins** and is configured to make API calls to `http://localhost:8080`.
    - **Testing:** Use a tool like Postman to test the core endpoints (e.g., `/api/auth/login` and `/api/buses`).

## 🌐 Main API Endpoints

The application exposes a structured RESTful API accessible via the base URL: `http://localhost:8080/api`.

---

### Authentication & User Management

These endpoints handle user security, session management, and basic user registration.

| HTTP Method | Endpoint              | Description                                                                         | Access        |
| :---------- | :-------------------- | :---------------------------------------------------------------------------------- | :------------ |
| **POST**    | `/api/auth/login`     | **Login:** Authenticates a user and establishes a session (returns session cookie). | Public        |
| **POST**    | `/api/auth/logout`    | **Logout:** Clears the session cookie, terminating the user session.                | Authenticated |
| **GET**     | `/api/auth/me`        | **Profile:** Retrieves information about the current authenticated user.            | Authenticated |
| **POST**    | `/api/users/register` | **Register:** Creates a new user account.                                           | Public        |
| **GET**     | `/api/users`          | Retrieves a list of all system users.                                               | ADMIN         |
| **GET**     | `/api/users/{id}`     | Retrieves a user by their ID.                                                       | ADMIN         |
| **PUT**     | `/api/users/{id}`     | Updates a user's details.                                                           | ADMIN         |
| **DELETE**  | `/api/users/{id}`     | Deletes a user by their ID.                                                         | ADMIN         |

---

### Route Management

| Controller          | Base Path     | Functionality                                                                                                                         |
| :------------------ | :------------ | :------------------------------------------------------------------------------------------------------------------------------------ |
| **RouteController** | `/api/routes` | Standard CRUD operations for Routes. Includes dedicated endpoints for searching by **route number**, **origin**, and **destination**. |

### Vehicle Management

| Controller        | Base Path    | Functionality                                                                                                                 |
| :---------------- | :----------- | :---------------------------------------------------------------------------------------------------------------------------- |
| **BusController** | `/api/buses` | Standard CRUD operations for Buses. Includes a search endpoint for filtering by **registration number** and **service type**. |

### Employee Management

The system manages drivers and conductors through inherited entity structures.

| Controller              | Base Path         | Functionality                                                                                                |
| :---------------------- | :---------------- | :----------------------------------------------------------------------------------------------------------- |
| **DriverController**    | `/api/drivers`    | CRUD and retrieval of **Driver** entities. Includes lookup by **NIC number**.                                |
| **ConductorController** | `/api/conductors` | CRUD and retrieval of **Conductor** entities. Includes lookup by **NIC number**.                             |
| **EmployeeController**  | `/api/employees`  | Search and query endpoints across both Driver and Conductor types (e.g., searching by name, contact number). |

### Assignment & Trip Management

| Controller                  | Base Path              | Functionality                                                                                                                                               |
| :-------------------------- | :--------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **AssignmentController**    | `/api/assignments`     | CRUD and comprehensive search for resource assignments (Bus, Driver, Conductor) to a trip. Search supports filtering by date, status, and employee/bus IDs. |
| **ScheduledTripController** | `/api/scheduled-trips` | CRUD and search functionality for defining and querying the planned schedule of trips (e.g., by route number, direction).                                   |

### Utility & Diagnostics

| Controller              | Base Path                | Functionality                                                                                                |
| :---------------------- | :----------------------- | :----------------------------------------------------------------------------------------------------------- |
| **DashboardController** | `/api/dashboard/metrics` | Retrieves core operational metrics and statistics for the system dashboard.                                  |
| **AuditController**     | `/api/audit`             | Provides historical change logs for entities (e.g., Bus) and recent **global audit logs** across the system. |
| **ActionLogController** | `/api/action-logs`       | Retrieves a full list of user actions and system events.                                                     |
| **EnumController**      | `/api/enums`             | Provides various application enumerations (Service Types, Fuel Types, User Roles) for frontend consumption.  |

## 📁 Project Structure

```
public-transport-management-system/
├── .github/
│   └── workflows/
│       └── ci.yml
├── .mvn/
├── scripts/
│   ├── run-dev.ps1            # PowerShell script to run the app
│   └── (other setup scripts)
├── logs/
│   └── ptms.log
├── .env.dev
├── .env.prod
├── Dockerfile
├── docker-entrypoint.sh
├── pom.xml
└── src/
├── main/
│   ├── java/
│   │   └── com/tritonptms/public_transport_management_system/
│   │       ├── PublicTransportManagementSystemApplication.java (Main Entry Point)
│   │       ├── auditing/
│   │       │   ├── CustomRevisionEntity.java
│   │       │   └── CustomRevisionListener.java
│   │       ├── config/
│   │       │   ├── security/
│   │       │   ├── dataLoaders/
│   │       │   ├── StartupDiagnostics.java
│   │       │   └── StartupPropertyValidator.java (Pre-context)
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       │   └── specification/
│   │       └── utils/
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── application-test.properties
│       └── logback-spring.xml
└── test/
├── java/
│   └── com/tritonptms/public_transport_management_system/
│       ├── PublicTransportManagementSystemApplicationTests.java
│       ├── controller/
│       └── service/
```

## 👥 Default Users (Development Environment)

The system automatically creates default users when running in development mode:

| Username   | Password    | Role               | Description                 |
| ---------- | ----------- | ------------------ | --------------------------- |
| `admin`    | `adminpass` | ADMIN              | Full system access          |
| `ops`      | `opspass`   | OPERATIONS_MANAGER | Route & employee management |
| `testuser` | `testpass`  | USER               | Read-only access            |

> [!NOTE]
> The exact credentials are set and initially loaded by the [UserDataLoader.java](src/main/java/com/tritonptms/public_transport_management_system/config/dataLoaders/UserDataLoader.java) file.

## 📄 License

This project is licensed under the MIT License.

## 🔄 Version History

- **v1.0.0** - Initial release with core functionalitys

---

# Homestay Booking - Backend (BE)

This is the robust backend service for the Homestay Booking platform, providing APIs for properties, bookings, users, and real-time features.

## 🚀 Technologies Used

- **Framework**: Spring Boot 3.3.12 (Java 17)
- **Database**: PostgreSQL with Spring Data JPA
- **Security**: Spring Security & OAuth2 Resource Server
- **Real-time Communication**: Spring Boot WebSockets
- **Cloud Storage**: Cloudinary (Image upload/management)
- **Third-party Services**: Firebase Admin SDK
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Other Utilities**:
  - JHipster Framework
  - SpringFilter (Query builder)
  - MapStruct (Object mapping)
  - Bucket4j (Rate Limiting)
  - HikariCP (Connection Pooling)
  - Lombok

## 🛠️ Prerequisites

- **Java JDK**: 17 or higher
- **Gradle**: Wrapper is provided (`gradlew`)
- **Docker**: (Optional) For running the database or the entire stack via `docker-compose.yml`.
- **Database**: PostgreSQL instance running.

## 📦 Getting Started

### 1. Configure the Database
Create a PostgreSQL database for the application. Alternatively, you can spin up the DB using the provided `docker-compose.yml` in the project root.

### 2. Environment Variables
Configure your environment variables in `.env` or your application properties:
- **Database Configuration**:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- **Cloudinary Setup**:
  - `CLOUDINARY_URL`
- **Firebase Setup**: Provide the necessary service account keys.
- **OAuth2**: Keys relating to security.

### 3. Build the Application
```bash
./gradlew build
```
*(Skip tests using `./gradlew build -x test` if needed)*

### 4. Run the Application
```bash
./gradlew bootRun
```
The server will typically start on `http://localhost:8080`.

## 📚 API Documentation

Once the server is running, the Swagger UI API documentation can be accessed locally at:
`http://localhost:8080/swagger-ui.html`

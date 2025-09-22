# Blood Bank Management System - Backend

A Spring Boot backend application for managing blood bank operations including donor registration, blood inventory tracking, and blood request management.

## 🚀 Features

- **Donor Management**: Register and manage blood donors with eligibility tracking
- **Blood Inventory**: Track blood units by type with automatic stock level monitoring
- **Blood Requests**: Handle blood requests from hospitals and patients
- **Dashboard Analytics**: Real-time statistics and insights
- **RESTful APIs**: Complete REST API with comprehensive endpoints
- **Data Validation**: Robust input validation and error handling
- **Database Integration**: MySQL database with JPA/Hibernate ORM

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Libraries**: 
  - Lombok (for boilerplate reduction)
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Validation
  - MySQL Connector/J

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd blood-bank-system/BACKEND/blood-bank-backend
```

### 2. Database Setup

1. Install and start MySQL server
2. Create a new database:

```sql
CREATE DATABASE bloodbank_db;
CREATE USER 'bloodbank_user'@'localhost' IDENTIFIED BY 'bloodbank_password';
GRANT ALL PRIVILEGES ON bloodbank_db.* TO 'bloodbank_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bloodbank_db
spring.datasource.username=bloodbank_user
spring.datasource.password=bloodbank_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
server.servlet.context-path=/

# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Logging Configuration
logging.level.com.bloodbank=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### 4. Build and Run

```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Or using Maven Wrapper
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Donors API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/donors` | Get all donors |
| GET | `/donors/{id}` | Get donor by ID |
| POST | `/donors` | Create new donor |
| PUT | `/donors/{id}` | Update donor |
| DELETE | `/donors/{id}` | Delete donor |
| GET | `/donors/eligible` | Get eligible donors |
| GET | `/donors/blood-group/{bloodGroup}` | Get donors by blood group |

### Blood Inventory API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/inventory` | Get all inventory |
| GET | `/inventory/{id}` | Get inventory by ID |
| POST | `/inventory` | Create inventory entry |
| PUT | `/inventory/{id}` | Update inventory |
| DELETE | `/inventory/{id}` | Delete inventory |
| PUT | `/inventory/{id}/add-units` | Add blood units |
| PUT | `/inventory/{id}/remove-units` | Remove blood units |

### Blood Requests API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/requests` | Get all requests |
| GET | `/requests/{id}` | Get request by ID |
| POST | `/requests` | Create new request |
| PUT | `/requests/{id}` | Update request |
| DELETE | `/requests/{id}` | Delete request |
| PUT | `/requests/{id}/status` | Update request status |
| GET | `/requests/email/{email}` | Get requests by email |

### Dashboard API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/stats` | Get dashboard statistics |

### Sample API Requests

#### Create Donor
```bash
curl -X POST http://localhost:8080/api/donors \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john.doe@email.com",
  "phone": "1234567890",
  "bloodGroup": "O+",
  "age": 25,
  "weight": 70.0,
  "address": "123 Main St, City",
  "isEligible": true
}'
```

#### Create Blood Request
```bash
curl -X POST http://localhost:8080/api/requests \
-H "Content-Type: application/json" \
-d '{
  "requesterName": "Hospital Admin",
  "contactEmail": "admin@hospital.com",
  "contactPhone": "9876543210",
  "hospitalName": "City Hospital",
  "patientName": "Jane Smith",
  "bloodGroup": "A+",
  "unitsRequested": 2,
  "urgencyLevel": "URGENT",
  "medicalReason": "Surgery preparation"
}'
```

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/com/bloodbank/
│   │   ├── BloodBankBackendApplication.java  # Main application class
│   │   ├── config/                          # Configuration classes
│   │   │   └── CorsConfig.java             # CORS configuration
│   │   ├── controller/                      # REST controllers
│   │   │   ├── DonorController.java
│   │   │   ├── BloodInventoryController.java
│   │   │   ├── BloodRequestController.java
│   │   │   ├── DashboardController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── DonorDTO.java
│   │   │   ├── BloodInventoryDTO.java
│   │   │   ├── BloodRequestDTO.java
│   │   │   └── CommonDTO.java
│   │   ├── entity/                         # JPA entities
│   │   │   ├── Donor.java
│   │   │   ├── BloodInventory.java
│   │   │   └── BloodRequest.java
│   │   ├── repository/                     # JPA repositories
│   │   │   ├── DonorRepository.java
│   │   │   ├── BloodInventoryRepository.java
│   │   │   └── BloodRequestRepository.java
│   │   └── service/                        # Service layer
│   │       ├── DonorService.java
│   │       ├── BloodInventoryService.java
│   │       ├── BloodRequestService.java
│   │       └── DashboardService.java
│   └── resources/
│       ├── application.properties          # Application configuration
│       ├── static/                        # Static resources
│       └── templates/                     # Templates (if any)
└── test/                                  # Test classes
```

## 🔧 Configuration Options

### Database Configuration
- Configure different database connections in `application.properties`
- Supports MySQL, PostgreSQL, H2 (for testing)

### CORS Configuration
- Modify `CorsConfig.java` to adjust allowed origins
- Currently configured for frontend development servers

### Logging Configuration
- Adjust logging levels in `application.properties`
- Separate loggers for application and framework components

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

## 🚀 Deployment

### Using Docker

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/blood-bank-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:

```bash
mvn clean package
docker build -t blood-bank-backend .
docker run -p 8080:8080 blood-bank-backend
```

### Production Deployment

1. Configure production database settings
2. Set appropriate logging levels
3. Configure security settings
4. Set up environment-specific profiles

## 🛡️ Security Considerations

- Implement authentication and authorization
- Add input sanitization
- Configure HTTPS in production
- Implement rate limiting
- Add API documentation with Swagger

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This is a demo application for educational purposes. Additional security and production considerations should be implemented before deploying in a real-world scenario.
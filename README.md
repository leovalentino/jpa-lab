# JPA Performance Lab

This project demonstrates common JPA performance issues and their solutions in a Spring Boot application.

## Features

- **N+1 Query Problem**: Shows the classic N+1 issue and how to fix it with JOIN FETCH or entity graphs.
- **Dirty Checking**: Demonstrates how Hibernate's dirty checking works and its performance impact.
- **Lazy Loading Exceptions**: Illustrates the `LazyInitializationException` and how to avoid it.
- **Query Statistics**: Integrated with DataSource-Proxy to log SQL queries and execution times.

## Prerequisites

- Java 17 or later
- Maven 3.6+
- PostgreSQL 14+
- Docker and Docker Compose (optional, for running PostgreSQL in a container)

## Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd jpa-performance-lab
```

### 2. Start PostgreSQL

You can either run PostgreSQL locally or use Docker Compose:

```bash
docker-compose up -d
```

This starts a PostgreSQL container on port 5432 with:
- Database: `jpa_lab`
- Username: `postgres`
- Password: `postgres`

### 3. Configure the database

If you're not using Docker Compose, create a PostgreSQL database manually:

```sql
CREATE DATABASE jpa_lab;
```

Update `src/main/resources/application.properties` if your connection details differ.

### 4. Build and run the application

```bash
./mvnw clean spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### N+1 Demo
- `GET /api/lab/nplus1` – Demonstrates the N+1 problem and its solution.

### Dirty Checking Demo
- `GET /api/lab/dirty-checking` – Shows Hibernate's dirty checking mechanism.

### Lazy Loading Exception Demo
- `GET /api/lab/lazy-exception` – Triggers a `LazyInitializationException`.

### Lazy Loading Correct Demo
- `GET /api/lab/lazy-correct` – Shows the correct way to handle lazy loading.

## Project Structure

```
src/main/java/com/lab/
├── Application.java              # Main Spring Boot application
├── DataInitializer.java          # Initializes sample data on startup
├── config/
│   └── QueryCountConfig.java     # Configuration for query statistics
├── controller/
│   └── LabController.java        # REST endpoints for demos
└── entity/
    ├── User.java                 # User entity
    ├── Order.java                # Order entity
    └── Product.java              # Product entity
```

## Key Configuration

- **Batch Operations**: Configured for batch inserts/updates in `application.properties`.
- **SQL Logging**: All SQL statements are logged with formatting and comments.
- **Open Session in View**: Enabled to demonstrate lazy loading behavior (can be disabled for production).

## Performance Tips

1. **Use JOIN FETCH** for one-to-many relationships when you need all associated data.
2. **Avoid `Open Session in View`** in production to prevent unintended lazy loading.
3. **Enable batch processing** for bulk operations.
4. **Monitor query counts** with DataSource-Proxy integration.

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running and accessible.
- Verify credentials in `application.properties`.

### LazyInitializationException
- Make sure you're accessing lazy-loaded collections within an active transaction.
- Consider using `@Transactional` on service methods.

### High Memory Usage
- The data initializer creates 100 users, 50 products, and 1000 orders. Adjust numbers in `DataInitializer.java` if needed.

## Demo Instructions

1. Start the application and wait for data initialization to complete (you'll see a confirmation message in the logs).
2. Open your browser or use a tool like `curl` to visit the endpoints:
   - Visit `http://localhost:8080/api/lab/nplus1` to see the N+1 problem in action (check the console for SQL logs).
   - Visit `http://localhost:8080/api/lab/dirty-checking` to see dirty checking.
   - Visit `http://localhost:8080/api/lab/lazy-exception` to trigger a lazy loading exception.
   - Visit `http://localhost:8080/api/lab/lazy-correct` to see the correct approach.

## License

This project is for educational purposes.

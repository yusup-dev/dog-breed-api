# Dog Breed API – Technical Test

A REST API built with **Spring Boot 3** that consumes dog breed data from [Dog CEO's Dog API](https://dog.ceo/dog-api/) and applies custom transformations to specific breeds.

---

## 🚀 Running the Application

### Prerequisites

- Java 17+
- Maven 3.8+

### Run the Application

```bash
mvn spring-boot:run
```

### Swagger UI

Access the API documentation at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### H2 Console

Access the in-memory database console at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Database Connection Settings:**

- **JDBC URL:** `jdbc:h2:mem:dogdb`
- **Username:** `sa`
- **Password:** Leave blank

---

## 🧪 Running Tests

Run the test suite using the following command:

```bash
mvn test
```

**Test Results:** 29 tests, 0 failures, 0 errors, 0 skipped — **BUILD SUCCESS**

---

## 📡 API Endpoints

### Dog Breeds (Primary Feature)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/breeds` | Retrieve all dog breeds with custom transformations |
| `GET` | `/api/breeds/shiba/images` | Retrieve Shiba Inu images at odd-numbered positions (1-based indexing) |

### Dogs CRUD (Secondary Feature)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dogs` | Retrieve all stored dogs |
| `GET` | `/api/dogs/{id}` | Retrieve a dog by ID |
| `POST` | `/api/dogs` | Create a new dog |
| `PUT` | `/api/dogs/{id}` | Update an existing dog |
| `DELETE` | `/api/dogs/{id}` | Delete a dog |

---

## 📦 Tech Stack

| Dependency | Version | Purpose |
|---|---|---|
| Spring Boot | 3.3.4 | Core application framework |
| Spring Data JPA | Managed by Spring Boot BOM | ORM and data access layer |
| H2 Database | Managed by Spring Boot BOM | In-memory SQL database |
| Spring Cache | Managed by Spring Boot BOM | In-memory caching using `ConcurrentMapCacheManager` |
| springdoc-openapi | 2.6.0 | API documentation and Swagger UI at `/swagger-ui.html` |
| Lombok | Managed by Spring Boot BOM | Reduces boilerplate code with annotations such as `@Slf4j`, `@Getter`, `@Setter`, and `@RequiredArgsConstructor` |
| WireMock | 3.9.1 | HTTP stubbing for integration tests |
| JUnit 5 + Mockito | Managed by Spring Boot BOM | Unit testing and slice testing |

---

## 📝 Notes

- The application integrates with the external Dog CEO API to retrieve dog breed and image data.
- The breed endpoints implement custom transformation logic for specific breeds.
- The Dogs CRUD endpoints provide basic data persistence using Spring Data JPA and the H2 in-memory database.
- Automated tests cover application functionality and integration scenarios.
# Task Management API Design Document

## 1. Overview
A REST API backend built with Spring Boot for managing tasks. This document details the technical implementation strategy based on the provided specifications and database schema.

## 2. Configuration & Infrastructure
- **Database:** PostgreSQL.
- **Application Properties (`src/main/resources/application.properties`):**
  - Database URL: `jdbc:postgresql://localhost:5432/tasks`
  - Credentials: `postgres` / `admin`
  - Hibernate DDL Auto: `validate`
  - Show SQL: `true`
- **CORS Configuration:**
  - Implemented globally via a `@Configuration` class implementing `WebMvcConfigurer`.
  - Allowed Origins: `http://localhost:4200` (Angular Dev Server).
  - Allowed Methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`.
- **API Documentation:**
  - SpringDoc OpenAPI will expose the Swagger UI at `/swagger-ui.html`.

## 3. Data Model (Entity)
- **Entity:** `Task`
- **Table:** `tasks`
- **Fields:**
  - `id`: `Long` (Primary Key, Identity).
  - `title`: `String` (Max 70 chars, Not Null).
  - `description`: `String`.
  - `completed`: `Boolean` (Default `false`).
  - `createdAt`: `LocalDateTime` (Mapped to `created_at`, managed by `@CreationTimestamp`).
  - `updatedAt`: `LocalDateTime` (Mapped to `updated_at`, managed by `@UpdateTimestamp`).
- **Annotations:** Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`).

## 4. DTOs
- **`TaskRequestDTO`:**
  - `title`: `@NotBlank(message = "O título é obrigatório")`, `@Size(max = 70, message = "O título deve ter no máximo 70 caracteres")`.
  - `description`: Optional field.
- **`TaskResponseDTO`:**
  - Contains all fields (`id`, `title`, `description`, `completed`, `createdAt`, `updatedAt`).

## 5. API Endpoints (`/api/tasks`)
- `POST /`: Create a new task.
- `GET /`: Retrieve all tasks.
- `GET /{id}`: Retrieve a specific task by ID.
- `PUT /{id}`: Update an entire task (title, description, completed status).
- `PATCH /{id}/toggle`: Toggle the `completed` status of a task.
- `DELETE /{id}`: Delete a task by ID.

## 6. Exception Handling
- **GlobalExceptionHandler:** Uses `@RestControllerAdvice`.
- **Error Response Structure (Simplified):**
  ```json
  {
    "status": <HTTP_STATUS_CODE>,
    "error": "<HTTP_STATUS_REASON>",
    "message": "<ERROR_MESSAGE>",
    "details": ["<OPTIONAL_LIST_OF_SPECIFIC_ERRORS>"]
  }
  ```
- **Custom Exceptions:**
  - `TaskNotFoundException` -> Maps to `404 Not Found`.
- **Validation Exceptions:**
  - `MethodArgumentNotValidException` -> Maps to `400 Bad Request` with field-specific messages extracted from annotations.

## 7. Business Logic (Service Layer)
- **`TaskService`:**
  - Manages creation, ensuring `completed` is initially `false`.
  - Fetches tasks, throwing `TaskNotFoundException` if the ID does not exist.
  - Handles the toggle logic by retrieving the task, inverting the `completed` boolean, and saving it back.
  
## 8. Testing Strategy
- **Unit Tests:** Focus on `TaskService`.
- **Tools:** JUnit 5, Mockito.
- **Scenarios to cover:**
  - Task Creation (Success).
  - Task Retrieval (Success and Not Found).
  - Task Listing.
  - Task Update (Success).
  - Task Toggle (Success).
  - Task Deletion (Success).

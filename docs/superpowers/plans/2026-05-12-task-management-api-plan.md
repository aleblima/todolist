# Task Management API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a REST API backend with Spring Boot for managing tasks, including creation, listing, retrieval, update, status toggling, and deletion.

**Architecture:** Standard Spring Boot layered architecture (Controller, Service, Repository, Model, DTO, Exception) with a Global Exception Handler for validation and not-found errors. WebMvcConfigurer for CORS.

**Tech Stack:** Java 21, Spring Boot 3.x (Web, Data JPA, Validation), PostgreSQL, Lombok, JUnit 5, Mockito, SpringDoc OpenAPI.

---

### Task 1: Application Configuration

**Files:**
- Modify: `src/main/resources/application.properties`
- Create: `src/main/java/teste/tech/tasks/config/WebConfig.java`

- [ ] **Step 1: Configure application properties**
  Write the database and application settings.

```properties
spring.application.name=Tasks
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks
spring.datasource.username=postgres
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

- [ ] **Step 2: Create WebConfig for CORS**
  Create `WebConfig.java` in a new `config` package.

```java
package teste.tech.tasks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
```

- [ ] **Step 3: Commit**
```bash
git add src/main/resources/application.properties src/main/java/teste/tech/tasks/config/WebConfig.java
git commit -m "chore: configure database and global cors"
```

---

### Task 2: Domain Model & Repository

**Files:**
- Create: `src/main/java/teste/tech/tasks/model/Task.java`
- Create: `src/main/java/teste/tech/tasks/repository/TaskRepository.java`

- [ ] **Step 1: Create Task entity**
  Create `Task.java` with JPA and Lombok annotations.

```java
package teste.tech.tasks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 70)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: Create TaskRepository interface**

```java
package teste.tech.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teste.tech.tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

- [ ] **Step 3: Commit**
```bash
git add src/main/java/teste/tech/tasks/model/Task.java src/main/java/teste/tech/tasks/repository/TaskRepository.java
git commit -m "feat: create Task entity and repository"
```

---

### Task 3: DTOs & Custom Exception

**Files:**
- Create: `src/main/java/teste/tech/tasks/dto/TaskRequestDTO.java`
- Create: `src/main/java/teste/tech/tasks/dto/TaskResponseDTO.java`
- Create: `src/main/java/teste/tech/tasks/exception/TaskNotFoundException.java`

- [ ] **Step 1: Create DTOs**
  Implement request/response DTOs with validation.

```java
// src/main/java/teste/tech/tasks/dto/TaskRequestDTO.java
package teste.tech.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 70, message = "O título deve ter no máximo 70 caracteres")
    private String title;
    
    private String description;
}
```

```java
// src/main/java/teste/tech/tasks/dto/TaskResponseDTO.java
package teste.tech.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: Create Exception**
  Implement the custom not found exception.

```java
// src/main/java/teste/tech/tasks/exception/TaskNotFoundException.java
package teste.tech.tasks.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
```

- [ ] **Step 3: Commit**
```bash
git add src/main/java/teste/tech/tasks/dto/ src/main/java/teste/tech/tasks/exception/
git commit -m "feat: create DTOs and custom exceptions"
```

---

### Task 4: Global Exception Handler

**Files:**
- Create: `src/main/java/teste/tech/tasks/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Create Exception Handler**
  Implement `@RestControllerAdvice` to format errors according to Option A.

```java
package teste.tech.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Erro de validação");
        
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add src/main/java/teste/tech/tasks/exception/GlobalExceptionHandler.java
git commit -m "feat: add global exception handler for simplified error responses"
```

---

### Task 5: TaskService Implementation & Tests

**Files:**
- Create: `src/main/java/teste/tech/tasks/service/TaskService.java`
- Create: `src/test/java/teste/tech/tasks/service/TaskServiceTest.java`

- [ ] **Step 1: Write tests for TaskService**
  Create the test class with JUnit5 and Mockito.

```java
// src/test/java/teste/tech/tasks/service/TaskServiceTest.java
package teste.tech.tasks.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teste.tech.tasks.dto.TaskRequestDTO;
import teste.tech.tasks.dto.TaskResponseDTO;
import teste.tech.tasks.exception.TaskNotFoundException;
import teste.tech.tasks.model.Task;
import teste.tech.tasks.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = TaskRequestDTO.builder()
                .title("Test Task")
                .description("Description")
                .build();
    }

    @Test
    void createTask_ReturnsResponseDTO() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.createTask(requestDTO);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertFalse(result.getCompleted());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasks_ReturnsList() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.getAllTasks();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getTaskById_WhenExists_ReturnsDTO() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTaskById_WhenNotExists_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTask_ReturnsUpdatedDTO() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRequestDTO updateRequest = TaskRequestDTO.builder().title("Updated").description("Desc").build();
        TaskResponseDTO result = taskService.updateTask(1L, updateRequest);

        assertNotNull(result);
        verify(taskRepository).save(task);
    }

    @Test
    void toggleTaskCompleted_InvertsStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task); // task mock originally has completed=false

        TaskResponseDTO result = taskService.toggleTaskCompleted(1L);

        assertNotNull(result);
        // The service should have inverted the property on the object before saving
        verify(taskRepository).save(argThat(savedTask -> savedTask.getCompleted()));
    }

    @Test
    void deleteTask_WhenExists_CallsDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**
Run: `./mvnw test -Dtest=TaskServiceTest` (Expected: Fail, class not found)

- [ ] **Step 3: Implement TaskService**

```java
// src/main/java/teste/tech/tasks/service/TaskService.java
package teste.tech.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teste.tech.tasks.dto.TaskRequestDTO;
import teste.tech.tasks.dto.TaskResponseDTO;
import teste.tech.tasks.exception.TaskNotFoundException;
import teste.tech.tasks.model.Task;
import teste.tech.tasks.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        Task task = Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .completed(false)
                .build();
        
        return toDTO(taskRepository.save(task));
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return toDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        Task task = findTaskOrThrow(id);
        
        task.setTitle(requestDTO.getTitle());
        task.setDescription(requestDTO.getDescription());
        
        return toDTO(taskRepository.save(task));
    }

    public TaskResponseDTO toggleTaskCompleted(Long id) {
        Task task = findTaskOrThrow(id);
        task.setCompleted(!task.getCompleted());
        return toDTO(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Tarefa não encontrada com ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + id));
    }

    private TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
```

- [ ] **Step 4: Run tests to verify pass**
Run: `./mvnw test -Dtest=TaskServiceTest` (Expected: Pass)

- [ ] **Step 5: Commit**
```bash
git add src/main/java/teste/tech/tasks/service/TaskService.java src/test/java/teste/tech/tasks/service/TaskServiceTest.java
git commit -m "feat: implement TaskService with comprehensive unit tests"
```

---

### Task 6: TaskController

**Files:**
- Create: `src/main/java/teste/tech/tasks/controller/TaskController.java`

- [ ] **Step 1: Implement Controller**

```java
package teste.tech.tasks.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teste.tech.tasks.dto.TaskRequestDTO;
import teste.tech.tasks.dto.TaskResponseDTO;
import teste.tech.tasks.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO requestDTO) {
        return taskService.createTask(requestDTO);
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO requestDTO) {
        return taskService.updateTask(id, requestDTO);
    }

    @PatchMapping("/{id}/toggle")
    public TaskResponseDTO toggleTaskCompleted(@PathVariable Long id) {
        return taskService.toggleTaskCompleted(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add src/main/java/teste/tech/tasks/controller/TaskController.java
git commit -m "feat: create TaskController with REST endpoints"
```
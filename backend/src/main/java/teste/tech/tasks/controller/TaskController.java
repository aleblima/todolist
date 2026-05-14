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

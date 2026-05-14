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
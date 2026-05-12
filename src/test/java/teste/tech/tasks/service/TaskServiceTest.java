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

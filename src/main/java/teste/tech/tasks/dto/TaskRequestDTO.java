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
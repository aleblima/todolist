package teste.tech.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teste.tech.tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}

// src/main/java/teste/tech/tasks/exception/TaskNotFoundException.java
package teste.tech.tasks.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}

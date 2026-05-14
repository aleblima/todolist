import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TaskService } from '../../services/task';
import { Task } from '../../models/task.model';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatCheckboxModule,
    MatButtonToggleModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './task-list.html',
  styleUrls: ['./task-list.css']
})
export class TaskListComponent {
  taskService = inject(TaskService);

  tasks = this.taskService.tasks;
  isLoading = this.taskService.isLoading;
  filter = this.taskService.filter;

  toggleTaskStatus(task: Task) {
    const newStatus = task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED';
    this.taskService.patchTaskStatus(task.id, newStatus).subscribe(() => {
      this.taskService.loadTasks(); // Reload tasks after updating
    });
  }

  deleteTask(task: Task) {
    if (confirm(`Are you sure you want to delete "${task.title}"?`)) {
      this.taskService.deleteTask(task.id).subscribe(() => {
        this.taskService.loadTasks();
      });
    }
  }

  onFilterChange(event: any) {
    this.taskService.setFilter(event.value);
  }
}

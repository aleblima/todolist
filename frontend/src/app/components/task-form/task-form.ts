import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { TaskService } from '../../services/task';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './task-form.html',
  styleUrls: ['./task-form.css']
})
export class TaskFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isLoading = this.taskService.isLoading;
  taskId: number | null = null;
  isEditMode = false;

  taskForm = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(70)]],
    description: ['']
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.taskId = Number(idParam);
      this.isEditMode = true;
      this.loadTask(this.taskId);
    }
  }

  loadTask(id: number) {
    this.taskService.getTask(id).subscribe({
      next: (task) => {
        this.taskForm.patchValue({
          title: task.title,
          description: task.description || ''
        });
      },
      error: () => {
        this.router.navigate(['/tasks']);
      }
    });
  }

  onSubmit() {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }

    const taskData = this.taskForm.value as any;

    if (this.isEditMode && this.taskId) {
      this.taskService.updateTask(this.taskId, taskData).subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
          this.taskService.loadTasks(); // refresh list
        }
      });
    } else {
      this.taskService.createTask(taskData).subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
          this.taskService.loadTasks(); // refresh list
        }
      });
    }
  }

  get titleControl() {
    return this.taskForm.get('title');
  }
}

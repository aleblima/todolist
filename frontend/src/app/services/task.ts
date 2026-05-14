import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../environments/environment';
import { Task, TaskRequestDTO } from '../models/task.model';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private http = inject(HttpClient);
  private snackBar = inject(MatSnackBar);
  private apiUrl = environment.apiUrl;

  // Signals for state management
  private _tasks = signal<Task[]>([]);
  private _isLoading = signal<boolean>(false);
  private _filter = signal<'ALL' | 'PENDING' | 'COMPLETED'>('ALL');

  // Exposed computed signals
  tasks = computed(() => {
    const filter = this._filter();
    const tasks = this._tasks();
    if (filter === 'ALL') return tasks;
    return tasks.filter(t => filter === 'COMPLETED' ? t.completed : !t.completed);
  });
  isLoading = computed(() => this._isLoading());
  filter = computed(() => this._filter());

  constructor() {
    this.loadTasks();
  }

  setFilter(filter: 'ALL' | 'PENDING' | 'COMPLETED') {
    this._filter.set(filter);
  }

  loadTasks() {
    this._isLoading.set(true);
    this.http.get<Task[]>(this.apiUrl)
      .pipe(
        catchError(err => {
          this.handleError('Error loading tasks', err);
          return of([]);
        }),
        finalize(() => this._isLoading.set(false))
      )
      .subscribe(tasks => {
        this._tasks.set(tasks);
      });
  }

  getTask(id: number) {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: TaskRequestDTO) {
    this._isLoading.set(true);
    return this.http.post<Task>(this.apiUrl, task)
      .pipe(
        catchError(err => {
          this.handleError('Error creating task', err);
          throw err;
        }),
        finalize(() => this._isLoading.set(false))
      );
  }

  updateTask(id: number, task: TaskRequestDTO) {
    this._isLoading.set(true);
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task)
      .pipe(
        catchError(err => {
          this.handleError('Error updating task', err);
          throw err;
        }),
        finalize(() => this._isLoading.set(false))
      );
  }

  toggleTaskStatus(id: number) {
    this._isLoading.set(true);
    return this.http.patch<Task>(`${this.apiUrl}/${id}/completed`, {})
      .pipe(
        catchError(err => {
          this.handleError('Error updating task status', err);
          throw err;
        }),
        finalize(() => this._isLoading.set(false))
      );
  }

  deleteTask(id: number) {
    this._isLoading.set(true);
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(err => {
          this.handleError('Error deleting task', err);
          throw err;
        }),
        finalize(() => this._isLoading.set(false))
      );
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.snackBar.open(`${message}. Please try again later.`, 'Close', {
      duration: 3000,
    });
  }
}

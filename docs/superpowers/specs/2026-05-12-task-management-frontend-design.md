# Task Management Frontend Design Document (Angular)

## 1. Overview
A modern Angular frontend for managing tasks, consuming the REST API at `http://localhost:8080/api/tasks`. The application uses Angular Material for UI and Angular Signals for reactive state management.

## 2. Configuration & Infrastructure
- **Project Location:** `/frontend` directory in the root of the repository.
- **Environment (`src/environments/environment.ts`):**
  - `apiUrl`: `http://localhost:8080/api/tasks`
- **UI Library:** Angular Material.
- **State Management:** Angular Signals.

## 3. Architecture
### 3.1. Layered Approach
- **Services:** `TaskService` handles all HTTP communication using `HttpClient`.
- **Components:** Focused on presentation and user interaction.
- **Models/Interfaces:** Define TypeScript interfaces for `Task`, `TaskRequestDTO`, and `TaskResponseDTO`.

### 3.2. Routing
- `/` -> Redirects to `/tasks`
- `/tasks` -> `TaskListComponent` (List view with filters).
- `/tasks/new` -> `TaskFormComponent` (Create mode).
- `/tasks/:id/edit` -> `TaskFormComponent` (Edit mode).

## 4. Components Detail
### 4.1. `TaskListComponent`
- **Features:**
  - Displays tasks with title, description, and status.
  - Toggle completion status (patch toggle endpoint).
  - Navigation to Edit form.
  - Delete task with a simple confirmation.
  - Filter: All / Pending / Completed.
- **Visuals:** Completed tasks will have a "struck-through" text or reduced opacity using conditional CSS.

### 4.2. `TaskFormComponent`
- **Features:**
  - Reactive form (`ReactiveFormsModule`).
  - Validations: `title` (required, max 70 chars).
  - Portuguese error messages.
  - Unified logic for Create and Edit.
  - "Cancel" button to return to the list.

## 5. Technical Implementation Details
- **Reactive State:** Use `signal()` and `computed()` for handling the task list and filters.
- **Error Handling:** Global approach to display API errors amigably using `MatSnackBar`.
- **Loading State:** A signal-based `isLoading` boolean to manage UI feedback during HTTP calls.
- **CORS:** Already enabled in the backend for `http://localhost:4200`.

## 6. Development Workflow
1. Initialize project with Angular CLI.
2. Install Angular Material.
3. Generate service and components.
4. Implement routing and reactive forms.
5. Integrate with backend API using Signals.

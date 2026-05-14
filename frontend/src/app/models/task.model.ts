export interface Task {
  id: number;
  title: string;
  description?: string;
  completed: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface TaskRequestDTO {
  title: string;
  description?: string;
  completed?: boolean;
}

export interface TaskResponseDTO {
  id: number;
  title: string;
  description?: string;
  completed: boolean;
  createdAt: string;
  updatedAt?: string;
}
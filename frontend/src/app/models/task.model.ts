export interface Task {
  id: number;
  title: string;
  description?: string;
  status: 'PENDING' | 'COMPLETED';
  createdAt: string;
  updatedAt?: string;
}

export interface TaskRequestDTO {
  title: string;
  description?: string;
  status?: 'PENDING' | 'COMPLETED';
}

export interface TaskResponseDTO {
  id: number;
  title: string;
  description?: string;
  status: 'PENDING' | 'COMPLETED';
  createdAt: string;
  updatedAt?: string;
}
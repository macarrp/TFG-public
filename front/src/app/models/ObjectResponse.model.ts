export interface ObjectResponse<T> {
    error: string;
    message: T;
    success: boolean;
  }
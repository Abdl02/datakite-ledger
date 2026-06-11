export type TransactionStatus =
  | "PENDING"
  | "COMPLETED"
  | "REJECTED"
  | "PENDING_REVIEW";

export const TRANSACTION_STATUSES: TransactionStatus[] = [
  "PENDING",
  "COMPLETED",
  "REJECTED",
  "PENDING_REVIEW",
];

export const STATUS_LABELS: Record<TransactionStatus, string> = {
  PENDING: "Pending",
  COMPLETED: "Completed",
  REJECTED: "Rejected",
  PENDING_REVIEW: "Pending Review",
};

export interface TransactionResponse {
  id: string;
  amount: number;
  currency: string;
  date: string;
  description: string;
  status: TransactionStatus;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080/api/v1";

export async function fetchAllTransactions(): Promise<TransactionResponse[]> {
  const res = await fetch(
    `${API_BASE}/transactions?page=0&size=1000&sort=date,desc`,
    { cache: "no-store" }
  );
  if (!res.ok) throw new Error(`API error: ${res.status} ${res.statusText}`);
  const data: PagedResponse<TransactionResponse> = await res.json();
  return data.content;
}

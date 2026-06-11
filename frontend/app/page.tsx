"use client";

import { VolumeChart } from "@/components/dashboard/VolumeChart";
import { StatsCards } from "@/components/dashboard/StatsCards";
import { StatusFilter } from "@/components/transactions/StatusFilter";
import { TransactionTable } from "@/components/transactions/TransactionTable";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { fetchAllTransactions, TransactionResponse, TransactionStatus } from "@/lib/api";
import { AlertCircle, BarChart3, Loader2, RefreshCw } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { Button } from "@/components/ui/button";

type FilterValue = TransactionStatus | "ALL";

export default function DashboardPage() {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<FilterValue>("ALL");
  const [lastFetched, setLastFetched] = useState<Date | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchAllTransactions();
      setTransactions(data);
      setLastFetched(new Date());
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load transactions. Make sure the API is running on localhost:8080."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-10 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary">
              <BarChart3 className="h-5 w-5 text-primary-foreground" />
            </div>
            <div>
              <h1 className="text-lg font-semibold leading-none">
                Datakite Ledger
              </h1>
              <p className="text-xs text-muted-foreground mt-0.5">
                Transaction Dashboard
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            {lastFetched && (
              <span className="hidden sm:block text-xs text-muted-foreground">
                Updated {lastFetched.toLocaleTimeString()}
              </span>
            )}
            <Button
              variant="outline"
              size="sm"
              onClick={loadData}
              disabled={loading}
              className="gap-2"
            >
              <RefreshCw className={`h-3.5 w-3.5 ${loading ? "animate-spin" : ""}`} />
              Refresh
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Error banner */}
        {error && (
          <div className="flex items-start gap-3 rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
            <AlertCircle className="h-4 w-4 mt-0.5 shrink-0" />
            <div>
              <p className="font-medium">Unable to load data</p>
              <p className="mt-1 text-destructive/80">{error}</p>
            </div>
          </div>
        )}

        {/* Loading skeleton */}
        {loading && !error && (
          <div className="flex flex-col items-center justify-center py-24 gap-4 text-muted-foreground">
            <Loader2 className="h-8 w-8 animate-spin" />
            <p className="text-sm">Loading transactions…</p>
          </div>
        )}

        {!loading && !error && (
          <>
            {/* Stats cards */}
            <section>
              <StatsCards data={transactions} />
            </section>

            {/* Chart */}
            <section>
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">
                    Transaction Volume by Status
                  </CardTitle>
                  <CardDescription>
                    Total sum of transaction amounts grouped by their current
                    status
                  </CardDescription>
                </CardHeader>
                <CardContent className="pb-4">
                  {transactions.length === 0 ? (
                    <div className="flex items-center justify-center h-[280px] text-muted-foreground text-sm">
                      No data available
                    </div>
                  ) : (
                    <VolumeChart data={transactions} />
                  )}
                </CardContent>
              </Card>
            </section>

            {/* Table */}
            <section>
              <Card>
                <CardHeader>
                  <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                    <div>
                      <CardTitle className="text-base">Transactions</CardTitle>
                      <CardDescription className="mt-1">
                        {transactions.length.toLocaleString()} transactions
                        loaded — search and filter below
                      </CardDescription>
                    </div>
                    <StatusFilter
                      value={statusFilter}
                      onChange={(v) => setStatusFilter(v)}
                    />
                  </div>
                </CardHeader>
                <CardContent>
                  <TransactionTable
                    data={transactions}
                    statusFilter={statusFilter}
                  />
                </CardContent>
              </Card>
            </section>
          </>
        )}
      </main>

      <footer className="border-t mt-16">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-4 text-center text-xs text-muted-foreground">
          Datakite Ledger Dashboard — connected to{" "}
          <code className="font-mono">localhost:8080</code>
        </div>
      </footer>
    </div>
  );
}

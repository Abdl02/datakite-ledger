"use client";

import { STATUS_LABELS, TransactionResponse, TransactionStatus } from "@/lib/api";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

const STATUS_COLORS: Record<TransactionStatus, string> = {
  COMPLETED: "hsl(142, 71%, 45%)",
  PENDING: "hsl(38, 92%, 50%)",
  PENDING_REVIEW: "hsl(217, 91%, 60%)",
  REJECTED: "hsl(0, 72%, 51%)",
};

interface VolumeChartProps {
  data: TransactionResponse[];
}

interface ChartEntry {
  status: TransactionStatus;
  label: string;
  volume: number;
  count: number;
  color: string;
}

function formatCurrency(value: number) {
  if (value >= 1_000_000)
    return `$${(value / 1_000_000).toFixed(1)}M`;
  if (value >= 1_000)
    return `$${(value / 1_000).toFixed(1)}K`;
  return `$${value.toFixed(0)}`;
}

export function VolumeChart({ data }: VolumeChartProps) {
  const chartData: ChartEntry[] = (
    ["PENDING", "COMPLETED", "REJECTED", "PENDING_REVIEW"] as TransactionStatus[]
  ).map((status) => {
    const txs = data.filter((tx) => tx.status === status);
    const volume = txs.reduce((sum, tx) => sum + Number(tx.amount), 0);
    return {
      status,
      label: STATUS_LABELS[status],
      volume,
      count: txs.length,
      color: STATUS_COLORS[status],
    };
  });

  const CustomTooltip = ({
    active,
    payload,
  }: {
    active?: boolean;
    payload?: { payload: ChartEntry }[];
  }) => {
    if (!active || !payload?.length) return null;
    const entry = payload[0].payload;
    return (
      <div className="rounded-lg border bg-background p-3 shadow-md text-sm">
        <p className="font-semibold mb-1">{entry.label}</p>
        <p className="text-muted-foreground">
          Volume:{" "}
          <span className="text-foreground font-medium">
            {new Intl.NumberFormat("en-US", {
              style: "currency",
              currency: "USD",
              minimumFractionDigits: 2,
            }).format(entry.volume)}
          </span>
        </p>
        <p className="text-muted-foreground">
          Count:{" "}
          <span className="text-foreground font-medium">{entry.count}</span>
        </p>
      </div>
    );
  };

  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart
        data={chartData}
        margin={{ top: 8, right: 8, bottom: 0, left: 8 }}
        barSize={48}
      >
        <CartesianGrid strokeDasharray="3 3" className="stroke-border" vertical={false} />
        <XAxis
          dataKey="label"
          tick={{ fontSize: 12, fill: "hsl(var(--muted-foreground))" }}
          axisLine={false}
          tickLine={false}
        />
        <YAxis
          tickFormatter={formatCurrency}
          tick={{ fontSize: 11, fill: "hsl(var(--muted-foreground))" }}
          axisLine={false}
          tickLine={false}
          width={60}
        />
        <Tooltip content={<CustomTooltip />} cursor={{ fill: "hsl(var(--muted))" }} />
        <Bar dataKey="volume" radius={[6, 6, 0, 0]}>
          {chartData.map((entry) => (
            <Cell key={entry.status} fill={entry.color} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  );
}

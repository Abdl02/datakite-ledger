"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { TransactionResponse } from "@/lib/api";
import { Activity, CheckCircle, Clock, DollarSign, XCircle } from "lucide-react";

interface StatsCardsProps {
  data: TransactionResponse[];
}

export function StatsCards({ data }: StatsCardsProps) {
  const totalVolume = data.reduce((sum, tx) => sum + Number(tx.amount), 0);
  const completed = data.filter((tx) => tx.status === "COMPLETED").length;
  const pending = data.filter(
    (tx) => tx.status === "PENDING" || tx.status === "PENDING_REVIEW"
  ).length;
  const rejected = data.filter((tx) => tx.status === "REJECTED").length;

  const cards = [
    {
      title: "Total Volume",
      value: new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        notation: "compact",
        maximumFractionDigits: 2,
      }).format(totalVolume),
      sub: `${data.length} transactions`,
      icon: <DollarSign className="h-4 w-4 text-muted-foreground" />,
    },
    {
      title: "Completed",
      value: completed.toLocaleString(),
      sub: `${data.length ? ((completed / data.length) * 100).toFixed(1) : 0}% of total`,
      icon: <CheckCircle className="h-4 w-4 text-emerald-500" />,
    },
    {
      title: "Pending",
      value: pending.toLocaleString(),
      sub: "Pending + Under review",
      icon: <Clock className="h-4 w-4 text-amber-500" />,
    },
    {
      title: "Rejected",
      value: rejected.toLocaleString(),
      sub: `${data.length ? ((rejected / data.length) * 100).toFixed(1) : 0}% of total`,
      icon: <XCircle className="h-4 w-4 text-red-500" />,
    },
  ];

  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
      {cards.map((card) => (
        <Card key={card.title}>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              {card.title}
            </CardTitle>
            {card.icon}
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold tracking-tight">{card.value}</p>
            <p className="text-xs text-muted-foreground mt-1">{card.sub}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

# ── Stage 1: Install dependencies ─────────────────────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

# ── Stage 2: Build ────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# next.config.ts reads BACKEND_URL while evaluating rewrites and writes the
# result into .next/routes-manifest.json at build time — it is NOT re-read at
# runtime.  Pass the Docker-internal URL via a build arg so the manifest is
# baked with the correct destination.  Defaults to localhost for local dev.
ARG BACKEND_URL=http://localhost:8080
ENV BACKEND_URL=$BACKEND_URL

# Bake the API base as a relative path so the Next.js server-side rewrite
# proxies all /api/* calls to the backend container without CORS issues.
ENV NEXT_PUBLIC_API_BASE=/api/v1

RUN npm run build

# ── Stage 3: Runtime ──────────────────────────────────────────────────────────
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
# Bind to all interfaces inside the container
ENV HOSTNAME=0.0.0.0
ENV PORT=3000

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Standalone output bundles only what is needed to run the server
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 3000

CMD ["node", "server.js"]

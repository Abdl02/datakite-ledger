import type { NextConfig } from "next";

// BACKEND_URL is supplied as a Docker build arg (not a runtime env var) because
// Next.js evaluates next.config.ts at build time and bakes rewrites into
// .next/routes-manifest.json.  Locally it falls back to localhost unchanged.
const BACKEND_URL = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  // Enable standalone output for the production Docker image
  output: "standalone",

  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${BACKEND_URL}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;

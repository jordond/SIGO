import { defineWorkersConfig } from "@cloudflare/vitest-pool-workers/config";

export default defineWorkersConfig({
  test: {
    poolOptions: {
      workers: {
        wrangler: { configPath: "./wrangler.json" },
        miniflare: {
          bindings: {
            FORECAST_API_KEY: "test-api-key",
          },
        },
      },
    },
  },
});

import { SELF } from "cloudflare:test";
import { describe, it, expect } from "vitest";

const ALLOWED_ORIGIN = "https://shouldigooutside.now";
const VALID_CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";

const VALID_HEADERS = {
  Origin: ALLOWED_ORIGIN,
  "X-Client-ID": VALID_CLIENT_ID,
};

describe("Worker", () => {
  describe("Version endpoint (GET /)", () => {
    it("returns 200 with version data", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: VALID_HEADERS,
      });

      expect(response.status).toBe(200);
      const body = (await response.json()) as { data: { version: unknown } };
      expect(body.data).toBeDefined();
      expect(body.data.version).toBeDefined();
    });

    it("includes CORS headers for allowed origin", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: VALID_HEADERS,
      });

      expect(response.headers.get("Access-Control-Allow-Origin")).toBe(ALLOWED_ORIGIN);
    });

    it("includes cache-control header", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: VALID_HEADERS,
      });

      expect(response.headers.get("Cache-Control")).toContain("max-age=");
    });
  });

  describe("Authentication", () => {
    it("returns 401 when X-Client-ID is missing", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: { Origin: ALLOWED_ORIGIN },
      });

      expect(response.status).toBe(401);
    });

    it("returns 401 when X-Client-ID is not a valid UUID", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: {
          Origin: "https://shouldigooutside.now",
          "X-Client-ID": "not-a-uuid",
        },
      });

      expect(response.status).toBe(401);
    });
  });

  describe("CORS", () => {
    it("returns 403 for disallowed origin", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: {
          Origin: "https://evil.example.com",
          "X-Client-ID": VALID_CLIENT_ID,
        },
      });

      expect(response.status).toBe(403);
    });

    it("handles OPTIONS preflight request", async () => {
      const response = await SELF.fetch("https://api.test/", {
        method: "OPTIONS",
        headers: { Origin: ALLOWED_ORIGIN },
      });

      expect(response.status).toBe(204);
      expect(response.headers.get("Access-Control-Allow-Origin")).toBe(ALLOWED_ORIGIN);
      expect(response.headers.get("Access-Control-Allow-Methods")).toContain(
        "GET",
      );
    });

    it("allows localhost origins", async () => {
      const response = await SELF.fetch("https://api.test/", {
        headers: {
          Origin: "http://localhost:4321",
          "X-Client-ID": VALID_CLIENT_ID,
        },
      });

      expect(response.status).not.toBe(403);
    });
  });

  describe("Routing", () => {
    it("returns 404 for unknown path", async () => {
      const response = await SELF.fetch("https://api.test/nonexistent", {
        headers: VALID_HEADERS,
      });

      expect(response.status).toBe(404);
    });

    it("returns 405 for unsupported method on known route", async () => {
      const response = await SELF.fetch("https://api.test/", {
        method: "DELETE",
        headers: VALID_HEADERS,
      });

      expect(response.status).toBe(405);
    });
  });
});

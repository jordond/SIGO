// @ts-check
import { defineConfig } from "astro/config"
import sitemap from "@astrojs/sitemap"
import icon from "astro-icon"

import cloudflare from "@astrojs/cloudflare"

export default defineConfig({
  site: "https://shouldigooutside.now",
  integrations: [icon(), sitemap()],
  output: "static",
  adapter: cloudflare({ imageService: "compile" }),
})

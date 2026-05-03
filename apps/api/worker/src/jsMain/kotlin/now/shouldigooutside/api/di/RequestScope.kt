package now.shouldigooutside.api.di

/**
 * Koin scope qualifier marking objects whose lifetime is bounded by a single
 * Cloudflare Worker `fetch` invocation. Resources tied to per-request I/O
 * (HttpClient, CoroutineScope, KvCache, route handlers) must be declared
 * inside `scope<RequestScope>` and resolved via a Koin scope opened in
 * `App.handle()`.
 */
internal class RequestScope

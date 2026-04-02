// Polyfill process.hrtime for Kotlin/JS TimeSource.Monotonic.
// Kotlin's stdlib checks `process.versions.node` and uses process.hrtime() if truthy.
// In workerd, `process` exists (via esbuild polyfill) but `hrtime` is not available.
if (typeof process !== "undefined" && !process.hrtime) {
    process.hrtime = function(prev) {
        const now = performance.now();
        const sec = Math.floor(now / 1000);
        const nano = Math.floor((now % 1000) * 1e6);
        if (prev) {
            let ds = sec - prev[0];
            let dn = nano - prev[1];
            if (dn < 0) { ds--; dn += 1e9; }
            return [ds, dn];
        }
        return [sec, nano];
    };
}

const lib = await import("./build/compileSync/js/main/productionExecutable/kotlin/index.mjs")

export default {
    async fetch(request, env, ctx) {
        return lib.fetch(request, env)
    }
}

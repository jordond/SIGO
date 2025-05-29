import * as worker from "./build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

export default {
    async fetch(request, env, ctx) {
        return worker.fetch(request)
    }
}
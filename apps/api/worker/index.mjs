import * as lib from "./build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

export default {
    async fetch(request, env, ctx) {
        return lib.fetch(request, env)
    }
}
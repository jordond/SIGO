package app.sigot.core.api.server.attestation

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

@JsModule("cbor-x")
@JsNonModule
external object CborX {
    fun decode(buffer: Uint8Array): dynamic

    fun encode(value: dynamic): Uint8Array
}

internal fun cborDecode(data: ByteArray): dynamic {
    val uint8 = Uint8Array(data.toTypedArray())
    return CborX.decode(uint8)
}

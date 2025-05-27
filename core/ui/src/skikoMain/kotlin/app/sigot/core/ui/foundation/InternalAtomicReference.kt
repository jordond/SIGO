package app.sigot.core.ui.foundation

import kotlinx.atomicfu.atomic

internal actual class InternalAtomicReference<V> actual constructor(
    value: V,
) {
    private val delegate = atomic(value)

    actual fun get() = delegate.value

    actual fun set(value: V) {
        delegate.value = value
    }

    actual fun getAndSet(value: V) = delegate.getAndSet(value)

    actual fun compareAndSet(
        expect: V,
        newValue: V,
    ) = delegate.compareAndSet(expect, newValue)
}

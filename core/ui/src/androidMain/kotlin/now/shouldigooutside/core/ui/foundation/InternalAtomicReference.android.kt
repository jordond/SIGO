package now.shouldigooutside.core.ui.foundation

import java.util.concurrent.atomic.AtomicReference

internal actual class InternalAtomicReference<V> actual constructor(
    value: V,
) {
    private val atomicReference = AtomicReference(value)

    actual fun get(): V = atomicReference.get()

    actual fun set(value: V) {
        atomicReference.set(value)
    }

    actual fun getAndSet(value: V): V = atomicReference.getAndSet(value)

    actual fun compareAndSet(
        expect: V,
        newValue: V,
    ): Boolean = atomicReference.compareAndSet(expect, newValue)
}

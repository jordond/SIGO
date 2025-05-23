package app.sigot.core.foundation.ktx

import app.sigot.core.platform.currentTimeMillis
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Ensure that a coroutine takes at least [timeInMillis] milliseconds to execute.
 *
 * This is useful when showing a loading UI state, it will ensure the loading UI is visible for at
 * least [timeInMillis] milliseconds.
 *
 * If [block] takes longer than [timeInMillis] then the thread will not delay at all. However if
 * [block] takes less that [timeInMillis], it will calculate the remaining required delay.
 *
 * @param[T] Return type of your async [block] call.
 * @param[timeInMillis] Ensure the execution of [block] takes _at least_ this many milliseconds.
 * @param[block] The async operation you want to delay.
 * @return The result of the call to [block].
 */
public suspend inline fun <T> ensureExecutionTime(
    timeInMillis: Long,
    crossinline block: suspend () -> T,
): T {
    val startTime = currentTimeMillis()

    val result = block()

    val executionTime = currentTimeMillis() - startTime
    val delayTime = timeInMillis - executionTime
    if (delayTime > 0) {
        Logger.d { "Execution took $executionTime, delaying for $delayTime ms" }
        delay(delayTime)
    } else {
        Logger.d { "Execution took $executionTime, no delay required" }
    }

    return result
}

/**
 * Convenience function for re-throwing a [CancellationException]
 *
 * When using a `try {} catch {}` in a coroutine you have to cooperate with Coroutine's
 * "cooperative cancellation". So you need to check if the error thrown is a [CancellationException].
 *
 * Incorrect example:
 *
 * ```
 * suspend fun doSomeWork() {
 *     try {
 *         thisMightThrow()
 *     catch (cause: Throwable) {
 *         Log.e("Something bad happened", cause)
 *     }
 * }
 * ```
 *
 * The above code is swallowing the [Throwable] and if it is a [CancellationException] it will be
 * ignored.
 *
 * The correct way is to catch the [CancellationException] separately and re-throw:
 *
 * ```
 * try {
 *     thisMightThrow()
 * } catch (cancellation: CancellationException) {
 *     throw cancellation
 * } catch (cause: Throwable) {
 *     Log.e("Something bad happened", cause)
 * }
 * ```
 *
 * Or you can check with an `if` statement:
 *
 * ```
 * try {
 *     thisMightThrow()
 * } catch (cause: Throwable) {
 *     if (cause is CancellationException) throw cancellation
 *     Log.e("Something bad happened", cause)
 * }
 * ```
 *
 * This function is a convenience function for the conditional approach:
 *
 * ```
 * try {
 *     thisMightThrow()
 * } catch (cause: Throwable) {
 *     // Will throw and stop execution if is Cancellation
 *     cause.checkCancellation()
 *     Log.e("Something bad happened", cause)
 * }
 * ```
 *
 * @receiver An instance of [Throwable] that could be a [CancellationException].
 * @throws CancellationException if [Throwable] coroutine is cancelling.
 */
public fun Throwable.checkCancellation() {
    if (this is CancellationException) {
        throw this
    }
}

public val Job?.active: Boolean
    get() = this != null && this.isActive

public inline fun <T, R> Flow<T>.mapDistinct(crossinline transform: suspend (value: T) -> R): Flow<R> =
    map(transform).distinctUntilChanged()

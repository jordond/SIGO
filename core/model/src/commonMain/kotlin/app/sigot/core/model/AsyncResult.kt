package app.sigot.core.model

import app.sigot.core.model.AsyncResult.Error
import app.sigot.core.model.AsyncResult.Loading
import app.sigot.core.model.AsyncResult.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

public sealed interface AsyncResult<out T> {
    public data object Loading : AsyncResult<Nothing>

    public data class Success<T>(
        val data: T,
    ) : AsyncResult<T>

    public data class Error(
        val error: Throwable,
    ) : AsyncResult<Nothing>
}

public val <T> AsyncResult<T>?.isLoading: Boolean get() = this is Loading

public fun <T> AsyncResult<T>?.getOrNull(): T? =
    when (this) {
        is Loading -> null
        is Error -> null
        is Success -> data
        null -> null
    }

public fun <T> Result<T>.toAsyncResult(): AsyncResult<T> =
    fold(
        onSuccess = { Success(it) },
        onFailure = { Error(it) },
    )

public fun <T, R> AsyncResult<T>.mapSuccess(transform: (T) -> R): AsyncResult<R> =
    when (this) {
        is Loading -> Loading
        is Error -> Error(error)
        is Success -> try {
            Success(transform(data))
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause
            Error(cause)
        }
    }

public fun <T, R> Flow<AsyncResult<T>>.mapSuccess(transform: (T) -> R): Flow<AsyncResult<R>> =
    this.map { result -> result.mapSuccess(transform) }

public fun <T> T.asAsyncResult(): AsyncResult<T> = Success(this)

public fun <T> T?.asAsyncResultOrNull(): AsyncResult<T>? = this?.asAsyncResult()

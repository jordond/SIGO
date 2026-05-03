package now.shouldigooutside.api.di

import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.promise
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.foundation.di.foundationModule
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RequestScopeCloseTest {
    private lateinit var koin: Koin

    @BeforeTest
    fun setUp() {
        koin = startKoin {
            modules(
                module { single<Json> { Json.Default } },
                foundationModule(),
                requestModule(),
            )
        }.koin
    }

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun closingRequestScopeCancelsCoroutineScope() {
        val scope = koin.createScope<RequestScope>(Uuid.random().toString())
        val coroutineScope = scope.get<CoroutineScope>()

        scope.close()

        assertFalse(
            coroutineScope.isActive,
            "CoroutineScope must be cancelled when Koin scope closes",
        )
    }

    @Test
    fun closingRequestScopeClosesHttpClient() {
        val scope = koin.createScope<RequestScope>(Uuid.random().toString())
        val client: HttpClient = scope.get()
        val clientJob = client.coroutineContext[Job]
            ?: error("HttpClient must expose a Job in its coroutineContext")

        scope.close()

        assertFalse(
            clientJob.isActive,
            "HttpClient's coroutineContext Job must be cancelled when Koin scope closes",
        )
    }

    // Guards the App.handle invariant: closing the Koin scope must not run inside
    // a coroutine that lives in the scope's CoroutineScope. Doing so cancels the
    // very deferred backing the returned Promise.
    @Test
    fun closingScopeFromInsidePromiseRejectsIt(): Promise<Unit> {
        val scope = koin.createScope<RequestScope>(Uuid.random().toString())
        val coroutineScope = scope.get<CoroutineScope>()

        val promise = coroutineScope.promise {
            try {
                "ok"
            } finally {
                scope.close()
            }
        }

        return promise.then(
            onFulfilled = { value -> error("expected cancellation, got $value") },
            onRejected = { cause ->
                assertTrue(
                    cause is CancellationException,
                    "expected CancellationException, got ${cause::class.simpleName}: $cause",
                )
            },
        )
    }

    @Test
    fun closingScopeAfterPromiseSettlesResolvesNormally(): Promise<Unit> {
        val scope = koin.createScope<RequestScope>(Uuid.random().toString())
        val coroutineScope = scope.get<CoroutineScope>()

        return coroutineScope.promise { "ok" }.then(
            onFulfilled = { value ->
                scope.close()
                assertEquals("ok", value)
            },
            onRejected = { cause -> error("expected success, got $cause") },
        )
    }
}

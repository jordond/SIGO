package now.shouldigooutside.whatsnew.data

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.Version
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.FakeVersionProvider
import now.shouldigooutside.test.NullableStore
import now.shouldigooutside.whatsnew.data.entity.WhatsNewStateEntity
import kotlin.test.Test
import kotlin.time.Instant

class KeyValueWhatsNewRepoTest {
    private val registryPages = WhatsNewRegistry.pages
    private val registryVersion = registryPages.maxOf { it.version }

    @Test
    fun unseenEntries_whenLastSeenBelowRegistryVersion_returnsUnseenPages() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = registryVersion - 1),
            )
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.unseenEntries.value shouldHaveSize registryPages.count { it.version > registryVersion - 1 }
        }

    @Test
    fun unseenEntries_whenLastSeenAtRegistryVersion_returnsEmpty() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = registryVersion),
            )
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.unseenEntries.value.shouldBeEmpty()
        }

    @Test
    fun unseenEntries_whenLastSeenIsNull_fallsBackToCurrentMinusOne() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = null),
            )
            // current - 1 < registryVersion → entries visible
            val repo = createRepo(store = store, versionCode = registryVersion)

            repo.unseenEntries.value shouldHaveSize registryPages.count { it.version > registryVersion - 1 }
        }

    @Test
    fun unseenEntries_whenLastSeenIsNull_andCurrentAboveRegistry_returnsEmpty() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = null),
            )
            // current - 1 == registryVersion → no entries visible
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.unseenEntries.value.shouldBeEmpty()
        }

    @Test
    fun unseenEntries_whenStoreEmpty_returnsEmptyInitialValue() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.unseenEntries.value.shouldBeEmpty()
        }

    @Test
    fun initialize_freshInstall_marksAllEntriesAsSeen() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val versionCode = registryVersion + 1
            val repo = createRepo(
                store = store,
                versionCode = versionCode,
                hasCompletedOnboarding = false,
            )

            repo.initialize()

            val entity = store.get()
            entity?.initialized shouldBe true
            entity?.lastSeenVersionCode shouldBe versionCode
        }

    @Test
    fun initialize_existingUser_setsLastSeenBelowLatestRegistryVersion() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val versionCode = registryVersion + 1
            val repo = createRepo(
                store = store,
                versionCode = versionCode,
                hasCompletedOnboarding = true,
            )

            repo.initialize()

            val entity = store.get()
            entity?.initialized shouldBe true
            entity?.lastSeenVersionCode shouldBe registryVersion - 1
        }

    @Test
    fun initialize_whenAlreadyInitialized_doesNotModifyState() =
        runTest(UnconfinedTestDispatcher()) {
            val existing = WhatsNewStateEntity(initialized = true, lastSeenVersionCode = 5)
            val store = NullableStore(existing)
            val repo = createRepo(store = store, versionCode = 20)

            repo.initialize()

            store.get() shouldBe existing
        }

    @Test
    fun initialize_existingUser_unseenEntriesReflectsNewState() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val repo = createRepo(
                store = store,
                versionCode = registryVersion + 1,
                hasCompletedOnboarding = true,
            )

            repo.initialize()

            repo.unseenEntries.value shouldHaveSize registryPages.count { it.version > registryVersion - 1 }
        }

    @Test
    fun markSeen_updatesLastSeenToCurrentVersionCode() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = 0),
            )
            val versionCode = registryVersion + 1
            val repo = createRepo(store = store, versionCode = versionCode)

            repo.markSeen()

            store.get()?.lastSeenVersionCode shouldBe versionCode
        }

    @Test
    fun markSeen_whenStoreEmpty_createsNewEntity() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val versionCode = 15
            val repo = createRepo(store = store, versionCode = versionCode)

            repo.markSeen()

            val entity = store.get()
            entity?.initialized shouldBe true
            entity?.lastSeenVersionCode shouldBe versionCode
        }

    @Test
    fun markSeen_clearsUnseenEntries() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = 0),
            )
            val versionCode = registryVersion + 1
            val repo = createRepo(store = store, versionCode = versionCode)

            repo.markSeen()

            repo.unseenEntries.value.shouldBeEmpty()
        }

    @Test
    fun reset_setsLastSeenToZero() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = registryVersion + 1),
            )
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.reset()
            advanceUntilIdle()

            store.get()?.lastSeenVersionCode shouldBe 0
        }

    @Test
    fun reset_whenStoreEmpty_createsEntityWithLastSeenZero() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore<WhatsNewStateEntity>()
            val repo = createRepo(store = store, versionCode = 15)

            repo.reset()
            advanceUntilIdle()

            val entity = store.get()
            entity?.initialized shouldBe true
            entity?.lastSeenVersionCode shouldBe 0
        }

    @Test
    fun reset_makesAllRegistryEntriesVisible() =
        runTest(UnconfinedTestDispatcher()) {
            val store = NullableStore(
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = registryVersion + 1),
            )
            val repo = createRepo(store = store, versionCode = registryVersion + 1)

            repo.reset()
            advanceUntilIdle()

            repo.unseenEntries.value shouldHaveSize registryPages.size
        }

    private fun kotlinx.coroutines.test.TestScope.createRepo(
        store: NullableStore<WhatsNewStateEntity> = NullableStore(),
        versionCode: Int = 15,
        hasCompletedOnboarding: Boolean = false,
    ): KeyValueWhatsNewRepo {
        val settings = Settings(
            firstLaunch = Instant.fromEpochSeconds(0),
            hasCompletedOnboarding = hasCompletedOnboarding,
            loaded = true,
        )
        return KeyValueWhatsNewRepo(
            store = store,
            settingsRepo = FakeSettingsRepo(settings),
            registry = WhatsNewRegistry,
            versionProvider = FakeVersionProvider(Version(code = versionCode, name = "$versionCode.0.0")),
            scope = backgroundScope,
        )
    }
}

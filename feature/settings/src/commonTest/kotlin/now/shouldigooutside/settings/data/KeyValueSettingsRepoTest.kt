package now.shouldigooutside.settings.data

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.settings.data.entity.SettingsEntity
import now.shouldigooutside.settings.data.entity.toEntity
import now.shouldigooutside.settings.data.entity.toModel
import now.shouldigooutside.test.NullableStore
import kotlin.test.Test
import kotlin.time.Instant

class KeyValueSettingsRepoTest {
    @Test
    fun settings_whenStoreIsEmpty_exposesDefaultSettings() =
        runTest {
            val store = NullableStore<SettingsEntity>()
            val repo = KeyValueSettingsRepo(store, backgroundScope)

            repo.settings.value.themeMode shouldBe ThemeMode.Light
            repo.settings.value.includeAirQuality shouldBe true
            repo.settings.value.loaded shouldBe false
        }

    @Test
    fun settings_whenStoreHasValue_mapsEntityToSettings() =
        runTest {
            val stored = Settings(
                firstLaunch = Instant.fromEpochSeconds(0),
                themeMode = ThemeMode.Dark,
                includeAirQuality = false,
            )
            val store = NullableStore(stored.toEntity())
            val repo = KeyValueSettingsRepo(store, backgroundScope)

            repo.settings.test {
                awaitItem().loaded shouldBe false

                val mapped = awaitItem()
                mapped.themeMode shouldBe ThemeMode.Dark
                mapped.includeAirQuality shouldBe false
                mapped.loaded shouldBe true
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun update_persistsTransformedSettingsToStore() =
        runTest {
            val initial = Settings(firstLaunch = Instant.fromEpochSeconds(0))
            val store = NullableStore(initial.toEntity())
            val repo = KeyValueSettingsRepo(store, backgroundScope)

            repo.settings.test {
                awaitItem()
                awaitItem()

                repo.update { it.copy(themeMode = ThemeMode.Dark, includeAirQuality = false) }
                advanceUntilIdle()

                val updated = awaitItem()
                updated.themeMode shouldBe ThemeMode.Dark
                updated.includeAirQuality shouldBe false
                updated.loaded shouldBe true
                cancelAndIgnoreRemainingEvents()
            }

            store.get()?.toModel()?.themeMode shouldBe ThemeMode.Dark
            store.get()?.toModel()?.includeAirQuality shouldBe false
        }

    @Test
    fun reset_replacesStoreWithDefaultSettings() =
        runTest {
            val initial = Settings(
                firstLaunch = Instant.fromEpochSeconds(0),
                themeMode = ThemeMode.Dark,
                includeAirQuality = false,
            )
            val store = NullableStore(initial.toEntity())
            val repo = KeyValueSettingsRepo(store, backgroundScope)

            repo.settings.test {
                awaitItem()
                awaitItem()

                repo.reset()
                advanceUntilIdle()

                val reset = awaitItem()
                reset.themeMode shouldBe ThemeMode.Light
                reset.includeAirQuality shouldBe true
                reset.loaded shouldBe true
                cancelAndIgnoreRemainingEvents()
            }

            store.get()?.toModel()?.themeMode shouldBe ThemeMode.Light
            store.get()?.toModel()?.includeAirQuality shouldBe true
        }
}

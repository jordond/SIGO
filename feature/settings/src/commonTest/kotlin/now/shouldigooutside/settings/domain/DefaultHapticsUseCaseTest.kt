package now.shouldigooutside.settings.domain

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.test.FakeSettingsRepo
import kotlin.test.Test

class DefaultHapticsUseCaseTest {
    private val settingsRepo = FakeSettingsRepo()
    private val useCase = DefaultHapticsUseCase(settingsRepo = settingsRepo)

    @Test
    fun isEnabledReturnsTrueByDefault() {
        useCase.isEnabled() shouldBe true
    }

    @Test
    fun isEnabledReturnsFalseWhenDisabled() {
        settingsRepo.update { it.copy(enableHaptics = false) }

        useCase.isEnabled() shouldBe false
    }

    @Test
    fun updateSetsEnableHaptics() {
        useCase.update(false)

        settingsRepo.settings.value.enableHaptics shouldBe false
    }

    @Test
    fun updatesFlowEmitsOnChange() =
        runTest {
            useCase.updates().test {
                awaitItem() shouldBe true // initial

                settingsRepo.update { it.copy(enableHaptics = false) }
                awaitItem() shouldBe false

                cancel()
            }
        }

    @Test
    fun updatesFlowDoesNotEmitOnIrrelevantChange() =
        runTest {
            useCase.updates().test {
                awaitItem() // initial

                settingsRepo.update { it.copy(themeMode = ThemeMode.Dark) }

                expectNoEvents()
                cancel()
            }
        }

    @Test
    fun updatesFlowEmitsDistinctOnly() =
        runTest {
            useCase.updates().test {
                awaitItem() // initial true

                settingsRepo.update { it.copy(enableHaptics = true) }

                expectNoEvents()
                cancel()
            }
        }
}

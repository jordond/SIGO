package now.shouldigooutside.settings.domain

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.settings.InternalSettings
import now.shouldigooutside.test.FakeSettingsRepo
import kotlin.test.Test

class SettingsIsSimulateFailureUseCaseTest {
    private val settingsRepo = FakeSettingsRepo()
    private val useCase = SettingsIsSimulateFailureUseCase(settingsRepo = settingsRepo)

    @Test
    fun returnsFalseByDefault() {
        useCase() shouldBe false
    }

    @Test
    fun returnsTrueWhenSimulateFailureEnabled() {
        settingsRepo.update {
            it.copy(internalSettings = InternalSettings(simulateFailure = true))
        }

        useCase() shouldBe true
    }
}

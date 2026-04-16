package now.shouldigooutside.core.model.forecast

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AlertTest {
    @Test
    fun descriptionParagraphs_withSeparators_splitsAndTrims() {
        val alert = Alert(title = "t", description = "a ### b ###c")
        alert.descriptionParagraphs shouldBe listOf("a", "b", "c")
    }

    @Test
    fun descriptionParagraphs_noSeparators_returnsSingleton() {
        val alert = Alert(title = "t", description = "single paragraph")
        alert.descriptionParagraphs shouldBe listOf("single paragraph")
    }

    @Test
    fun descriptionParagraphs_emptyDescription_returnsEmpty() {
        val alert = Alert(title = "t", description = "")
        alert.descriptionParagraphs shouldBe emptyList()
    }

    @Test
    fun descriptionParagraphs_blankParagraphsDropped() {
        val alert = Alert(title = "t", description = "a ###   ### b")
        alert.descriptionParagraphs shouldBe listOf("a", "b")
    }

    @Test
    fun defaults_allOptionalFieldsAreNull() {
        val alert = Alert(title = "t", description = "d")
        alert.event shouldBe null
        alert.headline shouldBe null
        alert.onset shouldBe null
        alert.ends shouldBe null
        alert.link shouldBe null
        alert.id shouldBe null
    }
}

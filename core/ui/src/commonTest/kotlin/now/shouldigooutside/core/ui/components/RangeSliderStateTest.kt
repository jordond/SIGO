package now.shouldigooutside.core.ui.components

import now.shouldigooutside.core.ui.foundation.slider.RangeSliderState
import kotlin.test.Test
import kotlin.test.assertEquals

class RangeSliderStateTest {
    @Test
    fun updateActiveRangePreservesValueWhenRangeMovesAboveCurrentState() {
        val state = RangeSliderState(
            activeRangeStart = 5f,
            activeRangeEnd = 35f,
            valueRange = 243.15f..313.15f,
        )

        state.updateActiveRange(278.15f..308.15f)

        assertEquals(278.15f, state.activeRangeStart)
        assertEquals(308.15f, state.activeRangeEnd)
    }

    @Test
    fun updateActiveRangePreservesValueWhenRangeMovesBelowCurrentState() {
        val state = RangeSliderState(
            activeRangeStart = 278.15f,
            activeRangeEnd = 308.15f,
            valueRange = -30f..40f,
        )

        state.updateActiveRange(5f..35f)

        assertEquals(5f, state.activeRangeStart)
        assertEquals(35f, state.activeRangeEnd)
    }

    @Test
    fun updateActiveRangeClampsOutOfBoundsValues() {
        val state = RangeSliderState(
            activeRangeStart = 5f,
            activeRangeEnd = 35f,
            valueRange = -30f..40f,
        )

        state.updateActiveRange(60f..-50f)

        assertEquals(-30f, state.activeRangeStart)
        assertEquals(40f, state.activeRangeEnd)
    }
}

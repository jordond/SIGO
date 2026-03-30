package now.shouldigooutside.core.model.forecast

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AirQualityTest {
    @Test
    fun constructor_valueZero_constructsSuccessfully() {
        shouldNotThrow<IllegalArgumentException> { AirQuality(0) }
    }

    @Test
    fun constructor_value10_constructsSuccessfully() {
        shouldNotThrow<IllegalArgumentException> { AirQuality(10) }
    }

    @Test
    fun constructor_valueMidRange_constructsSuccessfully() {
        shouldNotThrow<IllegalArgumentException> { AirQuality(5) }
    }

    @Test
    fun constructor_belowZero_throwsIllegalArgumentException() {
        shouldThrow<IllegalArgumentException> { AirQuality(-1) }
    }

    @Test
    fun constructor_above10_throwsIllegalArgumentException() {
        shouldThrow<IllegalArgumentException> { AirQuality(11) }
    }

    @Test
    fun hasData_valueIsZero_returnsFalse() {
        AirQuality(0).hasData shouldBe false
    }

    @Test
    fun hasData_valueIsNonZero_returnsTrue() {
        AirQuality(1).hasData shouldBe true
    }

    @Test
    fun none_valueIsZero() {
        AirQuality.None.value shouldBe 0
    }

    @Test
    fun from_nullValue_returnsNull() {
        AirQuality.from(null) shouldBe null
    }

    @Test
    fun from_valueAbove10_coercesTo10() {
        val result = AirQuality.from(15)
        result?.value shouldBe 10
    }

    @Test
    fun from_valueBelowZero_coercesTo0() {
        val result = AirQuality.from(-5)
        result?.value shouldBe 0
    }

    @Test
    fun from_validValue_returnsAirQualityWithSameValue() {
        val result = AirQuality.from(7)
        result?.value shouldBe 7
    }
}

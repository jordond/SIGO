package app.sigot.core.api.server.util

import app.sigot.core.api.server.exception.BadRequestException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CoordinatesTest {
    @Test
    fun roundCoordinateDefaultsTwoDecimalPlaces() {
        12.3456.roundCoordinate() shouldBe 12.35
    }

    @Test
    fun roundCoordinateRespectsCustomDecimals() {
        12.3456.roundCoordinate(3) shouldBe 12.346
    }

    @Test
    fun roundCoordinateZeroDecimals() {
        12.6.roundCoordinate(0) shouldBe 13.0
    }

    @Test
    fun roundCoordinateNegativeValue() {
        (-45.678).roundCoordinate() shouldBe -45.68
    }

    @Test
    fun roundCoordinateZero() {
        0.0.roundCoordinate() shouldBe 0.0
    }

    @Test
    fun roundCoordinateAlreadyRounded() {
        12.34.roundCoordinate() shouldBe 12.34
    }

    @Test
    fun validateCoordinatesAcceptsValidValues() {
        shouldNotThrow<BadRequestException> {
            validateCoordinates(lat = 45.0, lon = -90.0)
        }
    }

    @Test
    fun validateCoordinatesAcceptsBoundaryValues() {
        shouldNotThrow<BadRequestException> {
            validateCoordinates(lat = 90.0, lon = 180.0)
        }
        shouldNotThrow<BadRequestException> {
            validateCoordinates(lat = -90.0, lon = -180.0)
        }
    }

    @Test
    fun validateCoordinatesAcceptsZero() {
        shouldNotThrow<BadRequestException> {
            validateCoordinates(lat = 0.0, lon = 0.0)
        }
    }

    @Test
    fun validateCoordinatesRejectsLatAbove90() {
        shouldThrow<BadRequestException> {
            validateCoordinates(lat = 90.1, lon = 0.0)
        }
    }

    @Test
    fun validateCoordinatesRejectsLatBelowNeg90() {
        shouldThrow<BadRequestException> {
            validateCoordinates(lat = -90.1, lon = 0.0)
        }
    }

    @Test
    fun validateCoordinatesRejectsLonAbove180() {
        shouldThrow<BadRequestException> {
            validateCoordinates(lat = 0.0, lon = 180.1)
        }
    }

    @Test
    fun validateCoordinatesRejectsLonBelowNeg180() {
        shouldThrow<BadRequestException> {
            validateCoordinates(lat = 0.0, lon = -180.1)
        }
    }

    @Test
    fun validateCoordinatesReportsBothErrors() {
        shouldThrow<BadRequestException> {
            validateCoordinates(lat = 91.0, lon = 181.0)
        }
    }
}

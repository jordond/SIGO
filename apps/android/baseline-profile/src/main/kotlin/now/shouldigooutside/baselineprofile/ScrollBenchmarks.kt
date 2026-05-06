package now.shouldigooutside.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import now.shouldigooutside.baselineprofile.support.PACKAGE_NAME
import now.shouldigooutside.baselineprofile.support.scrollHomeForecastList
import now.shouldigooutside.baselineprofile.support.seedBenchmark
import now.shouldigooutside.baselineprofile.support.waitForHomeTabLoaded
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollHomeForecast() =
        rule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
            setupBlock = { seedBenchmark() },
        ) {
            startActivityAndWait()
            waitForHomeTabLoaded()
            scrollHomeForecastList()
        }
}

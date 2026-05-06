package now.shouldigooutside.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import now.shouldigooutside.baselineprofile.support.PACKAGE_NAME
import now.shouldigooutside.baselineprofile.support.coldLaunch
import now.shouldigooutside.baselineprofile.support.openForecastDetailsAndBack
import now.shouldigooutside.baselineprofile.support.scrollHomeForecastList
import now.shouldigooutside.baselineprofile.support.switchAllTabs
import now.shouldigooutside.baselineprofile.support.waitForHomeTabLoaded
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect(
            packageName = PACKAGE_NAME,
            includeInStartupProfile = true,
        ) {
            coldLaunch()
            waitForHomeTabLoaded()
            switchAllTabs()
            scrollHomeForecastList()
            openForecastDetailsAndBack()
        }
}

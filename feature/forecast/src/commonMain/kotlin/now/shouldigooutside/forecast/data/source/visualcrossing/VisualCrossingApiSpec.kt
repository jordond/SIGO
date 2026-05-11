package now.shouldigooutside.forecast.data.source.visualcrossing

internal object VisualCrossingApiSpec {
    const val BaseUrl: String =
        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"

    const val UnitGroup: String = "base"
    const val Include: String = "days,hours,alerts,current,events"
    const val Elements: String = "+aqius,+aqieur"

    fun params(token: String): List<Pair<String, String>> =
        listOf(
            "key" to token,
            "unitGroup" to UnitGroup,
            "include" to Include,
            "elements" to Elements,
        )
}

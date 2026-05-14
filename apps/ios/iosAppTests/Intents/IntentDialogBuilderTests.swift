import XCTest
@testable import SIGOT

final class IntentDialogBuilderTests: XCTestCase {
    private let templates = StubIntentDialogTemplates()
    private let locale = Locale(identifier: "en_US_POSIX")

    func test_yes_fresh_substitutesLocationAndTemp() {
        let data = WidgetDataFixtures.make(scoreResult: .Yes)
        let result = IntentDialogBuilder.spokenString(
            data: data, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "Y Toronto/22°C")
    }

    func test_maybe_fresh_usesMaybeTemplate() {
        let data = WidgetDataFixtures.make(scoreResult: .Maybe, locationName: "Vancouver", formattedTemp: "9°C")
        let result = IntentDialogBuilder.spokenString(
            data: data, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "M Vancouver/9°C")
    }

    func test_no_fresh_usesNoTemplate() {
        let data = WidgetDataFixtures.make(scoreResult: .No, locationName: "Halifax", formattedTemp: "-3°C")
        let result = IntentDialogBuilder.spokenString(
            data: data, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "N Halifax/-3°C")
    }

    func test_yes_stale_appendsSuffixSeparatedBySpace() {
        let data = WidgetDataFixtures.make(scoreResult: .Yes, isStale: true)
        let result = IntentDialogBuilder.spokenString(
            data: data, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "Y Toronto/22°C STALE")
    }

    func test_no_stale_appendsSuffixSeparatedBySpace() {
        let data = WidgetDataFixtures.make(scoreResult: .No, isStale: true)
        let result = IntentDialogBuilder.spokenString(
            data: data, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "N Toronto/22°C STALE")
    }

    func test_nilData_returnsNoLocationDialog_andIgnoresStaleSuffix() {
        let result = IntentDialogBuilder.spokenString(
            data: nil, templates: templates, locale: locale
        )
        XCTAssertEqual(result, "no-loc")
    }
}

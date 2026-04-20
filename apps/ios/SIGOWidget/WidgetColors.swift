import SwiftUI

/// Widget colors mirrored from core/ui Color.kt LightColors/DarkColors.
/// Update when the app theme colors change.
struct WidgetThemeColors {
    let success: Color
    let primary: Color
    let error: Color
    let surface: Color
    let text: Color
    let textSecondary: Color
    let onSuccess: Color

    func scoreColor(for result: ScoreResult) -> Color {
        switch result {
        case .Yes: return success
        case .Maybe: return primary
        case .No: return error
        }
    }
}

private let lightColors = WidgetThemeColors(
    success: Color(hex: 0xFF90EE90),
    primary: Color(hex: 0xFFFFDB02),
    error: Color(hex: 0xFFFF6B6B),
    surface: Color(hex: 0xFFFFF5EA),
    text: .black,
    textSecondary: Color(hex: 0xFF333333),
    onSuccess: .black
)

private let darkColors = WidgetThemeColors(
    success: Color(hex: 0xFF7FFFD4),
    primary: Color(hex: 0xFFFCCF03),
    error: Color(hex: 0xFFFF8C91),
    surface: Color(hex: 0xFF2A2236),
    text: Color(hex: 0xFFF8F8F8),
    textSecondary: Color(hex: 0xFFAAAAAA),
    onSuccess: .black
)

func widgetColors(scheme: ColorScheme) -> WidgetThemeColors {
    scheme == .dark ? darkColors : lightColors
}

extension Color {
    /// Text laid over a score color background. Black at varying alphas mirrors
    /// the Android widget's Black* color providers.
    static let widgetOnScore = Color.black
    static let widgetOnScoreLocation = Color.black.opacity(0.8)
    static let widgetOnScoreTitle = Color.black.opacity(0.7)
    static let widgetOnScoreStale = Color.black.opacity(0.6)
}

private extension Color {
    init(hex: UInt64) {
        let a = Double((hex >> 24) & 0xFF) / 255.0
        let r = Double((hex >> 16) & 0xFF) / 255.0
        let g = Double((hex >> 8) & 0xFF) / 255.0
        let b = Double(hex & 0xFF) / 255.0
        self.init(red: r, green: g, blue: b, opacity: a)
    }
}

import SwiftUI

/// Widget colors derived from core/ui Color.kt LightColors/DarkColors.
/// Keep in sync with core/ui/src/commonMain/.../Color.kt
struct WidgetThemeColors {
    let success: Color      // ScoreResult.Yes
    let primary: Color      // ScoreResult.Maybe
    let error: Color        // ScoreResult.No
    let surface: Color
    let background: Color
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

// Matches core/ui LightColors
private let lightColors = WidgetThemeColors(
    success: Color(hex: 0xFF90EE90),
    primary: Color(hex: 0xFFFFDB02),
    error: Color(hex: 0xFFFF6B6B),
    surface: Color(hex: 0xFFFFF5EA),
    background: Color(hex: 0xFFFEECDE),
    text: .black,
    textSecondary: Color(hex: 0xFF333333),
    onSuccess: .black
)

// Matches core/ui DarkColors
private let darkColors = WidgetThemeColors(
    success: Color(hex: 0xFF7FFFD4),
    primary: Color(hex: 0xFFFCCF03),
    error: Color(hex: 0xFFFF8C91),
    surface: Color(hex: 0xFF2A2236),
    background: Color(hex: 0xFF141228),
    text: Color(hex: 0xFFF8F8F8),
    textSecondary: Color(hex: 0xFFAAAAAA),
    onSuccess: .black
)

func widgetColors(scheme: ColorScheme) -> WidgetThemeColors {
    scheme == .dark ? darkColors : lightColors
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

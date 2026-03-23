import SwiftUI

enum WidgetColors {
    // Light theme
    static let scoreYesLight = Color(red: 0x90 / 255.0, green: 0xEE / 255.0, blue: 0x90 / 255.0) // Mint
    static let scoreMaybeLight = Color(red: 0xFF / 255.0, green: 0xDB / 255.0, blue: 0x02 / 255.0) // YellowAlt
    static let scoreNoLight = Color(red: 0xFF / 255.0, green: 0x6B / 255.0, blue: 0x6B / 255.0) // Coral
    static let backgroundLight = Color(red: 0xFE / 255.0, green: 0xEC / 255.0, blue: 0xDE / 255.0) // WarmBeige
    static let surfaceLight = Color(red: 0xFF / 255.0, green: 0xF5 / 255.0, blue: 0xEA / 255.0) // LightBeige

    // Dark theme
    static let scoreYesDark = Color(red: 0x7F / 255.0, green: 0xFF / 255.0, blue: 0xD4 / 255.0) // BrightMint
    static let scoreMaybeDark = Color(red: 0xFC / 255.0, green: 0xCF / 255.0, blue: 0x03 / 255.0) // BrightYellow
    static let scoreNoDark = Color(red: 0xFF / 255.0, green: 0x8C / 255.0, blue: 0x91 / 255.0) // BrightCoral
    static let backgroundDark = Color(red: 0x14 / 255.0, green: 0x12 / 255.0, blue: 0x28 / 255.0) // NeoDarkBackground
    static let surfaceDark = Color(red: 0x2A / 255.0, green: 0x22 / 255.0, blue: 0x36 / 255.0) // NeoDarkSurface

    static func scoreColor(for result: String, scheme: ColorScheme) -> Color {
        let isDark = scheme == .dark
        switch result {
        case "Yes": return isDark ? scoreYesDark : scoreYesLight
        case "No": return isDark ? scoreNoDark : scoreNoLight
        default: return isDark ? scoreMaybeDark : scoreMaybeLight
        }
    }

    static let scoreTextColor = Color.black

    static func backgroundColor(scheme: ColorScheme) -> Color {
        scheme == .dark ? backgroundDark : backgroundLight
    }

    static func surfaceColor(scheme: ColorScheme) -> Color {
        scheme == .dark ? surfaceDark : surfaceLight
    }

    static func textColor(scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(white: 0.97) : Color.black
    }

    static func textSecondaryColor(scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(white: 0.67) : Color(red: 0x33 / 255.0, green: 0x33 / 255.0, blue: 0x33 / 255.0)
    }
}

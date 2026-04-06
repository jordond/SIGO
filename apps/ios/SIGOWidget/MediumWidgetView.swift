import SwiftUI
import WidgetKit

struct MediumWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            let colors = widgetColors(scheme: colorScheme)

            HStack(spacing: 0) {
                // Left side: score badge
                VStack(spacing: 4) {
                    if let activityName = data.activityName {
                        Text(activityName)
                            .font(.system(size: 10, weight: .medium))
                            .foregroundColor(colors.onSuccess.opacity(0.7))
                    }

                    Text(data.scoreResult.rawValue.uppercased())
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(colors.onSuccess)

                    Text(data.formattedTemp)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(colors.onSuccess)

                    Text(data.locationName)
                        .font(.system(size: 11))
                        .foregroundColor(colors.onSuccess.opacity(0.8))
                        .lineLimit(1)
                }
                .frame(width: 120)
                .frame(maxHeight: .infinity)
                .background(colors.scoreColor(for: data.scoreResult))

                // Right side: weather details
                VStack(alignment: .leading, spacing: 4) {
                    DetailRow(label: String(localized: "widget_feels_like"), value: data.formattedFeelsLike, colors: colors)
                    DetailRow(label: String(localized: "widget_wind"), value: data.formattedWind, colors: colors)
                    DetailRow(label: String(localized: "widget_precip"), value: "\(data.precipChance)%", colors: colors)
                    DetailRow(label: String(localized: "widget_today"), value: data.todayScoreResult.rawValue, colors: colors)

                    if data.alertCount > 0 {
                        Text("\(data.alertCount) alert\(data.alertCount > 1 ? "s" : "")")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(colors.error)
                    }

                    if data.isStale {
                        Text(data.updatedAgo)
                            .font(.system(size: 9))
                            .foregroundColor(colors.textSecondary)
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }
            .containerBackground(
                colors.surface,
                for: .widget
            )
        } else {
            EmptyWidgetView()
        }
    }
}

private struct DetailRow: View {
    let label: String
    let value: String
    let colors: WidgetThemeColors

    var body: some View {
        HStack(spacing: 4) {
            Text("\(label):")
                .font(.system(size: 12))
                .foregroundColor(colors.textSecondary)
            Text(value)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(colors.text)
        }
    }
}

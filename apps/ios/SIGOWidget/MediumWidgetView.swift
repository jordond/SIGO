import SwiftUI
import WidgetKit

struct MediumWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            HStack(spacing: 0) {
                // Left side: score badge
                VStack(spacing: 4) {
                    if let activityName = data.activityName, activityName != WidgetData.defaultActivityName {
                        Text(activityName)
                            .font(.system(size: 10, weight: .medium))
                            .foregroundColor(WidgetColors.scoreTextColor.opacity(0.7))
                    }

                    Text(data.scoreResult.uppercased())
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(WidgetColors.scoreTextColor)

                    Text(data.formattedTemp)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(WidgetColors.scoreTextColor)

                    Text(data.locationName)
                        .font(.system(size: 11))
                        .foregroundColor(WidgetColors.scoreTextColor.opacity(0.8))
                        .lineLimit(1)
                }
                .frame(width: 120)
                .frame(maxHeight: .infinity)
                .background(WidgetColors.scoreColor(for: data.scoreResult, scheme: colorScheme))

                // Right side: weather details
                VStack(alignment: .leading, spacing: 4) {
                    DetailRow(label: "Feels like", value: data.formattedFeelsLike, colorScheme: colorScheme)
                    DetailRow(label: "Wind", value: data.formattedWind, colorScheme: colorScheme)
                    DetailRow(label: "Precip", value: "\(data.precipChance)%", colorScheme: colorScheme)
                    DetailRow(label: "Today", value: data.todayScoreResult, colorScheme: colorScheme)

                    if data.alertCount > 0 {
                        Text("\(data.alertCount) alert\(data.alertCount > 1 ? "s" : "")")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(WidgetColors.scoreNoLight)
                    }

                    if data.isStale {
                        Text(data.updatedAgo)
                            .font(.system(size: 9))
                            .foregroundColor(WidgetColors.textSecondaryColor(scheme: colorScheme))
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }
            .containerBackground(
                WidgetColors.surfaceColor(scheme: colorScheme),
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
    let colorScheme: ColorScheme

    var body: some View {
        HStack(spacing: 4) {
            Text("\(label):")
                .font(.system(size: 12))
                .foregroundColor(WidgetColors.textSecondaryColor(scheme: colorScheme))
            Text(value)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(WidgetColors.textColor(scheme: colorScheme))
        }
    }
}

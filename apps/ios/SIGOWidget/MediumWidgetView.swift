import SwiftUI
import WidgetKit

struct MediumWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            let colors = widgetColors(scheme: colorScheme)
            let scoreColor = colors.scoreColor(for: data.scoreResult)

            HStack(spacing: 0) {
                ScoreBadge(data: data)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)

                VStack(alignment: .leading, spacing: 4) {
                    if let activityName = data.activityName {
                        BrutalDetailRow(
                            label: String(localized: "widget_activity"),
                            value: activityName
                        )
                    }
                    BrutalDetailRow(
                        label: String(localized: "widget_feels_like"),
                        value: data.formattedFeelsLike
                    )
                    BrutalDetailRow(
                        label: String(localized: "widget_wind"),
                        value: data.formattedWind
                    )
                    BrutalDetailRow(
                        label: String(localized: "widget_precip"),
                        value: "\(data.precipChance)%"
                    )
                    BrutalDetailRow(
                        label: String(localized: "widget_today"),
                        value: data.todayScoreLabel
                    )

                    if data.isStale {
                        Text(data.updatedAgoLabel)
                            .font(.system(size: 9))
                            .foregroundColor(colors.textSecondary)
                    }
                }
                .padding(.horizontal, 16)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }
            .containerBackground(for: .widget) {
                HStack(spacing: 0) {
                    scoreColor
                        .frame(maxWidth: .infinity)
                    colors.surface
                        .frame(maxWidth: .infinity)
                }
            }
        } else {
            EmptyWidgetView()
        }
    }
}

private struct ScoreBadge: View {
    let data: WidgetData

    var body: some View {
        VStack(spacing: 2) {
            Text(data.scoreLabel.uppercased())
                .font(.system(size: 56, weight: .bold).italic())
                .foregroundColor(.widgetOnScore)
                .lineLimit(1)
                .minimumScaleFactor(0.5)

            Text(data.formattedTemp)
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(.widgetOnScore)
                .lineLimit(1)

            Text(data.locationName)
                .font(.system(size: 10, weight: .medium))
                .foregroundColor(.widgetOnScoreLocation)
                .lineLimit(1)
                .padding(.top, 2)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 10)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct BrutalDetailRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: 8) {
            Text(label.uppercased())
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.widgetOnScore)
            Spacer(minLength: 0)
            Text(value)
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.widgetOnScore)
                .lineLimit(1)
        }
    }
}

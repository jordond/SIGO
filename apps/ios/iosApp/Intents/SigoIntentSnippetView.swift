import SwiftUI

struct SigoIntentSnippetView: View {
    @Environment(\.colorScheme) private var colorScheme

    let data: WidgetData?

    var body: some View {
        if let data {
            populated(data: data)
        } else {
            empty
        }
    }

    private func populated(data: WidgetData) -> some View {
        HStack(spacing: 12) {
            Circle()
                .fill(widgetColors(scheme: colorScheme).scoreColor(for: data.scoreResult))
                .frame(width: 36, height: 36)
                .overlay(
                    Text(data.scoreLabel)
                        .font(.caption.bold())
                        .foregroundStyle(Color.widgetOnScore)
                )
            VStack(alignment: .leading, spacing: 2) {
                Text(data.locationName)
                    .font(.subheadline.bold())
                    .lineLimit(1)
                HStack(spacing: 6) {
                    Text(data.formattedTemp)
                        .font(.caption)
                    if data.isStale {
                        Text(NSLocalizedString("siri_intent_stale_suffix", comment: ""))
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                    }
                }
            }
            Spacer(minLength: 0)
        }
        .padding(12)
        .frame(maxWidth: 220, maxHeight: 60, alignment: .leading)
    }

    private var empty: some View {
        Text(NSLocalizedString("siri_intent_no_location_dialog", comment: ""))
            .font(.caption)
            .padding(12)
            .frame(maxWidth: 220, maxHeight: 60, alignment: .leading)
    }
}

#Preview("Yes") {
    SigoIntentSnippetView(data: WidgetData.sample)
}

#Preview("No / stale") {
    SigoIntentSnippetView(data: WidgetData(
        scoreResult: .No,
        scoreLabel: "NO",
        locationName: "Halifax",
        formattedTemp: "-3°C",
        formattedFeelsLike: "-7°C",
        formattedWind: "30 km/h",
        precipChance: 80,
        todayScoreResult: .No,
        todayScoreLabel: "No",
        alertCount: 1,
        updatedAtMillis: 0,
        isStale: true,
        updatedAgoLabel: "1h ago",
        activityName: nil
    ))
}

#Preview("Empty") {
    SigoIntentSnippetView(data: nil)
}

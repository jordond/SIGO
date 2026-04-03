import SwiftUI
import WidgetKit

struct SmallWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            VStack(spacing: 4) {
                Text(data.scoreResult.rawValue.uppercased())
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(WidgetColors.scoreTextColor)

                Text(data.formattedTemp)
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(WidgetColors.scoreTextColor)

                Text(data.locationName)
                    .font(.system(size: 12))
                    .foregroundColor(WidgetColors.scoreTextColor.opacity(0.8))
                    .lineLimit(1)

                if data.isStale {
                    Text(data.updatedAgo)
                        .font(.system(size: 9))
                        .foregroundColor(WidgetColors.scoreTextColor.opacity(0.6))
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .containerBackground(
                WidgetColors.scoreColor(for: data.scoreResult, scheme: colorScheme),
                for: .widget
            )
        } else {
            EmptyWidgetView()
        }
    }
}

struct EmptyWidgetView: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        VStack(spacing: 4) {
            Text("Open SIGO")
                .font(.system(size: 14, weight: .medium))
            Text("to get started")
                .font(.system(size: 12))
                .foregroundColor(WidgetColors.textSecondaryColor(scheme: colorScheme))
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .containerBackground(
            WidgetColors.surfaceColor(scheme: colorScheme),
            for: .widget
        )
    }
}

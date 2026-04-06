import SwiftUI
import WidgetKit

struct SmallWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            let colors = widgetColors(scheme: colorScheme)

            VStack(spacing: 4) {
                Text(data.scoreResult.rawValue.uppercased())
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(colors.onSuccess)

                Text(data.formattedTemp)
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(colors.onSuccess)

                Text(data.locationName)
                    .font(.system(size: 12))
                    .foregroundColor(colors.onSuccess.opacity(0.8))
                    .lineLimit(1)

                if data.isStale {
                    Text(data.updatedAgo)
                        .font(.system(size: 9))
                        .foregroundColor(colors.onSuccess.opacity(0.6))
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .containerBackground(
                colors.scoreColor(for: data.scoreResult),
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
        let colors = widgetColors(scheme: colorScheme)

        VStack(spacing: 4) {
            Text(String(localized: "widget_empty_title"))
                .font(.system(size: 14, weight: .medium))
            Text(String(localized: "widget_empty_subtitle"))
                .font(.system(size: 12))
                .foregroundColor(colors.textSecondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .containerBackground(
            colors.surface,
            for: .widget
        )
    }
}

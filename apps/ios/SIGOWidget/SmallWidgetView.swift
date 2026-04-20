import SwiftUI
import WidgetKit

struct SmallWidgetView: View {
    let data: WidgetData?
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        if let data = data {
            let colors = widgetColors(scheme: colorScheme)

            VStack(spacing: 2) {
                Text(String(localized: "widget_title"))
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.widgetOnScoreTitle)
                    .multilineTextAlignment(.center)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)

                Spacer(minLength: 0)

                Text(data.scoreLabel.uppercased())
                    .font(.system(size: 64, weight: .bold).italic())
                    .foregroundColor(.widgetOnScore)
                    .lineLimit(1)
                    .minimumScaleFactor(0.5)

                Spacer(minLength: 0)

                Text(data.formattedTemp)
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.widgetOnScore)
                    .lineLimit(1)

                Text(data.locationName)
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(.widgetOnScoreLocation)
                    .lineLimit(1)

                if data.isStale {
                    Text(data.updatedAgoLabel)
                        .font(.system(size: 9))
                        .foregroundColor(.widgetOnScoreStale)
                        .lineLimit(1)
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

        Text(String(localized: "widget_empty"))
            .font(.system(size: 14, weight: .bold))
            .foregroundColor(colors.text)
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .containerBackground(
                colors.surface,
                for: .widget
            )
    }
}

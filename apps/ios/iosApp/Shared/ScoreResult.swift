import Foundation

enum ScoreResult: String {
    case Yes, Maybe, No

    static func from(kmpName: String) -> ScoreResult {
        if let known = ScoreResult(rawValue: kmpName) { return known }
        // KMP enum case added without Swift bridge — fail loud in DEBUG.
        assertionFailure("Unknown KMP ScoreResult name: \(kmpName)")
        return .Maybe
    }
}

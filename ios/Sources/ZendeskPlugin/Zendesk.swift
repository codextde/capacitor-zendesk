import Foundation

@objc public class Zendesk: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}

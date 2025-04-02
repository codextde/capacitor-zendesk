// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CodextCapacitorZendesk",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CodextCapacitorZendesk",
            targets: ["ZendeskPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "ZendeskPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ZendeskPlugin"),
        .testTarget(
            name: "ZendeskPluginTests",
            dependencies: ["ZendeskPlugin"],
            path: "ios/Tests/ZendeskPluginTests")
    ]
)

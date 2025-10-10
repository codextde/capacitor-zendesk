import Foundation
import UIKit
import UserNotifications
import SupportSDK
import ZendeskCoreSDK

/**
 * Helper class for handling Zendesk push notifications on iOS
 *
 * Usage:
 * 1. In your AppDelegate, implement UNUserNotificationCenterDelegate
 * 2. Call ZendeskNotificationHelper methods to handle notifications
 * 3. Use the deep linking functionality to navigate to tickets
 */
@objc public class ZendeskNotificationHelper: NSObject {

    /**
     * Checks if a notification payload is from Zendesk
     *
     * - Parameter userInfo: The notification payload dictionary
     * - Returns: true if the notification is from Zendesk
     */
    @objc public static func isZendeskNotification(_ userInfo: [AnyHashable: Any]) -> Bool {
        return userInfo["zendesk_sdk_request_id"] != nil
    }

    /**
     * Extracts the request ID from a Zendesk notification payload
     *
     * - Parameter userInfo: The notification payload dictionary
     * - Returns: The request ID string, or nil if not found
     */
    @objc public static func getRequestId(from userInfo: [AnyHashable: Any]) -> String? {
        return userInfo["zendesk_sdk_request_id"] as? String
    }

    /**
     * Handles a Zendesk push notification when the app is in the foreground
     * This will refresh the comment stream if the ticket view is visible
     *
     * - Parameter userInfo: The notification payload dictionary
     * - Returns: true if the UI was refreshed, false otherwise
     */
    @objc public static func handleForegroundNotification(_ userInfo: [AnyHashable: Any]) -> Bool {
        guard let requestId = getRequestId(from: userInfo) else {
            return false
        }

        // Try to refresh the comment stream if it's visible
        return ZDKPushUtil.handlePush(userInfo, for: UIApplication.shared)
    }

    /**
     * Opens a specific Zendesk ticket
     *
     * - Parameters:
     *   - requestId: The Zendesk request/ticket ID
     *   - viewController: The view controller to present from
     */
    @objc public static func openTicket(requestId: String, from viewController: UIViewController?) {
        guard let viewController = viewController else {
            return
        }

        DispatchQueue.main.async {
            let config = RequestUiConfiguration()
            config.requestId = requestId

            let requestController = RequestUi.buildRequestUi(with: [config])
            let navController = UINavigationController(rootViewController: requestController)
            viewController.present(navController, animated: true, completion: nil)
        }
    }

    /**
     * Handles a notification tap by opening the relevant ticket
     * Call this from UNUserNotificationCenterDelegate's didReceive response method
     *
     * - Parameters:
     *   - userInfo: The notification payload dictionary
     *   - viewController: The view controller to present from
     * - Returns: true if the notification was handled, false otherwise
     */
    @objc public static func handleNotificationTap(_ userInfo: [AnyHashable: Any], from viewController: UIViewController?) -> Bool {
        guard let requestId = getRequestId(from: userInfo) else {
            return false
        }

        openTicket(requestId: requestId, from: viewController)
        return true
    }

    /**
     * Converts APNs device token to a string format
     * This is useful for registering the token with Zendesk
     *
     * - Parameter deviceToken: The device token data from APNs
     * - Returns: A hex string representation of the token
     */
    @objc public static func deviceTokenString(from deviceToken: Data) -> String {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        return tokenParts.joined()
    }
}

/**
 * Extension to help with AppDelegate integration
 *
 * Add this to your AppDelegate:
 *
 * ```swift
 * import UserNotifications
 *
 * class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
 *
 *     func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
 *         // Set notification delegate
 *         UNUserNotificationCenter.current().delegate = self
 *         return true
 *     }
 *
 *     func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
 *         let tokenString = ZendeskNotificationHelper.deviceTokenString(from: deviceToken)
 *         // Send this token to Zendesk via the plugin
 *         // Zendesk.registerPushNotifications({ deviceToken: tokenString })
 *     }
 *
 *     func userNotificationCenter(_ center: UNUserNotificationCenter,
 *                                willPresent notification: UNNotification,
 *                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
 *         let userInfo = notification.request.content.userInfo
 *
 *         if ZendeskNotificationHelper.isZendeskNotification(userInfo) {
 *             // Handle Zendesk notification
 *             let handled = ZendeskNotificationHelper.handleForegroundNotification(userInfo)
 *             if handled {
 *                 // UI was refreshed, don't show banner
 *                 completionHandler([])
 *                 return
 *             }
 *         }
 *
 *         // Show notification banner
 *         completionHandler([.banner, .sound, .badge])
 *     }
 *
 *     func userNotificationCenter(_ center: UNUserNotificationCenter,
 *                                didReceive response: UNNotificationResponse,
 *                                withCompletionHandler completionHandler: @escaping () -> Void) {
 *         let userInfo = response.notification.request.content.userInfo
 *
 *         if ZendeskNotificationHelper.isZendeskNotification(userInfo) {
 *             // Get the root view controller
 *             if let window = UIApplication.shared.windows.first,
 *                let rootViewController = window.rootViewController {
 *                 ZendeskNotificationHelper.handleNotificationTap(userInfo, from: rootViewController)
 *             }
 *         }
 *
 *         completionHandler()
 *     }
 * }
 * ```
 */

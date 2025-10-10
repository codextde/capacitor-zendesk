# Zendesk Push Notifications Setup Guide

This guide explains how to implement push notifications for Zendesk Support in your Capacitor application.

## Overview

The Capacitor Zendesk plugin provides support for handling push notifications from Zendesk Support, including:

- Device registration with Zendesk
- Device unregistration
- Notification payload handling
- Ticket deep-linking

## Prerequisites

### Android
1. **Firebase Cloud Messaging (FCM)**: Your app must be set up with Firebase and have FCM configured
2. **Zendesk Support SDK**: The plugin already includes the necessary Zendesk dependencies
3. **Firebase Messaging Plugin**: Install a Capacitor Firebase plugin (e.g., `@capacitor-firebase/messaging`)

### iOS
1. **Apple Push Notification Service (APNs)**: Your app must be configured for push notifications in Apple Developer Portal
2. **Zendesk Support SDK**: The plugin already includes the necessary Zendesk dependencies
3. **Push Notification Capability**: Enable push notifications in Xcode

## Installation Steps

## Android Setup

### 1. Install Firebase Dependencies

Add Firebase to your Android project following the [Firebase documentation](https://firebase.google.com/docs/android/setup).

In your app's `build.gradle`, add:

```gradle
dependencies {
    // Firebase BOM (Bill of Materials)
    implementation platform('com.google.firebase:firebase-bom:32.7.0')

    // Firebase Cloud Messaging
    implementation 'com.google.firebase:firebase-messaging'
}
```

### 2. Configure AndroidManifest.xml

Add the `ZendeskFirebaseMessagingService` to your app's `AndroidManifest.xml`:

```xml
<manifest>
    <application>
        <!-- Your other components -->

        <!-- Zendesk Firebase Messaging Service -->
        <service
            android:name="de.codext.capacitor.zendesk.ZendeskFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

### 3. Request FCM Token and Register with Zendesk

In your app code, get the FCM token and register it with Zendesk:

```typescript
import { Zendesk } from '@codext/capacitor-zendesk';
import { FirebaseMessaging } from '@capacitor-firebase/messaging';

async function setupPushNotifications() {
  try {
    // Request permission (iOS only, Android auto-grants)
    await FirebaseMessaging.requestPermissions();

    // Get the FCM token
    const result = await FirebaseMessaging.getToken();
    const fcmToken = result.token;

    // Register the token with Zendesk
    await Zendesk.registerPushNotifications({
      deviceToken: fcmToken
    });

    console.log('Successfully registered for Zendesk push notifications');
  } catch (error) {
    console.error('Failed to setup push notifications:', error);
  }
}
```

### 4. Handle Token Refresh

FCM tokens can change, so you should listen for token updates:

```typescript
import { FirebaseMessaging } from '@capacitor-firebase/messaging';

FirebaseMessaging.addListener('tokenReceived', async (event) => {
  console.log('New FCM token:', event.token);

  // Re-register with Zendesk
  await Zendesk.registerPushNotifications({
    deviceToken: event.token
  });
});
```

### 5. Unregister When User Signs Out

When a user signs out or disables notifications:

```typescript
async function unregisterPushNotifications() {
  try {
    await Zendesk.unregisterPushNotifications();
    console.log('Successfully unregistered from Zendesk push notifications');
  } catch (error) {
    console.error('Failed to unregister:', error);
  }
}
```

## iOS Setup

### 1. Enable Push Notifications in Xcode

1. Open your iOS project in Xcode
2. Select your target
3. Go to "Signing & Capabilities"
4. Click "+ Capability" and add "Push Notifications"
5. Add "Background Modes" capability and enable "Remote notifications"

### 2. Configure APNs in Apple Developer Portal

1. Go to [Apple Developer Portal](https://developer.apple.com/)
2. Create an APNs certificate or key for your app
3. Configure your app identifier with push notifications enabled

### 3. Update AppDelegate.swift

Add push notification handling to your `AppDelegate.swift`:

```swift
import UIKit
import Capacitor
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Set notification delegate
        UNUserNotificationCenter.current().delegate = self

        // Register for remote notifications
        application.registerForRemoteNotifications()

        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        // Convert device token to string
        let tokenString = ZendeskNotificationHelper.deviceTokenString(from: deviceToken)

        // Send this token to your app via NotificationCenter or other method
        // so you can register it with Zendesk via the plugin
        NotificationCenter.default.post(name: Notification.Name("APNsDeviceToken"), object: tokenString)
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error)")
    }

    // Handle notification when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo

        if ZendeskNotificationHelper.isZendeskNotification(userInfo) {
            // Try to refresh the UI if ticket view is visible
            let handled = ZendeskNotificationHelper.handleForegroundNotification(userInfo)
            if handled {
                // UI was refreshed, don't show banner
                completionHandler([])
                return
            }
        }

        // Show notification banner
        if #available(iOS 14.0, *) {
            completionHandler([.banner, .sound, .badge])
        } else {
            completionHandler([.alert, .sound, .badge])
        }
    }

    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo

        if ZendeskNotificationHelper.isZendeskNotification(userInfo) {
            // Get the root view controller
            if let window = UIApplication.shared.windows.first,
               let rootViewController = window.rootViewController {
                ZendeskNotificationHelper.handleNotificationTap(userInfo, from: rootViewController)
            }
        }

        completionHandler()
    }
}
```

### 4. Request Permission and Register with Zendesk

In your app code (TypeScript/JavaScript):

```typescript
import { Zendesk } from '@codext/capacitor-zendesk';

async function setupPushNotificationsIOS() {
  try {
    // Request notification permission
    const permission = await Capacitor.Plugins.PushNotifications.requestPermissions();

    if (permission.receive === 'granted') {
      // Listen for device token
      Capacitor.Plugins.PushNotifications.addListener('registration', async (token) => {
        console.log('APNs token:', token.value);

        // Register with Zendesk
        await Zendesk.registerPushNotifications({
          deviceToken: token.value
        });

        console.log('Successfully registered for Zendesk push notifications');
      });

      // Register for push notifications (triggers didRegisterForRemoteNotificationsWithDeviceToken in AppDelegate)
      await Capacitor.Plugins.PushNotifications.register();
    }
  } catch (error) {
    console.error('Failed to setup push notifications:', error);
  }
}
```

Alternatively, if using `@capacitor-firebase/messaging`:

```typescript
import { Zendesk } from '@codext/capacitor-zendesk';
import { FirebaseMessaging } from '@capacitor-firebase/messaging';
import { Capacitor } from '@capacitor/core';

async function setupPushNotificationsIOS() {
  try {
    // Request permission
    await FirebaseMessaging.requestPermissions();

    // Get APNs token (on iOS, Firebase returns the APNs token)
    const result = await FirebaseMessaging.getToken();

    // Register with Zendesk
    await Zendesk.registerPushNotifications({
      deviceToken: result.token
    });

    console.log('Successfully registered for Zendesk push notifications');
  } catch (error) {
    console.error('Failed to setup push notifications:', error);
  }
}
```

### 5. Handle Zendesk Notifications in Your App

Import the notification helper in your AppDelegate (already done in step 3):

```swift
import de_codext_capacitor_zendesk // Your module name
```

## Cross-Platform Setup

### Unified Registration Code

Here's a cross-platform approach that works for both iOS and Android:

```typescript
import { Zendesk } from '@codext/capacitor-zendesk';
import { Capacitor } from '@capacitor/core';
import { PushNotifications } from '@capacitor/push-notifications';

async function setupPushNotifications() {
  try {
    // Request permission
    const permission = await PushNotifications.requestPermissions();

    if (permission.receive !== 'granted') {
      console.log('Push notification permission denied');
      return;
    }

    // Register for push notifications
    await PushNotifications.register();

    // Listen for registration success
    PushNotifications.addListener('registration', async (token) => {
      console.log('Device token:', token.value);

      try {
        // Register with Zendesk
        await Zendesk.registerPushNotifications({
          deviceToken: token.value
        });
        console.log('Successfully registered with Zendesk');
      } catch (error) {
        console.error('Failed to register with Zendesk:', error);
      }
    });

    // Listen for registration errors
    PushNotifications.addListener('registrationError', (error) => {
      console.error('Push registration error:', error);
    });

  } catch (error) {
    console.error('Failed to setup push notifications:', error);
  }
}
```

## Notification Handling

### Android

The `ZendeskFirebaseMessagingService` automatically handles:

1. **Automatic UI Refresh**: If the ticket conversation screen is visible, it will automatically refresh when a new message arrives
2. **Notification Display**: If the screen is not visible, a system notification is shown
3. **Deep Linking**: Tapping the notification opens the specific ticket in your app

### iOS

The `ZendeskNotificationHelper` class provides automatic handling:

1. **Automatic UI Refresh**: If the ticket conversation screen is visible, it will automatically refresh when a new message arrives
2. **Notification Display**: Handled through `UNUserNotificationCenterDelegate` methods in AppDelegate
3. **Deep Linking**: Tapping the notification opens the specific ticket in your app

## Deep Linking

The plugin provides deep linking support through platform-specific `ZendeskNotificationHelper` classes:

### Automatic Deep Linking

When a user taps a Zendesk notification, the app will automatically:
1. Open the app (if closed)
2. Navigate to the specific ticket
3. Preserve the back stack (ticket list → specific ticket)

### Manual Deep Linking (Android)

You can manually trigger deep linking in your Android code:

```kotlin
import de.codext.capacitor.zendesk.ZendeskNotificationHelper

// Open a specific ticket
ZendeskNotificationHelper.openTicket(context, requestId)

// Check if an intent is from Zendesk
if (ZendeskNotificationHelper.isZendeskNotification(intent)) {
    val requestId = ZendeskNotificationHelper.getRequestIdFromIntent(intent)
    // Handle the notification
}
```

### Manual Deep Linking (iOS)

You can manually trigger deep linking in your iOS code:

```swift
import de_codext_capacitor_zendesk

// Open a specific ticket
ZendeskNotificationHelper.openTicket(requestId: "123", from: viewController)

// Check if notification is from Zendesk
if ZendeskNotificationHelper.isZendeskNotification(userInfo) {
    let requestId = ZendeskNotificationHelper.getRequestId(from: userInfo)
    // Handle the notification
}
```

## Customization

### Android

#### Custom Notification Icon

The default notification icon is `android.R.drawable.ic_dialog_info`. To use your own icon:

1. Add your icon to `res/drawable/`
2. Modify `ZendeskFirebaseMessagingService.kt`:

```kotlin
.setSmallIcon(R.drawable.ic_notification) // Your custom icon
```

#### Custom Notification Channel

You can customize the notification channel by modifying the `createNotificationChannel()` method in `ZendeskFirebaseMessagingService.kt`:

```kotlin
val channel = NotificationChannel(
    ZENDESK_NOTIFICATION_CHANNEL_ID,
    "Your Channel Name",
    NotificationManager.IMPORTANCE_HIGH // Change importance level
).apply {
    description = "Your channel description"
    enableLights(true)
    lightColor = Color.BLUE
    enableVibration(true)
}
```

### iOS

#### Custom Notification Presentation

Customize how notifications appear by modifying the `willPresent` method in AppDelegate:

```swift
func userNotificationCenter(_ center: UNUserNotificationCenter,
                            willPresent notification: UNNotification,
                            withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
    // Customize presentation options
    if #available(iOS 14.0, *) {
        completionHandler([.banner, .sound, .badge, .list])
    } else {
        completionHandler([.alert, .sound, .badge])
    }
}
```

#### Custom Notification Sound

1. Add your sound file (.caf, .wav, or .aiff) to your Xcode project
2. Configure the sound in your push notification payload from Zendesk backend

## Testing

1. **Test Device Registration**:
   - Call `registerPushNotifications()` with a valid FCM token
   - Check Zendesk Support dashboard to verify the device is registered

2. **Test Notifications**:
   - Create a test ticket in Zendesk
   - Reply to the ticket from the Zendesk dashboard
   - Verify the notification appears on the device

3. **Test Deep Linking**:
   - Tap the notification
   - Verify the app opens to the correct ticket
   - Test the back button navigation

## Troubleshooting

### Android

#### Notifications Not Received

- Verify FCM is properly configured in your Firebase Console
- Check that `google-services.json` is in the correct location
- Ensure the app has notification permissions
- Verify the device token was successfully registered with Zendesk
- Check that `ZendeskFirebaseMessagingService` is declared in `AndroidManifest.xml`

#### Deep Linking Not Working

- Verify `ZendeskFirebaseMessagingService` is declared in `AndroidManifest.xml`
- Check that Zendesk is properly initialized before registering for push
- Ensure the `requestId` in the notification matches the actual ticket ID

#### Build Errors

If you encounter build errors related to Firebase:
- Make sure Firebase BOM version is compatible with your project
- Sync Gradle files
- Clean and rebuild the project

### iOS

#### Notifications Not Received

- Verify APNs certificate/key is properly configured in Apple Developer Portal
- Ensure push notifications capability is enabled in Xcode
- Check that the app has notification permissions
- Verify the device token was successfully registered with Zendesk
- Make sure you're testing on a real device (push notifications don't work in simulator)

#### Deep Linking Not Working

- Verify `UNUserNotificationCenterDelegate` is properly set in AppDelegate
- Check that Zendesk is properly initialized before registering for push
- Ensure the `requestId` in the notification matches the actual ticket ID
- Verify the `ZendeskNotificationHelper` class is accessible in your AppDelegate

#### Build Errors

If you encounter build errors:
- Make sure ZendeskSupportSDK is properly installed via CocoaPods
- Run `pod install` in the iOS directory
- Clean build folder (Cmd+Shift+K) and rebuild
- Verify Swift version compatibility

#### Token Registration Issues

If device token registration fails:
- Check that you have a valid APNs certificate or key
- Ensure your provisioning profile includes push notifications
- Verify your app identifier matches the one in Apple Developer Portal

## API Reference

### TypeScript

```typescript
interface PushRegistrationOptions {
  deviceToken: string;
}

// Register device for push notifications
await Zendesk.registerPushNotifications(options: PushRegistrationOptions): Promise<void>

// Unregister device from push notifications
await Zendesk.unregisterPushNotifications(): Promise<void>
```

### Android (Kotlin)

```kotlin
// Register with device token
fun registerPushNotifications(
    deviceToken: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
)

// Unregister device
fun unregisterPushNotifications(
    onSuccess: () -> Unit,
    onError: (String) -> Unit
)
```

### iOS (Swift)

```swift
// Register with device token
func registerPushNotifications(
    _ deviceToken: String,
    completion: @escaping (Error?) -> Void
)

// Unregister device
func unregisterPushNotifications(
    completion: @escaping (Error?) -> Void
)

// Helper methods
ZendeskNotificationHelper.isZendeskNotification(_ userInfo: [AnyHashable: Any]) -> Bool
ZendeskNotificationHelper.getRequestId(from userInfo: [AnyHashable: Any]) -> String?
ZendeskNotificationHelper.handleForegroundNotification(_ userInfo: [AnyHashable: Any]) -> Bool
ZendeskNotificationHelper.openTicket(requestId: String, from viewController: UIViewController?)
ZendeskNotificationHelper.handleNotificationTap(_ userInfo: [AnyHashable: Any], from viewController: UIViewController?) -> Bool
ZendeskNotificationHelper.deviceTokenString(from deviceToken: Data) -> String
```

## Additional Resources

### General
- [Capacitor Push Notifications Guide](https://capacitorjs.com/docs/guides/push-notifications-firebase)
- [@capacitor/push-notifications](https://capacitorjs.com/docs/apis/push-notifications)

### Android
- [Zendesk Android Push Notification Documentation](https://developer.zendesk.com/documentation/classic-web-widget-sdks/support-sdk/android/handle_push_notifications_wh/)
- [Firebase Cloud Messaging Documentation](https://firebase.google.com/docs/cloud-messaging)
- [Android Notification Channels](https://developer.android.com/develop/ui/views/notifications/channels)

### iOS
- [Zendesk iOS Push Notification Documentation](https://developer.zendesk.com/documentation/classic-web-widget-sdks/support-sdk/ios/handle_push_notifications_wh/)
- [Apple Push Notification Service](https://developer.apple.com/documentation/usernotifications)
- [UserNotifications Framework](https://developer.apple.com/documentation/usernotifications)

package de.codext.capacitor.zendesk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import zendesk.support.Support

/**
 * Firebase Messaging Service for handling Zendesk push notifications
 *
 * To use this service, add it to your AndroidManifest.xml:
 *
 * <service
 *     android:name="de.codext.capacitor.zendesk.ZendeskFirebaseMessagingService"
 *     android:exported="false">
 *     <intent-filter>
 *         <action android:name="com.google.firebase.MESSAGING_EVENT" />
 *     </intent-filter>
 * </service>
 */
class ZendeskFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val ZENDESK_NOTIFICATION_CHANNEL_ID = "zendesk_notifications"
        private const val ZENDESK_NOTIFICATION_CHANNEL_NAME = "Zendesk Support"
        private const val ZENDESK_SDK_ID_KEY = "zendesk_sdk_request_id"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message is from Zendesk
        val requestId = remoteMessage.data[ZENDESK_SDK_ID_KEY]

        if (requestId != null) {
            // Handle Zendesk notification
            handleZendeskNotification(remoteMessage, requestId)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // The token should be sent to Zendesk using the registerPushNotifications method
        // This is typically handled by the app when it gets the token from FCM
    }

    private fun handleZendeskNotification(remoteMessage: RemoteMessage, requestId: String) {
        val title = remoteMessage.notification?.title ?: "Zendesk Support"
        val body = remoteMessage.notification?.body ?: "You have a new message"

        // Try to refresh the comment stream if it's visible
        val refreshed = Support.INSTANCE.refreshRequest(requestId, applicationContext)

        // If the conversation UI is not visible, show a notification
        if (!refreshed) {
            showNotification(title, body, requestId)
        }
    }

    private fun showNotification(title: String, body: String, requestId: String) {
        createNotificationChannel()

        // Create an intent for deep linking
        val notificationIntent = ZendeskNotificationHelper.createDeepLinkIntent(
            applicationContext,
            requestId
        )

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            requestId.hashCode(),
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val notification = NotificationCompat.Builder(applicationContext, ZENDESK_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestId.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ZENDESK_NOTIFICATION_CHANNEL_ID,
                ZENDESK_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for Zendesk Support tickets"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

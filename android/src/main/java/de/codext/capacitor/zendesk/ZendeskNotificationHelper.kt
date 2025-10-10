package de.codext.capacitor.zendesk

import android.content.Context
import android.content.Intent
import zendesk.support.request.RequestActivity
import zendesk.support.requestlist.RequestListActivity

/**
 * Helper class for handling Zendesk notification deep linking
 */
object ZendeskNotificationHelper {

    /**
     * Creates a deep link intent for a Zendesk ticket notification
     *
     * @param context The application context
     * @param requestId The Zendesk ticket/request ID
     * @return An intent that can be used for deep linking to the ticket
     */
    fun createDeepLinkIntent(context: Context, requestId: String): Intent {
        // Create a back stack to preserve navigation flow
        val backStackIntents = ArrayList<Intent>()

        // Add the ticket list as the back stack
        val ticketListIntent = RequestListActivity.builder().intent(context)
        backStackIntents.add(ticketListIntent)

        // Create the deep link intent using Zendesk's RequestConfiguration
        return zendesk.support.request.RequestConfiguration.Builder()
            .withRequestId(requestId)
            .deepLinkIntent(context, backStackIntents)
    }

    /**
     * Opens a Zendesk ticket directly in the app
     *
     * @param context The application context
     * @param requestId The Zendesk ticket/request ID
     */
    fun openTicket(context: Context, requestId: String) {
        val intent = createDeepLinkIntent(context, requestId)
        context.sendBroadcast(intent)
    }

    /**
     * Checks if a given intent contains Zendesk notification data
     *
     * @param intent The intent to check
     * @return true if the intent contains Zendesk notification data
     */
    fun isZendeskNotification(intent: Intent): Boolean {
        return intent.hasExtra("zendesk_sdk_request_id")
    }

    /**
     * Extracts the request ID from a Zendesk notification intent
     *
     * @param intent The intent containing Zendesk notification data
     * @return The request ID, or null if not found
     */
    fun getRequestIdFromIntent(intent: Intent): String? {
        return intent.getStringExtra("zendesk_sdk_request_id")
    }
}

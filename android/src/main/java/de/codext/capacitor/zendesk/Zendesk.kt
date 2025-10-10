package de.codext.capacitor.zendesk;


import android.content.Context
import com.zendesk.logger.Logger
import zendesk.core.Zendesk
import zendesk.support.Support
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.JwtIdentity
import zendesk.support.guide.HelpCenterConfiguration
import zendesk.support.guide.HelpCenterActivity
import zendesk.support.guide.ViewArticleActivity
import zendesk.support.request.RequestConfiguration
import zendesk.support.request.RequestActivity
import zendesk.support.CustomField
import zendesk.support.requestlist.RequestListActivity
import zendesk.core.PushRegistrationProvider
import java.util.ArrayList

public class Zendesk {

   // initialize zendesk support sdk
    fun initialize(context: Context?, url: String?, appId: String?, clientId: String?, debugLog: Boolean) {
        Zendesk.INSTANCE.init(context!!, url!!, appId!!, clientId)
        if (debugLog) Logger.setLoggable(true)
        Support.INSTANCE.init(Zendesk.INSTANCE)
    }

    fun setAnonymousIdentity(name: String?, email: String?) {
        val identity = AnonymousIdentity.Builder()
                .withNameIdentifier(name)
                .withEmailIdentifier(email)
                .build()
        Zendesk.INSTANCE.setIdentity(identity)
    }

    fun setIdentity(token: String?) {
        val identity: Identity = JwtIdentity(token)
        Zendesk.INSTANCE.setIdentity(identity)
    }

    fun showHelpCenter(context: Context?, groupBy: String, groupIds: List<Long?>, labels: List<String?>) {
//        Show Help Center Builder
        var builder = HelpCenterActivity.builder()
        if ("category" == groupBy && groupIds.isNotEmpty()) {
//            Filter articles by category
            builder = builder.withArticlesForCategoryIds(groupIds)
        } else if ("section" == groupBy && groupIds.isNotEmpty()) {
//            Filter articles by section
            builder = builder.withArticlesForSectionIds(groupIds)
        }
        //        Filter articles by label
        if (labels.isNotEmpty()) {
            builder = builder.withLabelNames(labels)
        }
        builder.show(context!!)
    }

    fun showHelpCenterArticle(context: Context?, articleId: String) {
//        Show a single article Builder
        ViewArticleActivity.builder(articleId.toLong()).show(context!!)
    }

    fun showTicketRequest(context: Context?, subject: String, tags: List<String?>, fields: List<String>) {
        var builder = RequestActivity.builder()
        if (subject.isNotEmpty()) {
            builder = builder.withRequestSubject(subject)
        }
        if (tags.isNotEmpty()) {
            builder = builder.withTags(tags)
        }
        if (fields.isNotEmpty()) {
            val mappedFields: ArrayList<CustomField?> = ArrayList()
            for (field in fields) {
                val fieldParts = field.split("\\|".toRegex()).toTypedArray()
                if (fieldParts.size == 2) {
                    val fieldId = fieldParts[0].toLong()
                    val fieldValue = fieldParts[1]
                    val customField = CustomField(fieldId, fieldValue)
                    mappedFields.add(customField)
                }
            }
            builder = builder.withCustomFields(mappedFields)
        }
        builder.show(context!!)
    }

    fun showUserTickets(context: Context?) {
        RequestListActivity.builder().show(context!!)
    }

    fun registerPushNotifications(deviceToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Zendesk.INSTANCE.provider()?.pushRegistrationProvider()?.registerWithDeviceIdentifier(
            deviceToken,
            object : zendesk.core.ZendeskCallback<String>() {
                override fun onSuccess(result: String?) {
                    onSuccess()
                }

                override fun onError(errorResponse: zendesk.core.ErrorResponse?) {
                    onError(errorResponse?.reason ?: "Unknown error during push registration")
                }
            }
        )
    }

    fun unregisterPushNotifications(onSuccess: () -> Unit, onError: (String) -> Unit) {
        Zendesk.INSTANCE.provider()?.pushRegistrationProvider()?.unregisterDevice(
            object : zendesk.core.ZendeskCallback<Void>() {
                override fun onSuccess(result: Void?) {
                    onSuccess()
                }

                override fun onError(errorResponse: zendesk.core.ErrorResponse?) {
                    onError(errorResponse?.reason ?: "Unknown error during push unregistration")
                }
            }
        )
    }
}

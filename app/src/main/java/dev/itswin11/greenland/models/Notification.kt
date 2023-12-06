package dev.itswin11.greenland.models

import app.bsky.notification.ListNotificationsNotification
import app.bsky.notification.ListNotificationsReason
import dev.itswin11.greenland.models.Notification.Content.Liked
import dev.itswin11.greenland.models.Notification.Content.Mentioned
import dev.itswin11.greenland.models.Notification.Content.Quoted
import dev.itswin11.greenland.models.Notification.Content.RepliedTo
import dev.itswin11.greenland.models.Notification.Content.Reposted
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.runtime.ImmutableListSerializer

@Serializable
data class Notifications(
    @Serializable(ImmutableListSerializer::class)
    val list: ImmutableList<Notification>,
    val cursor: String?
)

@Serializable
data class Notification(
    val uri: AtUri,
    val cid: Cid,
    val author: Profile,
    val reason: Reason,
    val reasonSubject: AtUri?,
    val content: Content?,
    val isRead: Boolean,
    val indexedAt: Moment,
) {
    sealed interface Content {
        data class Liked(
            val post: TimelinePost,
        ) : Content

        data class Reposted(
            val post: TimelinePost,
        ) : Content

        data object Followed : Content

        data class Mentioned(
            val post: TimelinePost,
        ) : Content

        data class RepliedTo(
            val post: TimelinePost,
        ) : Content

        data class Quoted(
            val post: TimelinePost,
        ) : Content
    }

    enum class Reason {
        LIKE,
        REPOST,
        FOLLOW,
        MENTION,
        REPLY,
        QUOTE,
    }
}

fun ListNotificationsNotification.toNotification(
    postsByUri: Map<AtUri, TimelinePost>,
): Notification {
    val notificationPost by lazy {
        val postUri = getPostUri()!!
        postsByUri[postUri]
    }

    val (notificationReason, content) = when (reason) {
        ListNotificationsReason.LIKE -> Notification.Reason.LIKE to notificationPost?.let(::Liked)
        ListNotificationsReason.REPOST -> Notification.Reason.REPOST to notificationPost?.let(::Reposted)
        ListNotificationsReason.FOLLOW -> Notification.Reason.FOLLOW to Notification.Content.Followed
        ListNotificationsReason.MENTION -> Notification.Reason.MENTION to notificationPost?.let(::Mentioned)
        ListNotificationsReason.REPLY -> Notification.Reason.REPLY to notificationPost?.let(::RepliedTo)
        ListNotificationsReason.QUOTE -> Notification.Reason.QUOTE to notificationPost?.let(::Quoted)
    }

    return Notification(
        uri = uri,
        cid = cid,
        author = author.toProfile(),
        reason = notificationReason,
        reasonSubject = reasonSubject,
        content = content,
        isRead = isRead,
        indexedAt = Moment(indexedAt),
    )
}

fun ListNotificationsNotification.getPostUri(): AtUri? = when (reason) {
    ListNotificationsReason.LIKE -> reasonSubject
    ListNotificationsReason.REPOST -> reasonSubject
    ListNotificationsReason.MENTION -> uri
    ListNotificationsReason.REPLY -> uri
    ListNotificationsReason.QUOTE -> uri
    ListNotificationsReason.FOLLOW -> null
}
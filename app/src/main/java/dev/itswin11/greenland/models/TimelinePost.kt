package dev.itswin11.greenland.models

import androidx.compose.runtime.Immutable
import app.bsky.feed.FeedViewPost
import app.bsky.feed.Post
import app.bsky.feed.PostView
import dev.itswin11.greenland.util.deserialize
import dev.itswin11.greenland.util.mapImmutable
import dev.itswin11.greenland.util.recordType
import kotlinx.collections.immutable.ImmutableList
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

@Immutable
data class TimelinePost(
    val uri: AtUri,
    val cid: Cid,
    val author: Profile,
    val text: String,
    val textLinks: ImmutableList<TimelinePostLink>,
    val createdAt: Moment,
    val feature: TimelinePostFeature?,
    val replyCount: Long,
    val repostCount: Long,
    val likeCount: Long,
    val indexedAt: Moment,
    val reposted: Boolean,
    val liked: Boolean,
    val labels: ImmutableList<Label>,
    val reply: TimelinePostReply?,
    val reason: TimelinePostReason?,
    val tags: List<String>
)

fun TimelinePost.toLitePost(): LitePost {
    return LitePost(
        text = text,
        links = textLinks,
        createdAt = createdAt,
        embed = feature
    )
}

fun FeedViewPost.toPost(): TimelinePost? {
    return post.toPost(
        reply = reply?.toReply(),
        reason = reason?.toReason(),
    )
}

fun PostView.toPost(): TimelinePost? {
    return toPost(
        reply = null,
        reason = null
    )
}

fun PostView.toPost(
    reply: TimelinePostReply?,
    reason: TimelinePostReason?,
): TimelinePost? {
    if (record.recordType != "app.bsky.feed.post") {
        return null
    }

    val postRecord = Post.serializer().deserialize(record)

    return TimelinePost(
        uri = uri,
        cid = cid,
        author = author.toProfile(),
        text = postRecord.text,
        textLinks = postRecord.facets.mapImmutable { it.toLink() },
        createdAt = Moment(postRecord.createdAt),
        feature = embed?.toFeature(),
        replyCount = replyCount ?: 0,
        repostCount = repostCount ?: 0,
        likeCount = likeCount ?: 0,
        indexedAt = Moment(indexedAt),
        reposted = viewer?.repost != null,
        liked = viewer?.like != null,
        labels = labels.mapImmutable { it.toLabel() },
        reply = reply,
        reason = reason,
        tags = postRecord.tags,
    )
}
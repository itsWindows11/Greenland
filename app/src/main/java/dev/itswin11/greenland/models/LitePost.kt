package dev.itswin11.greenland.models

import app.bsky.feed.Post
import dev.itswin11.greenland.util.mapImmutable
import kotlinx.collections.immutable.ImmutableList

data class LitePost(
    val text: String,
    val links: ImmutableList<TimelinePostLink>,
    val createdAt: Moment,
)

fun Post.toLitePost(): LitePost {
    return LitePost(
        text = text,
        links = facets.mapImmutable { it.toLink() },
        createdAt = Moment(createdAt),
    )
}
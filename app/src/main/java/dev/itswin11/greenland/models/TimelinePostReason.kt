package dev.itswin11.greenland.models

import app.bsky.feed.FeedViewPostReasonUnion

sealed interface TimelinePostReason {
    data class TimelinePostRepost(
        val repostAuthor: Profile,
        val indexedAt: Moment,
    ) : TimelinePostReason
}

fun FeedViewPostReasonUnion.toReason(): TimelinePostReason {
    return when (this) {
        is FeedViewPostReasonUnion.ReasonRepost -> {
            TimelinePostReason.TimelinePostRepost(
                repostAuthor = value.by.toProfile(),
                indexedAt = Moment(value.indexedAt),
            )
        }
    }
}
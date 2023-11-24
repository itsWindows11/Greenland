package dev.itswin11.greenland.models

import androidx.compose.runtime.Immutable
import app.bsky.feed.GeneratorView
import app.bsky.feed.GeneratorViewerState
import app.bsky.richtext.Facet
import kotlinx.collections.immutable.ImmutableList
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

@Immutable
data class FeedGeneratorListing(
    val uri: AtUri,
    val cid: Cid,
    val creator: Profile,
    val displayName: String,
    val description: String? = null,
    val descriptionFacets: ImmutableList<Facet>,
    val avatar: String? = null,
    val likeCount: Long? = null,
    val viewer: GeneratorViewerState? = null,
    val indexedAt: Moment,
)

fun GeneratorView.toFeedGeneratorListing() = FeedGeneratorListing(
    uri = uri,
    cid = cid,
    creator = creator.toProfile(),
    displayName = displayName,
    description = description,
    descriptionFacets = descriptionFacets,
    avatar = avatar,
    likeCount = likeCount,
    viewer = viewer,
    indexedAt = Moment(indexedAt)
)
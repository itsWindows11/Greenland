package dev.itswin11.greenland.models

import app.bsky.embed.ExternalView
import app.bsky.embed.ImagesView
import app.bsky.embed.RecordViewRecordUnion
import app.bsky.embed.RecordWithMediaViewMediaUnion
import app.bsky.feed.Post
import app.bsky.feed.PostViewEmbedUnion
import dev.itswin11.greenland.models.EmbedPost.BlockedEmbedPost
import dev.itswin11.greenland.models.EmbedPost.InvisibleEmbedPost
import dev.itswin11.greenland.models.EmbedPost.VisibleEmbedPost
import dev.itswin11.greenland.models.TimelinePostFeature.ExternalFeature
import dev.itswin11.greenland.models.TimelinePostFeature.ImagesFeature
import dev.itswin11.greenland.models.TimelinePostFeature.MediaPostFeature
import dev.itswin11.greenland.models.TimelinePostFeature.PostFeature
import dev.itswin11.greenland.util.deserialize
import dev.itswin11.greenland.util.mapImmutable
import dev.itswin11.greenland.util.recordType
import kotlinx.collections.immutable.ImmutableList
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Uri

sealed interface TimelinePostFeature {
    data class ImagesFeature(
        val images: ImmutableList<EmbedImage>,
    ) : TimelinePostFeature, TimelinePostMedia

    data class ExternalFeature(
        val uri: Uri,
        val title: String,
        val description: String,
        val thumb: String?,
    ) : TimelinePostFeature, TimelinePostMedia

    data class PostFeature(
        val post: EmbedPost,
    ) : TimelinePostFeature

    data class MediaPostFeature(
        val post: EmbedPost,
        val media: TimelinePostMedia,
    ) : TimelinePostFeature
}

sealed interface TimelinePostMedia

data class EmbedImage(
    val thumb: String,
    val fullsize: String,
    val alt: String,
)

sealed interface EmbedPost {
    data class VisibleEmbedPost(
        val uri: AtUri,
        val cid: Cid,
        val author: Profile,
        val litePost: LitePost,
    ) : EmbedPost {
        val reference: Reference = Reference(uri, cid)
    }

    data class InvisibleEmbedPost(
        val uri: AtUri,
    ) : EmbedPost

    data class BlockedEmbedPost(
        val uri: AtUri,
    ) : EmbedPost
}

fun PostViewEmbedUnion.toFeature(): TimelinePostFeature {
    return when (this) {
        is PostViewEmbedUnion.ImagesView -> {
            value.toImagesFeature()
        }
        is PostViewEmbedUnion.ExternalView -> {
            value.toExternalFeature()
        }
        is PostViewEmbedUnion.RecordView -> {
            PostFeature(
                post = value.record.toEmbedPost(),
            )
        }
        is PostViewEmbedUnion.RecordWithMediaView -> {
            MediaPostFeature(
                post = value.record.record.toEmbedPost(),
                media = when (val media = value.media) {
                    is RecordWithMediaViewMediaUnion.ExternalView -> media.value.toExternalFeature()
                    is RecordWithMediaViewMediaUnion.ImagesView -> media.value.toImagesFeature()
                },
            )
        }
    }
}

private fun ImagesView.toImagesFeature(): ImagesFeature {
    return ImagesFeature(
        images = images.mapImmutable {
            EmbedImage(
                thumb = it.thumb,
                fullsize = it.fullsize,
                alt = it.alt,
            )
        }
    )
}

private fun ExternalView.toExternalFeature(): ExternalFeature {
    return ExternalFeature(
        uri = external.uri,
        title = external.title,
        description = external.description,
        thumb = external.thumb,
    )
}

private fun RecordViewRecordUnion.toEmbedPost(): EmbedPost {
    return when (this) {
        is RecordViewRecordUnion.ViewBlocked -> {
            BlockedEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.ViewNotFound -> {
            InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.ViewRecord -> {
            if (value.value.recordType != "app.bsky.embed.record#viewRecord") {
                // Not a valid record type we should support.
                return InvisibleEmbedPost(
                    uri = value.uri,
                )
            }

            val litePost = Post.serializer().deserialize(value.value).toLitePost()

            VisibleEmbedPost(
                uri = value.uri,
                cid = value.cid,
                author = value.author.toProfile(),
                litePost = litePost,
            )
        }
        is RecordViewRecordUnion.FeedGeneratorView -> {
            // TODO: support generator views.
            InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.GraphListView -> {
            // TODO: support graph list views.
            InvisibleEmbedPost(
                uri = value.uri,
            )
        }
    }
}
package dev.itswin11.greenland.models

import app.bsky.embed.External
import app.bsky.embed.Images
import app.bsky.embed.RecordViewRecord
import app.bsky.embed.RecordWithMediaMediaUnion
import app.bsky.feed.Post
import app.bsky.feed.PostEmbedUnion
import dev.itswin11.greenland.util.deserialize
import dev.itswin11.greenland.util.mapImmutable
import dev.itswin11.greenland.util.toFeedImageUri
import kotlinx.collections.immutable.ImmutableList

data class LitePost(
    val text: String,
    val links: ImmutableList<TimelinePostLink>,
    val createdAt: Moment,
    val embed: TimelinePostFeature?
)

fun RecordViewRecord.toLitePost(): LitePost {
    val post = Post.serializer().deserialize(value)

    return LitePost(
        text = post.text,
        links = post.facets.mapImmutable { it.toLink() },
        createdAt = Moment(post.createdAt),
        embed = post.embed?.toFeature(author.did.did)
    )
}

fun Post.toLitePost(did: String): LitePost {
    return LitePost(
        text = text,
        links = facets.mapImmutable { it.toLink() },
        createdAt = Moment(createdAt),
        embed = embed?.toFeature(did)
    )
}

fun PostEmbedUnion.toFeature(did: String): TimelinePostFeature? {
    return when (this) {
        is PostEmbedUnion.Images -> {
            value.toImagesFeature(did)
        }
        is PostEmbedUnion.External -> {
            value.toExternalFeature(did)
        }
        is PostEmbedUnion.RecordWithMedia -> {
            TimelinePostFeature.MediaPostFeatureWithoutEmbedPost(
                media = when (val media = value.media) {
                    is RecordWithMediaMediaUnion.External -> media.value.toExternalFeature(did)
                    is RecordWithMediaMediaUnion.Images -> media.value.toImagesFeature(did)
                },
            )
        }
        else -> null
    }
}

private fun Images.toImagesFeature(did: String): TimelinePostFeature.ImagesFeature {
    return TimelinePostFeature.ImagesFeature(
        images = images.mapImmutable {
            var image = EmbedImage(
                thumb = it.image.toFeedImageUri(did, true),
                fullsize = it.image.toFeedImageUri(did),
                alt = it.alt
            )

            it.aspectRatio?.let { ratio ->
                image = image.copy(
                    aspectRatio = AspectRatio(
                        width = ratio.width,
                        height = ratio.height
                    )
                )
            }

            image
        }
    )
}

private fun External.toExternalFeature(did: String): TimelinePostFeature.ExternalFeature {
    return TimelinePostFeature.ExternalFeature(
        uri = external.uri,
        title = external.title,
        description = external.description,
        thumb = external.thumb?.toFeedImageUri(did),
    )
}
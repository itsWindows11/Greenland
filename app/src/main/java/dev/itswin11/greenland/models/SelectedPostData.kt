package dev.itswin11.greenland.models

import androidx.compose.runtime.Immutable
import sh.christian.ozone.api.AtUri

@Immutable
data class SelectedPostData(
    val text: String,
    val isLiked: Boolean,
    val isReposted: Boolean,
    val author: Profile? = null,
    val selectedImageIndex: Int? = null,
    val uri: AtUri? = null,
    val imagesFeature: TimelinePostFeature.ImagesFeature? = null,
)

fun TimelinePost.toSelectedPostData(selectedImageIndex: Int? = null): SelectedPostData {
    val imagesFeature = if (feature is TimelinePostFeature.ImagesFeature) {
        feature
    } else if (feature is TimelinePostFeature.MediaPostFeature && feature.media is TimelinePostFeature.ImagesFeature) {
        feature.media
    } else {
        null
    }

    return SelectedPostData(
        text,
        liked,
        reposted,
        author,
        selectedImageIndex,
        uri,
        imagesFeature
    )
}

fun EmbedPost.VisibleEmbedPost.toSelectedPostData(selectedImageIndex: Int? = null): SelectedPostData {
    return SelectedPostData(
        litePost.text,
        false,
        false,
        author,
        selectedImageIndex,
        uri
    )
}

fun LitePost.toSelectedPostData(uri: AtUri, selectedImageIndex: Int? = null): SelectedPostData {
    return SelectedPostData(
        text,
        false,
        false,
        null,
        selectedImageIndex,
        uri
    )
}
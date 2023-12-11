package dev.itswin11.greenland.util

import sh.christian.ozone.api.model.Blob

fun Blob.toFeedImageUri(did: String, thumb: Boolean = false): String {
    val builder = StringBuilder("https://cdn.bsky.app/img/")

    if (thumb) {
        builder.append("feed_thumbnail/")
    } else {
        builder.append("feed_fullsize/")
    }

    builder.append("plain/")

    builder.append(did)
    builder.append("/")

    if (this is Blob.StandardBlob) {
        builder.append(ref.link)
    } else if (this is Blob.LegacyBlob) {
        builder.append(cid)
    }

    builder.append("@jpeg")
    return builder.toString()
}
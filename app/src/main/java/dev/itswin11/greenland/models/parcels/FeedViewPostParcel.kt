package dev.itswin11.greenland.models.parcels

import android.os.Parcelable
import dev.itswin11.greenland.models.BskyFeedViewPost
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class FeedViewPostsParcel(var posts: @RawValue List<BskyFeedViewPost>? = null) : Parcelable
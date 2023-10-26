package dev.itswin11.greenland.models.parcels

import android.os.Parcelable
import dev.itswin11.greenland.models.BskyFeedGeneratorView
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ExploreFeedViewsParcel(var list: @RawValue List<BskyFeedGeneratorView>? = null) : Parcelable
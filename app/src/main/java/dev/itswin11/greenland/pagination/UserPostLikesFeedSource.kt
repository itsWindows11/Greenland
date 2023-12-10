package dev.itswin11.greenland.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.bsky.feed.GetActorLikesQueryParams
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.toPost
import kotlinx.collections.immutable.toImmutableList
import sh.christian.ozone.api.AtIdentifier

class UserPostLikesFeedSource(private val getUserIdentifier: () -> AtIdentifier): PagingSource<String, TimelinePost>() {
    private val postUris: HashSet<String> = HashSet()

    override fun getRefreshKey(state: PagingState<String, TimelinePost>) =
        if (state.anchorPosition == null) null else state.closestPageToPosition(state.anchorPosition!!)?.prevKey

    override suspend fun load(params: LoadParams<String>): LoadResult<String, TimelinePost> {
        postUris.clear()

        return try {
            val response = App.atProtoClient.getActorLikes(
                GetActorLikesQueryParams(
                    actor = getUserIdentifier(),
                    limit = (params.loadSize / 3).toLong(),
                    cursor = params.key,
                )
            ).requireResponse()

            LoadResult.Page(
                data = response.feed
                    .filter { !(!postUris.add(it.post.uri.atUri) && it.reply?.parent == null) }
                    .mapNotNull { it.toPost() }
                    .toImmutableList(),
                prevKey = null,
                nextKey = response.cursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
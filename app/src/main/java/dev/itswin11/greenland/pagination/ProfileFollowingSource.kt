package dev.itswin11.greenland.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.bsky.graph.GetFollowersQueryParams
import app.bsky.graph.GetFollowsQueryParams
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.FollowingResponse
import dev.itswin11.greenland.models.Profile
import dev.itswin11.greenland.models.toFollowingResponse
import sh.christian.ozone.api.AtIdentifier

class ProfileFollowingSource(
    private val getActorIdentifier: () -> AtIdentifier,
    private val getIsFollowing: () -> Boolean
): PagingSource<String, Profile>() {
    override fun getRefreshKey(state: PagingState<String, Profile>) =
        if (state.anchorPosition == null) null else state.closestPageToPosition(state.anchorPosition!!)?.prevKey

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Profile> {
        return try {
            val response = getFollowing(params.key)

            LoadResult.Page(
                data = response.follows,
                prevKey = null,
                nextKey = response.cursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getFollowing(cursor: String?): FollowingResponse {
        if (getIsFollowing()) {
            return App.atProtoClient.getFollows(
                GetFollowsQueryParams(
                    limit = 100,
                    actor = getActorIdentifier(),
                    cursor = cursor
                )
            )
                .requireResponse()
                .toFollowingResponse()
        }

        return App.atProtoClient.getFollowers(
            GetFollowersQueryParams(
                limit = 100,
                actor = getActorIdentifier(),
                cursor = cursor
            )
        )
            .requireResponse()
            .toFollowingResponse()
    }
}
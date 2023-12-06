package dev.itswin11.greenland.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.bsky.feed.GetPostsQueryParams
import app.bsky.notification.ListNotificationsQueryParams
import app.bsky.notification.ListNotificationsResponse
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.Notification
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.getPostUri
import dev.itswin11.greenland.models.toNotification
import dev.itswin11.greenland.models.toPost
import kotlinx.collections.immutable.toImmutableList

class UserNotificationsSource: PagingSource<String, Notification>() {
    override fun getRefreshKey(state: PagingState<String, Notification>) =
        if (state.anchorPosition == null) null else state.closestPageToPosition(state.anchorPosition!!)?.prevKey

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Notification> {
        return try {
            val response = App.atProtoClient.listNotifications(
                ListNotificationsQueryParams(limit = (params.loadSize / 3).toLong(), cursor = params.key)
            ).requireResponse()

            val posts = fetchPosts(response).associateBy { it.uri }

            LoadResult.Page(
                data = response.notifications
                    .map { it.toNotification(posts) }
                    .toImmutableList(),
                prevKey = null,
                nextKey = response.cursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchPosts(response: ListNotificationsResponse): List<TimelinePost> {
        val postUris = response.notifications
            .mapNotNull { it.getPostUri() }
            .distinct()
            .toImmutableList()

        return if (postUris.isEmpty()) {
            emptyList()
        } else {
            App.atProtoClient
                .getPosts(GetPostsQueryParams(postUris))
                .requireResponse()
                .posts
                .mapNotNull { it.toPost() }
        }
    }
}
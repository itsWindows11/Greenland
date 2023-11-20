package dev.itswin11.greenland.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import app.bsky.feed.FeedViewPost
import app.bsky.feed.Post
import app.bsky.feed.ReplyRefParentUnion
import dev.itswin11.greenland.App
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.decodeFromJsonElement

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostsList(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    posts: List<FeedViewPost>,
    pullRefreshState: PullRefreshState,
    refreshing: () -> Boolean,
    titleBarConnection: NestedScrollConnection,
    fabConnection: NestedScrollConnection
) {
    val recordList = remember { getValidRecordsFromFeedViewPosts(posts) }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(titleBarConnection)
                .nestedScroll(fabConnection),
            state = scrollState
        ) {
            items(posts.size) { index ->
                val post = remember { posts[index] }

                Column {
                    if (post.reply?.parent != null && post.reply.parent is ReplyRefParentUnion.PostView) {
                        PostView(post.reply.parent.value, recordList[index], hasThreadChild = true)
                    }

                    PostView(post.post, recordList[index], isThreadChild = post.reply?.parent != null)
                }

                Divider()
            }
        }

        PullRefreshIndicator(
            refreshing(),
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scale = true
        )
    }
}

fun getValidRecordsFromFeedViewPosts(posts: List<FeedViewPost>): ImmutableList<Post> {
    val records = mutableListOf<Post>()

    for (feedViewPost in posts) {
        try {
            val record = App.jsonSerializer.decodeFromJsonElement<Post>(feedViewPost.post.record)
            records.add(record)
        } catch (_: SerializationException) {

        } catch (_: IllegalArgumentException) {

        }
    }

    return records.toImmutableList()
}

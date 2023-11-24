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
import dev.itswin11.greenland.models.TimelinePost
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostsList(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    posts: ImmutableList<TimelinePost>,
    pullRefreshState: PullRefreshState,
    refreshing: () -> Boolean,
    titleBarConnection: NestedScrollConnection,
    fabConnection: NestedScrollConnection
) {
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
                    if (post.reply?.parent != null) {
                        PostView(post.reply.parent, hasThreadChild = true)
                    }

                    PostView(post, isThreadChild = post.reply?.parent != null)
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
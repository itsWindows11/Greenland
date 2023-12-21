package dev.itswin11.greenland.views

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.enums.PostAction
import dev.itswin11.greenland.models.SelectedPostData
import dev.itswin11.greenland.models.TimelinePost
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostsList(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    posts: Flow<PagingData<TimelinePost>>,
    titleBarConnection: NestedScrollConnection,
    fabConnection: NestedScrollConnection,
    onInteraction: (action: PostAction, postData: SelectedPostData) -> Unit
) {
    val postsList = posts.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = postsList.loadState.refresh is LoadState.Loading,
        onRefresh = {
            postsList.refresh()
        },
        refreshThreshold = 50.dp,
        refreshingOffset = 60.dp
    )

    Box(modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .nestedScroll(titleBarConnection)
                .nestedScroll(fabConnection),
            state = scrollState
        ) {
            items(postsList.itemCount) { index ->
                val post = remember { postsList[index] }

                post?.let {
                    FeedPostSlice(it, onInteraction = onInteraction)
                    Divider()
                }
            }

            if (postsList.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)) {
                        CircularProgressIndicator(
                            Modifier
                                .width(36.dp)
                                .align(Alignment.Center))
                    }
                }
            }
        }

        PullRefreshIndicator(
            postsList.loadState.refresh is LoadState.Loading,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scale = true
        )

        if (postsList.loadState.refresh is LoadState.Error || postsList.loadState.append is LoadState.Error) {
            Toast.makeText(
                LocalContext.current,
                "An error has occurred while loading posts.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
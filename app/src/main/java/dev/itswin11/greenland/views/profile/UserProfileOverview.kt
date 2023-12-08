package dev.itswin11.greenland.views.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.enums.UserProfileOverviewTabType
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.viewmodels.ProfileViewModel
import dev.itswin11.greenland.views.FeedPostSlice

@Composable
fun UserProfileOverview(
    pageType: UserProfileOverviewTabType,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    when (pageType) {
        UserProfileOverviewTabType.FEEDS -> UserProfileOverviewFeeds(modifier, viewModel)
        UserProfileOverviewTabType.LISTS -> UserProfileOverviewLists(modifier, viewModel)
        else -> UserProfileOverviewPosts(pageType, modifier, viewModel)
    }
}

@Composable
private fun UserProfileOverviewPosts(
    pageType: UserProfileOverviewTabType,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val posts: LazyPagingItems<TimelinePost> = when (pageType) {
        UserProfileOverviewTabType.REPLIES -> viewModel.postsWithReplies
        UserProfileOverviewTabType.MEDIA -> viewModel.media
        UserProfileOverviewTabType.LIKES -> viewModel.likes
        UserProfileOverviewTabType.POSTS -> viewModel.posts
        // In the case we get anything else other than the
        // values above this might be an unexpected
        // occurrence.
        else -> throw Exception("Unexpected UserProfileOverviewTabType value.")
    }.collectAsLazyPagingItems()

    val firstTimeLoaded = remember { mutableStateOf(false) }

    // TODO: media grid instead of list
    if (posts.loadState.refresh is LoadState.Loading && !firstTimeLoaded.value) {
        firstTimeLoaded.value = true

        Box(modifier.padding(top = 24.dp)) {
            CircularProgressIndicator(
                Modifier
                    .width(36.dp)
                    .align(Alignment.TopCenter))
        }
    } else {
        LazyColumn(modifier) {
            items(posts.itemCount) { index ->
                val post = posts[index]

                if (post != null) {
                    if (pageType == UserProfileOverviewTabType.POSTS && post.reply != null
                        || pageType == UserProfileOverviewTabType.MEDIA && post.feature == null)
                        return@items

                    FeedPostSlice(post, pageType == UserProfileOverviewTabType.REPLIES)
                    Divider()
                }
            }

            if (posts.loadState.append is LoadState.Loading) {
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
    }
}

@Composable
private fun UserProfileOverviewFeeds(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    Text(text = "If you have come across this page, then this is currently a placeholder for the feeds page.")
}

@Composable
private fun UserProfileOverviewLists(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    Text(text = "If you have come across this page, then this is currently a placeholder for the lists page.")
}
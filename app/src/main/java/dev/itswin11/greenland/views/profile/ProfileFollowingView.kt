package dev.itswin11.greenland.views.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.models.Profile
import dev.itswin11.greenland.viewmodels.ProfileFollowingViewModel
import sh.christian.ozone.api.AtIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFollowingView(identifier: AtIdentifier, isFollowingPage: Boolean = false, onBackRequested: () -> Unit, viewModel: ProfileFollowingViewModel = viewModel()) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val follows = viewModel.follows.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.profile.value = identifier
        viewModel.isFollowingPage.value = isFollowingPage
    }

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(if (isFollowingPage) "Following" else "Followers", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackRequested) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )

            if (follows.loadState.refresh == LoadState.Loading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            } else {
                ProfileFollowingList(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    follows,
                    shouldShowFollowLabel = isFollowingPage
                )
            }
        }
    }

    BackHandler {
        onBackRequested()
    }
}

@Composable
private fun ProfileFollowingList(modifier: Modifier = Modifier, follows: LazyPagingItems<Profile>, shouldShowFollowLabel: Boolean = false) {
    LazyColumn(modifier) {
        items(follows.itemCount) {
            val followItem = follows[it]

            followItem?.let { profile ->
                ProfileFollowingItem(profile, shouldShowFollowLabel = shouldShowFollowLabel)
            }
        }

        if (follows.loadState.append is LoadState.Loading) {
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
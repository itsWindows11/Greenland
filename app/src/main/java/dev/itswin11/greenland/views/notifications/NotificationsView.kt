package dev.itswin11.greenland.views.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.util.conditional
import dev.itswin11.greenland.viewmodels.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NotificationsView(viewModel: NotificationsViewModel = viewModel()) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    val topAppBarState = rememberTopAppBarState()
    val pinnedScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    val pullRefreshState = rememberPullRefreshState(
        refreshing = notifications.loadState.refresh is LoadState.Loading,
        onRefresh = {
            notifications.refresh()
        },
        refreshThreshold = 50.dp,
        refreshingOffset = 60.dp
    )

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            },
            scrollBehavior = pinnedScrollBehavior
        )

        Box(Modifier.weight(1f).pullRefresh(pullRefreshState)) {
            LazyColumn(Modifier.nestedScroll(pinnedScrollBehavior.nestedScrollConnection)) {
                items(notifications.itemCount) {
                    val notification = notifications[it]

                    if (notification != null) {
                        NotificationItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .conditional(
                                    !notification.isRead,
                                    Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                ),
                            notification = notification,
                            onClick = {}
                        )

                        Divider()
                    }
                }

                if (notifications.loadState.append is LoadState.Loading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                            CircularProgressIndicator(Modifier.width(36.dp).align(Alignment.Center))
                        }
                    }
                }
            }

            PullRefreshIndicator(
                notifications.loadState.refresh is LoadState.Loading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                scale = true
            )
        }
    }
}
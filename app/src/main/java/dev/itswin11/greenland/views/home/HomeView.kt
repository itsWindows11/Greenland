package dev.itswin11.greenland.views.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyFeedViewPost
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.views.PostsList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeView(modifier: Modifier = Modifier) {
    val posts = remember { mutableStateOf<List<BskyFeedViewPost>?>(null) }

    LaunchedEffect(Unit) {
        posts.value = App.atProtoClient.getHomeTimeline(
            "bsky.social",
            BskyGetTimelineInput(limit = 100)
        ).feed
    }

    val refreshing = remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    val coroutineScope = rememberCoroutineScope()

    val isFabVisible = rememberSaveable { mutableStateOf(true) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing.value,
        onRefresh = {
            coroutineScope.launch {
                refreshing.value = true
                posts.value = App.atProtoClient.getHomeTimeline(
                    "bsky.social",
                    BskyGetTimelineInput(limit = 100)
                ).feed
                refreshing.value = false

                scrollState.animateScrollToItem(0, 0)
            }
        },
        refreshThreshold = 50.dp,
        refreshingOffset = 60.dp
    )

    val fabNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -2) {
                    isFabVisible.value = false
                }

                if (available.y > 6) {
                    isFabVisible.value = true
                }

                return Offset.Zero
            }
        }
    }

    val pinnedScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Box(modifier) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(0.dp, Color.Transparent),
                        contentPadding = PaddingValues(8.dp, 0.dp),
                    ) {
                        Text("Home", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = "",
                            modifier = Modifier.rotate(90f).offset(2.dp, (-4).dp)
                        )
                    }
                },
                scrollBehavior = pinnedScrollBehavior
            )

            if (posts.value != null) {
                PostsList(
                    Modifier
                        .weight(1f)
                        .pullRefresh(pullRefreshState)
                        .fillMaxWidth(),
                    scrollState,
                    posts.value!!,
                    pullRefreshState,
                    refreshing,
                    pinnedScrollBehavior.nestedScrollConnection,
                    fabNestedScrollConnection
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.requiredWidth(48.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isFabVisible.value,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(0.dp, 0.dp, 20.dp, 20.dp)
        ) {
            FloatingActionButton(
                onClick = { }
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    }
}
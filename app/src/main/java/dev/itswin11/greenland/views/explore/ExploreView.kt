package dev.itswin11.greenland.views.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import dev.itswin11.greenland.viewmodels.ExploreViewModel
import dev.itswin11.greenland.views.FeedCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExploreView(modifier: Modifier = Modifier, viewModel: ExploreViewModel = viewModel()) {
    val feeds = viewModel.suggestedFeeds.collectAsStateWithLifecycle()
    val suggestedFollows = viewModel.suggestedFollows.collectAsStateWithLifecycle()

    val refreshing = viewModel.refreshing.collectAsStateWithLifecycle()
    val initiallyLoaded = viewModel.initiallyLoaded.collectAsStateWithLifecycle()

    val topAppBarState = rememberTopAppBarState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing.value,
        onRefresh = {
            viewModel.loadData(true)
        },
        refreshThreshold = 50.dp,
        refreshingOffset = 60.dp
    )

    val pinnedScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Surface(modifier.fillMaxSize()) {
        if (initiallyLoaded.value) {
            Column(Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Explore", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    },
                    scrollBehavior = pinnedScrollBehavior
                )

                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)) {
                    LazyColumn(Modifier.nestedScroll(pinnedScrollBehavior.nestedScrollConnection)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                onClick = {},
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Row(Modifier.padding(16.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Feeds for you",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
                                    )

                                    Spacer(Modifier.weight(1f))

                                    Icon(
                                        Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        Modifier.size(32.dp)
                                    )
                                }
                            }

                            if (feeds.value != null) {
                                Column(Modifier.padding(4.dp, 0.dp)) {
                                    feeds.value!!.forEach { feed ->
                                        FeedCard(Modifier.padding(4.dp, 2.dp), feed)
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Divider()

                            Spacer(Modifier.height(4.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                onClick = {},
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Text(
                                    "Suggested follows",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(20.dp, 8.dp)
                                )
                            }
                        }

                        items(suggestedFollows.value?.size ?: 0) { index ->
                            ExploreSuggestedFollowItem(
                                modifier = Modifier.padding(8.dp, 4.dp),
                                profileView = suggestedFollows.value!![index]
                            )
                        }
                    }

                    PullRefreshIndicator(
                        refreshing.value,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        scale = true
                    )
                }
            }
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp, 0.dp, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.requiredWidth(48.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ExploreViewPreview() {
    GreenlandTheme {
        ExploreView(Modifier.fillMaxSize())
    }
}
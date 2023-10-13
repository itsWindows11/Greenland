package dev.itswin11.greenland.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.App
import dev.itswin11.greenland.R
import dev.itswin11.greenland.models.BskyFeedViewPost
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyPost
import dev.itswin11.greenland.models.BskyPostRecord
import dev.itswin11.greenland.models.BskyProfileViewBasic
import dev.itswin11.greenland.models.navigation.BottomNavigationItem
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val posts = remember { mutableStateOf<List<BskyFeedViewPost>?>(null) }

            LaunchedEffect(Unit) {
                posts.value = App.atProtoClient.getHomeTimeline(
                    "bsky.social",
                    BskyGetTimelineInput(limit = 100)
                ).feed
            }

            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var refreshing by remember { mutableStateOf(false) }
                    val appBarState = rememberTopAppBarState()
                    val scrollState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()

                    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(appBarState)

                    var navigationSelectedItem by remember { mutableIntStateOf(0) }

                    /*val bottomBarState = remember { mutableStateOf(true) }

                    val bottomBarHeight = 84.dp
                    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
                    val bottomBarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

                    val topBarState = remember { mutableStateOf(true) }

                    val topBarHeight = 64.dp
                    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.roundToPx().toFloat() }
                    val topBarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

                    val nestedScrollConnection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                val delta = available.y
                                val newOffset = bottomBarOffsetHeightPx.floatValue + delta

                                bottomBarOffsetHeightPx.floatValue = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                                bottomBarState.value = bottomBarOffsetHeightPx.floatValue < bottomBarHeightPx

                                val newTopOffset = topBarOffsetHeightPx.floatValue + delta

                                topBarOffsetHeightPx.floatValue = newTopOffset.coerceIn(-topBarHeightPx, 0f)
                                topBarState.value = topBarOffsetHeightPx.floatValue < topBarHeightPx

                                return Offset.Zero
                            }
                        }
                    }*/

                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = refreshing,
                        onRefresh = {
                            coroutineScope.launch {
                                refreshing = true
                                posts.value = App.atProtoClient.getHomeTimeline(
                                    "bsky.social",
                                    BskyGetTimelineInput(limit = 100)
                                ).feed
                                refreshing = false

                                scrollState.animateScrollToItem(0, 0)
                            }
                        },
                        refreshThreshold = 50.dp,
                        refreshingOffset = 60.dp
                    )

                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            //Column(modifier = Modifier.offset { IntOffset(x = 0, y = topBarOffsetHeightPx.floatValue.roundToInt()) }) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        "Greenland",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                scrollBehavior = scrollBehavior
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                BottomNavigationItem.items.forEachIndexed { index, navigationItem ->
                                    NavigationBarItem(
                                        selected = index == navigationSelectedItem,
                                        label = {
                                            Text(
                                                navigationItem.label,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                navigationItem.icon,
                                                contentDescription = navigationItem.label
                                            )
                                        },
                                        onClick = {
                                            navigationSelectedItem = index
                                        }
                                    )
                                }
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = { }) {
                                Icon(Icons.Rounded.Add, contentDescription = "Add")
                            }
                        }
                    ) {
                        if (posts.value != null) {
                            Box(
                                modifier = Modifier
                                    .padding(it)
                                    .pullRefresh(pullRefreshState),
                                contentAlignment = Alignment.Center
                            ) {
                                PostsList(scrollState, posts.value!!)
                                PullRefreshIndicator(
                                    refreshing,
                                    pullRefreshState,
                                    Modifier.align(Alignment.TopCenter),
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    scale = true
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
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
            }
        }
    }
}

@Composable
fun PostsList(scrollState: LazyListState, posts: List<BskyFeedViewPost>) {
    LazyColumn(state = scrollState) {
        items(posts.size) { index ->
            PostView(posts[index].post)
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView(post: BskyPost, preview: Boolean = false) {
    val displayName = remember { post.author.displayName ?: post.author.handle }
    val dateInMillis = remember {
        if (preview) {
            return@remember 0L
        }

        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            .parse(post.record.createdAt)
            ?.time ?: 0
    }

    val timeAgoString = remember { timeAgo(dateInMillis) }

    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            onClick = {},
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 0.dp)) {
                AsyncImage(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.author.avatar)
                        .crossfade(500)
                        .build(),
                    contentDescription = "Profile picture of $displayName"
                )

                Column(
                    modifier = Modifier
                        .padding(12.dp, 0.dp, 0.dp, 0.dp)
                        .weight(1f)
                ) {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f, false)
                                        .padding(0.dp, 0.dp, 4.dp, 0.dp),
                                    text = displayName,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "• $timeAgoString",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (post.author.displayName != null)
                                Text(
                                    "@${post.author.handle}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                        }

                        OutlinedButton(
                            modifier = Modifier.width(48.dp).height(48.dp).offset(4.dp, (-10).dp),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Rounded.MoreHoriz, contentDescription = "More")
                        }
                    }

                    Column(modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)) {
                        Text(post.record.text)
                    }

                    Row(
                        modifier = Modifier.offset((-12).dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(12.dp, 4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                    painter = painterResource(R.drawable.comment_icon),
                                    contentDescription = "${post.replyCount} replies"
                                )
                                Text(post.replyCount.toString())
                            }
                        }

                        OutlinedButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(12.dp, 4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                    painter = painterResource(R.drawable.repost_icon),
                                    contentDescription = "${post.repostCount} reposts"
                                )
                                Text(post.repostCount.toString())
                            }
                        }

                        OutlinedButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(12.dp, 4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                    painter = painterResource(R.drawable.like_icon),
                                    contentDescription = "${post.likeCount} likes"
                                )
                                Text(post.likeCount.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostViewPreview() {
    val sampleRecord = BskyPostRecord(
        "Sample Post Content",
        "app.bsky.embed.record",
        Array(1) { "en" }.toList(),
        "2023-10-11T13:40:03.364Z"
    )

    val sampleProfile = BskyProfileViewBasic(
        "DID",
        "handle",
        "Display Name",
        "https://www.clevelanddentalhc.com/wp-content/uploads/2018/03/sample-avatar-300x300.jpg"
    )

    val bskyPost = BskyPost(
        "",
        "CID sample",
        sampleProfile,
        sampleRecord,
        0,
        0,
        0,
        "5d"
    )

    GreenlandTheme {
        PostView(bskyPost, true)
    }
}

fun timeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time
    val second = 1000L
    val minute = 60 * second
    val hour = 60 * minute
    val day = 24 * hour
    val week = 7 * day
    val month = 4 * week
    val year = 12 * month

    return when {
        diff < minute -> "now"
        diff < 2 * minute -> "m"
        diff < hour -> "${diff / minute}m"
        diff < 2 * hour -> "h"
        diff < day -> "${diff / hour}h"
        diff < 2 * day -> "1d"
        diff < week -> "${diff / day}d"
        diff < 2 * week -> "w"
        diff < month -> "${diff / week}w"
        diff < 2 * month -> "mo"
        diff < year -> "${diff / month}mo"
        else -> "${diff / year}y"
    }
}

/*@Preview(showBackground = true)
@Composable
fun PostsListPreview() {
    GreenlandTheme {
        PostsList()
    }
}*/
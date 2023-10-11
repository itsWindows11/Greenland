package dev.itswin11.greenland.activities.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyFeedViewPost
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.ui.theme.GreenlandTheme

class HomeActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val posts = remember { mutableStateOf<List<BskyFeedViewPost>?>(null) }

            LaunchedEffect(Unit) {
                posts.value = App.atProtoClient.getHomeTimeline("bsky.social", BskyGetTimelineInput(limit = 100)).feed
            }

            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var refreshing by remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(true) {
                        if (refreshing) {
                            posts.value = App.atProtoClient.getHomeTimeline("bsky.social", BskyGetTimelineInput(limit = 100)).feed
                            refreshing = false
                        }
                    }

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
                                posts.value = App.atProtoClient.getHomeTimeline("bsky.social", BskyGetTimelineInput(limit = 100)).feed
                                refreshing = false
                            }
                        }
                    )

                    Scaffold(
                        //modifier = Modifier.nestedScroll(nestedScrollConnection),
                        topBar = {
                            //Column(modifier = Modifier.offset { IntOffset(x = 0, y = topBarOffsetHeightPx.floatValue.roundToInt()) }) {
                            Column {
                                CenterAlignedTopAppBar(
                                    title = { Text("Home", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
                                )

                                Divider()
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                BottomNavigationItem.items.forEachIndexed { index, navigationItem ->
                                    NavigationBarItem(
                                        selected = index == navigationSelectedItem,
                                        label = {
                                            Text(navigationItem.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                        content = {
                            if (posts.value != null) {
                                PostsList(posts.value!!)

                                Box(
                                    modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PostsList(posts.value!!)
                                    PullRefreshIndicator(
                                        refreshing,
                                        pullRefreshState,
                                        Modifier.align(Alignment.TopCenter).border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline
                                        ),
                                        backgroundColor = MaterialTheme.colorScheme.background,
                                        contentColor = MaterialTheme.colorScheme.onBackground,
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
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsList(posts: List<BskyFeedViewPost>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(posts.size, key = { posts[it].post.cid }) { index ->
            PostView(posts[index].post)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView(post: BskyPost) {
    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            onClick = {},
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                AsyncImage(modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(CircleShape), model = post.author.avatar, contentDescription = "")
                Text(post.cid)
                Text(post.indexedAt)
                Text(post.author.handle)
                Text(post.likeCount.toString() + " likes")
                Text(post.repostCount.toString() + " reposts")

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Divider()
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PostsListPreview() {
    GreenlandTheme {
        PostsList()
    }
}*/
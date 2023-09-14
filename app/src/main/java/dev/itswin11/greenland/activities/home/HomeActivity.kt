package dev.itswin11.greenland.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyFeedViewPost
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlin.math.roundToInt

class HomeActivity : ComponentActivity() {
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
                    val bottomBarState = remember { mutableStateOf(true) }

                    val bottomBarHeight = 64.dp
                    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }

                    val bottomBarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

                    val nestedScrollConnection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                val delta = available.y
                                val newOffset = bottomBarOffsetHeightPx.floatValue + delta

                                bottomBarOffsetHeightPx.floatValue = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                                bottomBarState.value = bottomBarOffsetHeightPx.floatValue < bottomBarHeightPx

                                return Offset.Zero
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.nestedScroll(nestedScrollConnection),
                        bottomBar = {
                            BottomAppBar(
                                modifier = Modifier.height(bottomBarHeight).offset { IntOffset(x = 0, y = -bottomBarOffsetHeightPx.floatValue.roundToInt()) } // Apply the offset to the bottom bar as a translation in y direction
                            ) {
                            }
                        },
                        content = { innerPadding ->
                            if (posts.value != null) {
                                PostsList(posts.value!!, innerPadding)
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
fun PostsList(posts: List<BskyFeedViewPost>, contentPadding: PaddingValues) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(posts.size) { index ->
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
                            .clip(CircleShape), model = posts[index].post.author.avatar, contentDescription = "")
                        Text(posts[index].post.cid)
                        Text(posts[index].post.indexedAt)
                        Text(posts[index].post.author.handle)
                        Text(posts[index].post.likeCount.toString() + " likes")
                        Text(posts[index].post.repostCount.toString() + " reposts")

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
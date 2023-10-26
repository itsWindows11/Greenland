package dev.itswin11.greenland.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.parcels.ExploreFeedViewsParcel
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import dev.itswin11.greenland.views.FeedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(modifier: Modifier = Modifier) {
    val initiallyLoaded = rememberSaveable { mutableStateOf(false) }
    val feeds = rememberSaveable { mutableStateOf<ExploreFeedViewsParcel?>(null) }

    LaunchedEffect(Unit) {
        if (initiallyLoaded.value)
            return@LaunchedEffect

        feeds.value = ExploreFeedViewsParcel(App.atProtoClient.getSuggestedFeeds(
            "bsky.social",
            BskyGetSuggestedFeedsInput(6)
        ).feeds.subList(0, 6))

        initiallyLoaded.value = true
    }

    Column(modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text("Explore", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        )

        LazyColumn(modifier.weight(1f)) {
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
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.weight(1f))

                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            Modifier.size(32.dp)
                        )
                    }
                }

                if (feeds.value?.list != null) {
                    Column(Modifier.padding(4.dp, 0.dp)) {
                        feeds.value!!.list!!.forEach { feed ->
                            FeedCard(Modifier.padding(4.dp, 2.dp), feed)
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
                            modifier = Modifier.requiredWidth(32.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Divider()
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
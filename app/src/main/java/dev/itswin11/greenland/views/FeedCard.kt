package dev.itswin11.greenland.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.bsky.actor.ProfileView
import app.bsky.feed.GeneratorView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.models.FeedGeneratorListing
import dev.itswin11.greenland.models.toFeedGeneratorListing
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.datetime.Instant
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedCard(modifier: Modifier = Modifier, feedView: FeedGeneratorListing) {
    // TODO: Navigation to feed view functionality
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {},
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = modifier.padding(8.dp)) {
            AsyncImage(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(feedView.avatar)
                    .crossfade(500)
                    .build(),
                contentDescription = "Avatar of ${feedView.displayName} feed."
            )

            Column(
                Modifier.weight(1f).padding(12.dp, 0.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = feedView.displayName,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "by @${feedView.creator.handle}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedCardPreview() {
    val sampleFeedGeneratorView = GeneratorView(
        AtUri(""),
        Cid(""),
        Did(""),
        ProfileView(Did(""), Handle("@test.bsky.social")),
        "Feed Name",
        "Feed Description",
        indexedAt = Instant.fromEpochSeconds(0)
    ).toFeedGeneratorListing()

    GreenlandTheme {
        FeedCard(feedView = sampleFeedGeneratorView)
    }
}
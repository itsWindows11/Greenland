package dev.itswin11.greenland.views.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.bsky.actor.ProfileView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.atproto.label.Label
import dev.itswin11.greenland.models.LiteProfile
import dev.itswin11.greenland.models.toProfile
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreSuggestedFollowItem(modifier: Modifier = Modifier, profileView: LiteProfile) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {},
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp, 8.dp)) {
            AsyncImage(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileView.avatar!!)
                    .crossfade(500)
                    .build(),
                contentDescription = "Avatar of ${profileView.displayName}."
            )

            Column(Modifier.padding(start = 12.dp, end = 12.dp).weight(1f)) {
                if (profileView.displayName != null) {
                    Text(profileView.displayName, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                }

                Text("@${profileView.handle}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            // TODO: Make this button actually functional.
            Button(onClick = { }, modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(if (profileView.followedByMe) "Unfollow" else "Follow")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreSuggestedFollowItemPreview() {
    val sampleProfile = ProfileView(
        Did("DID"),
        Handle("handle"),
        "Display Name",
        "https://www.clevelanddentalhc.com/wp-content/uploads/2018/03/sample-avatar-300x300.jpg",
        "Description",
        Instant.fromEpochSeconds(0),
        null,
        emptyList<Label>().toImmutableList()
    ).toProfile()

    GreenlandTheme {
        ExploreSuggestedFollowItem(
            modifier = Modifier.width(400.dp),
            profileView = sampleProfile as LiteProfile
        )
    }
}
package dev.itswin11.greenland.views.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.R
import dev.itswin11.greenland.models.LiteProfile
import dev.itswin11.greenland.models.Moment
import dev.itswin11.greenland.models.Notification
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import dev.itswin11.greenland.util.emptyImmutableList
import dev.itswin11.greenland.views.PostView
import kotlinx.datetime.Instant
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

@Composable
fun NotificationItem(modifier: Modifier, notification: Notification, onClick: () -> Unit) {
    val notificationIconResource = when (notification.reason) {
        Notification.Reason.REPOST -> R.drawable.ic_repost
        Notification.Reason.FOLLOW -> R.drawable.ic_follow
        Notification.Reason.LIKE -> R.drawable.ic_like_filled
        else -> 0
    }

    Column(modifier.clickable { onClick() }) {
        if (notification.reason != Notification.Reason.MENTION
            && notification.reason != Notification.Reason.REPLY
            && notification.reason != Notification.Reason.QUOTE) {
            ConstraintLayout(Modifier.fillMaxWidth().padding(24.dp, 12.dp, 12.dp, 12.dp)) {
                val (icon, content) = createRefs()

                Icon(
                    modifier = Modifier
                        .constrainAs(icon) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(content.start, margin = 12.dp)
                            width = Dimension.preferredWrapContent
                        }
                        .size(24.dp),
                    painter = painterResource(notificationIconResource),
                    contentDescription = null,
                    tint = if (notification.reason != Notification.Reason.LIKE) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )

                NotificationContent(
                    Modifier.constrainAs(content) {
                        start.linkTo(icon.end)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                    notification
                )
            }
        } else if (notification.content is Notification.Content.Quoted) {
            PostView(notification.content.post, onPostClick = {/*TODO*/})
        } else if (notification.content is Notification.Content.RepliedTo) {
            PostView(notification.content.post, onPostClick = {/*TODO*/})
        } else if (notification.content is Notification.Content.Mentioned) {
            PostView(notification.content.post, onPostClick = {/*TODO*/})
        }
    }
}

@Composable
fun NotificationContent(modifier: Modifier, notification: Notification) {
    val notificationAuthorDisplayName = remember { notification.author.displayName ?: notification.author.handle.handle }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AsyncImage(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            model = ImageRequest.Builder(LocalContext.current)
                .data(notification.author.avatar)
                .crossfade(500)
                .build(),
            contentDescription = "Profile picture of $notificationAuthorDisplayName"
        )

        when (notification.reason) {
            Notification.Reason.LIKE -> {
                val content = notification.content as Notification.Content.Liked

                val postContent = remember {
                    if (content.post.text.length > 197)
                        content.post.text.substring(0, 197) + "..."
                    else
                        content.post.text
                }

                Text(
                    "$notificationAuthorDisplayName liked your post",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(postContent, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Notification.Reason.REPOST -> {
                val content = notification.content as Notification.Content.Reposted

                val postContent = remember {
                    if (content.post.text.length > 197)
                        content.post.text.substring(0, 197) + "..."
                    else
                        content.post.text
                }

                Text(
                    "$notificationAuthorDisplayName reposted your post",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(postContent, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Notification.Reason.FOLLOW -> {
                Text(
                    "$notificationAuthorDisplayName followed you",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // The others are basically just post views
            else -> {}
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun NotificationItemPreview() {
    val sampleTimelinePost = TimelinePost(
        AtUri(""),
        Cid(""),
        LiteProfile(
            Did(""),
            Handle("test.bsky.social"),
            "Test Account",
            "",
            false,
            false,
            false,
            emptyImmutableList()
        ),
        "Test post.",
        emptyImmutableList(),
        Moment(Instant.fromEpochMilliseconds(0)),
        null,
        0,
        0,
        0,
        Moment(Instant.fromEpochMilliseconds(0)),
        false,
        false,
        emptyImmutableList(),
        null,
        null,
        emptyList()
    )

    val sampleNotification = Notification(
        AtUri(""),
        Cid(""),
        LiteProfile(
            Did(""),
            Handle("test.bsky.social"),
            "Test Account",
            "",
            false,
            false,
            false,
            emptyImmutableList()
        ),
        Notification.Reason.LIKE,
        null,
        Notification.Content.Liked(sampleTimelinePost),
        false,
        Moment(Instant.fromEpochMilliseconds(0))
    )

    GreenlandTheme {
        NotificationItem(Modifier.fillMaxWidth(), sampleNotification) {}
    }
}
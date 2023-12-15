package dev.itswin11.greenland.views.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.models.LiteProfile
import dev.itswin11.greenland.models.Profile

@Composable
fun ProfileFollowingItem(profile: Profile, modifier: Modifier = Modifier, shouldShowFollowLabel: Boolean = false) {
    if (profile !is LiteProfile)
        return

    val displayName = remember { profile.displayName ?: profile.handle.handle }

    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .padding(24.dp, 12.dp, 12.dp, 12.dp)) {
        val (icon, content, followButton) = createRefs()

        AsyncImage(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(content.start, margin = 12.dp)
                    width = Dimension.preferredWrapContent
                },
            model = ImageRequest.Builder(LocalContext.current)
                .data(profile.avatar)
                .crossfade(500)
                .build(),
            contentDescription = "Profile picture of $displayName"
        )

        Column(
            modifier = Modifier.constrainAs(content) {
                start.linkTo(icon.end)
                top.linkTo(parent.top)
                end.linkTo(followButton.start, margin = 12.dp)
                width = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.Center,
        ) {
            if (profile.displayName == null) {
                Text(
                    profile.handle.handle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else {
                Text(
                    displayName,
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    "@${profile.handle.handle}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (profile.followingMe && shouldShowFollowLabel) {
                Surface(
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Follows you",
                        Modifier.padding(8.dp, 2.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 14.sp
                    )
                }
            }

            if (profile.description != null) {
                Text(
                    profile.description,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Button(
            onClick = {/*TODO*/},
            modifier = Modifier.constrainAs(followButton) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                width = Dimension.preferredWrapContent
            }.widthIn(min = 118.dp)
        ) {
            Text(if (profile.followedByMe) "Following" else "Follow")
        }
    }
}
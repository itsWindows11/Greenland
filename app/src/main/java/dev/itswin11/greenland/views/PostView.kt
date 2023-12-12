package dev.itswin11.greenland.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.bsky.actor.ProfileViewBasic
import app.bsky.feed.PostView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.R
import dev.itswin11.greenland.activities.home.timeAgo
import dev.itswin11.greenland.models.EmbedPost
import dev.itswin11.greenland.models.Label
import dev.itswin11.greenland.models.LitePost
import dev.itswin11.greenland.models.Moment
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.TimelinePostFeature
import dev.itswin11.greenland.models.TimelinePostLink
import dev.itswin11.greenland.models.TimelinePostReason
import dev.itswin11.greenland.models.toLitePost
import dev.itswin11.greenland.models.toPost
import dev.itswin11.greenland.models.toProfile
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView(
    post: TimelinePost,
    isThreadChild: Boolean = false,
    hasThreadChild: Boolean = false,
    onPostClick: (post: LitePost) -> Unit
) {
    val displayName = remember { post.author.displayName ?: post.author.handle }
    val paddingModifier =
        if (isThreadChild) Modifier.padding(12.dp, 4.dp, 12.dp, 0.dp) else Modifier.padding(
            12.dp,
            12.dp,
            12.dp,
            0.dp
        )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            onClick = { onPostClick(post.toLitePost()) },
            shape = RoundedCornerShape(0.dp)
        ) {
            if (post.reposted && post.reason is TimelinePostReason.TimelinePostRepost) {
                val reposterName = remember {
                    post.reason.repostAuthor.displayName ?: post.reason.repostAuthor.handle.handle
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp, 8.dp, 4.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.ic_repost),
                        contentDescription = null,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )

                    Text(
                        text = "Reposted by $reposterName",
                        maxLines = 1,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            ConstraintLayout(paddingModifier.fillMaxWidth()) {
                val (timelineAndAvatarRef, postContentRef) = createRefs()

                Box(
                    modifier = Modifier.constrainAs(timelineAndAvatarRef) {
                        top.linkTo(postContentRef.top)
                        start.linkTo(parent.start)
                        end.linkTo(postContentRef.start)
                        bottom.linkTo(postContentRef.bottom)
                        height = Dimension.fillToConstraints
                    },
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (hasThreadChild) {
                        Divider(
                            Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .padding(top = 52.dp)
                        )
                    }

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
                }

                PostContent(
                    modifier = Modifier
                        .padding(12.dp, 0.dp, 0.dp, 0.dp)
                        .constrainAs(postContentRef) {
                            start.linkTo(timelineAndAvatarRef.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxHeight(),
                    post = post,
                    onPostClick = onPostClick
                )
            }
        }
    }
}

// TODO: Implement post interactions
@Composable
fun PostContent(modifier: Modifier = Modifier, post: TimelinePost, onPostClick: (post: LitePost) -> Unit) {
    val displayName = remember { post.author.displayName ?: post.author.handle.handle }
    val timeAgoString = remember { timeAgo(post.createdAt.instant.epochSeconds) }

    val context = LocalContext.current

    Column(modifier) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                if (post.author.displayName != null) {
                    Text(
                        "@${post.author.handle}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .offset(4.dp, (-4).dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(0.dp, Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Rounded.MoreHoriz, contentDescription = "More")
            }
        }

        Column(
            modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (post.text.isNotBlank()) {
                Text(post.text)
            }

            if (post.feature != null) {
                when (post.feature) {
                    is TimelinePostFeature.ExternalFeature -> PostExternalEmbed(
                        Modifier.fillMaxWidth(),
                        post.feature
                    )

                    is TimelinePostFeature.ImagesFeature -> {
                        if (post.feature.images.size > 1) {
                            Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = 650.dp)
                        }

                        PostImageGrid(
                            modifier = Modifier.fillMaxWidth(),
                            images = { post.feature.images },
                            onImageClick = {
                                // TODO: Image Click Event
                                Toast.makeText(context, "TODO: Image Click", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onAltButtonClick = {
                                // TODO: Alt Click Event
                                Toast.makeText(context, "TODO: Alt Click", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }

                    is TimelinePostFeature.MediaPostFeature -> {
                        if (post.feature.media is TimelinePostFeature.ExternalFeature) {
                            PostExternalEmbed(Modifier.fillMaxWidth(), post.feature.media)
                        } else if (post.feature.media is TimelinePostFeature.ImagesFeature) {
                            if (post.feature.media.images.size > 1) {
                                Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 650.dp)
                            }

                            PostImageGrid(
                                modifier = Modifier.fillMaxWidth(),
                                images = { post.feature.media.images },
                                onImageClick = {
                                    // TODO: Image Click Event
                                    Toast.makeText(context, "TODO: Image Click", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onAltButtonClick = {
                                    // TODO: Alt Click Event
                                    Toast.makeText(context, "TODO: Alt Click", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }

                        when (post.feature.post) {
                            is EmbedPost.VisibleEmbedPost
                                -> EmbeddedPost(Modifier.fillMaxWidth(), post.feature.post, onPostClick)

                            is EmbedPost.GeneratorViewEmbedPost
                                -> GeneratorViewEmbed(Modifier.fillMaxWidth(), post.feature.post)

                            is EmbedPost.GraphListEmbedPost
                                -> GraphListEmbed(Modifier.fillMaxWidth(), post.feature.post)

                            is EmbedPost.BlockedEmbedPost
                                -> FeedWarningContainer(
                                    Modifier.fillMaxWidth(),
                                    "This post is made by a user whom you have blocked."
                                   )

                            else -> {}
                        }
                    }

                    is TimelinePostFeature.PostFeature -> {
                        when (post.feature.post) {
                            is EmbedPost.VisibleEmbedPost
                            -> EmbeddedPost(Modifier.fillMaxWidth(), post.feature.post, onPostClick)

                            is EmbedPost.GeneratorViewEmbedPost
                            -> GeneratorViewEmbed(Modifier.fillMaxWidth(), post.feature.post)

                            is EmbedPost.GraphListEmbedPost
                            -> GraphListEmbed(Modifier.fillMaxWidth(), post.feature.post)

                            is EmbedPost.BlockedEmbedPost
                            -> FeedWarningContainer(
                                Modifier.fillMaxWidth(),
                                "This post is made by a user whom you have blocked."
                            )

                            else -> {}
                        }
                    }
                    else -> {}
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset((-12).dp, 0.dp)
                .fillMaxWidth()
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
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        painter = painterResource(R.drawable.ic_comment),
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
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        painter = painterResource(R.drawable.ic_repost),
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
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        painter = painterResource(R.drawable.ic_like),
                        contentDescription = "${post.likeCount} likes"
                    )
                    Text(post.likeCount.toString())
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .offset(8.dp, 0.dp)
                    .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(0.dp, Color.Transparent),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp),
                    painter = painterResource(R.drawable.ic_share_outlined),
                    contentDescription = "Share"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostViewPreview() {
    val sampleProfile = ProfileViewBasic(
        Did("DID"),
        Handle("handle"),
        "Display Name",
        ""
    )

    val bskyPost = PostView(
        AtUri(""),
        Cid("CID sample"),
        sampleProfile,
        JsonObject(emptyMap()),
        null,
        0,
        0,
        0,
        Instant.fromEpochSeconds(0)
    ).toPost()

    TimelinePost(
        AtUri(""),
        Cid("CID sample"),
        sampleProfile.toProfile(),
        "Sample Post Content",
        emptyList<TimelinePostLink>().toImmutableList(),
        Moment(Instant.fromEpochSeconds(0)),
        null,
        0,
        0,
        0,
        Moment(Instant.fromEpochSeconds(0)),
        reposted = false,
        liked = false,
        emptyList<Label>().toImmutableList(),
        null,
        null,
        emptyList()
    )

    GreenlandTheme {
        PostView(bskyPost!!, onPostClick = {})
    }
}
package dev.itswin11.greenland.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.R
import dev.itswin11.greenland.activities.home.timeAgo
import dev.itswin11.greenland.main.DateHelpers
import dev.itswin11.greenland.models.BskyPost
import dev.itswin11.greenland.models.BskyPostRecord
import dev.itswin11.greenland.models.BskyProfileViewBasic
import dev.itswin11.greenland.ui.theme.GreenlandTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView(
    post: BskyPost,
    isThreadChild: Boolean = false,
    hasThreadChild: Boolean = false,
    preview: Boolean = false
) {
    val displayName = remember { post.author.displayName ?: post.author.handle }
    val paddingModifier = if (isThreadChild) Modifier.padding(12.dp, 0.dp) else Modifier.padding(
        12.dp,
        12.dp,
        12.dp,
        0.dp
    )

    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            onClick = {},
            shape = RoundedCornerShape(0.dp)
        ) {
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
                                .fillMaxHeight())
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
                    preview = preview
                )
            }
        }
    }
}

// TODO: Implement post interactions
@Composable
fun PostContent(modifier: Modifier = Modifier, post: BskyPost, preview: Boolean = false) {
    val displayName = remember { post.author.displayName ?: post.author.handle }

    val dateInMillis = remember { if (preview) 0 else DateHelpers.parseAtProtoIsoDate(post.record.createdAt) }
    val timeAgoString = remember { timeAgo(dateInMillis) }

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
                    .offset(12.dp, (-8).dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(0.dp, Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "More")
            }
        }

        Column(modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)) {
            Text(post.record.text)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset((-12).dp, 0.dp)
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
            }

            Spacer(Modifier.weight(1f))

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
                        .width(20.dp)
                        .height(20.dp),
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
        Box(Modifier.height(IntrinsicSize.Min)) {
            PostView(bskyPost, preview = true)
        }
    }
}
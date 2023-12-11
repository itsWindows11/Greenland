package dev.itswin11.greenland.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.activities.home.timeAgo
import dev.itswin11.greenland.models.EmbedPost
import dev.itswin11.greenland.models.TimelinePostFeature

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbeddedPost(modifier: Modifier = Modifier, post: EmbedPost.VisibleEmbedPost) {
    val context = LocalContext.current

    val displayName = remember { post.author.displayName ?: post.author.handle.handle }

    val postText = remember {
        if (post.litePost.text.length > 200) {
            post.litePost.text.substring(0, 197) + "..."
        } else {
            post.litePost.text
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        onClick = { }
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.author.avatar)
                        .crossfade(500)
                        .build(),
                    contentDescription = "Profile picture of $displayName"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "@${post.author.handle.handle} • ${timeAgo(post.litePost.createdAt.instant.epochSeconds)}",
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Text(text = postText, fontSize = 14.sp)

            when (post.litePost.embed) {
                is TimelinePostFeature.ImagesFeature -> PostImageGrid(
                    modifier = Modifier.fillMaxWidth(),
                    images = { post.litePost.embed.images },
                    onImageClick = {
                        // TODO: Image Click Event
                        Toast.makeText(context, "TODO: Image Click", Toast.LENGTH_SHORT).show()
                    },
                    onAltButtonClick = {
                        // TODO: Alt Click Event
                        Toast.makeText(context, "TODO: Alt Click", Toast.LENGTH_SHORT).show()
                    }
                )
                is TimelinePostFeature.MediaPostFeatureWithoutEmbedPost -> {
                    if (post.litePost.embed.media is TimelinePostFeature.ImagesFeature) {
                        if (post.litePost.embed.media.images.size > 1) {
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
                            images = { post.litePost.embed.media.images },
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
                }
                else -> {}
            }
        }
    }
}
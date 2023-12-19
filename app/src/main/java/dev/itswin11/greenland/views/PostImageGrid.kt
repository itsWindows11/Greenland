package dev.itswin11.greenland.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.models.EmbedImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun PostImageGrid(
    modifier: Modifier = Modifier,
    images: () -> ImmutableList<EmbedImage>,
    onImageClick: (imageIndex: Int) -> Unit,
    onAltButtonClick: (imageIndex: Int) -> Unit
) {
    val imagesRemembered = remember { images() }
    val blackRippleEffect = rememberRipple(color = Color.Black)

    when (imagesRemembered.size) {
        0 -> return
        2 -> {
            Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                PostImageWrapper(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = blackRippleEffect,
                            onClick = { onImageClick(0) }
                        )
                        .fillMaxHeight(),
                    altExists = imagesRemembered[0].alt.isNotEmpty(),
                    onAltButtonClick = { onAltButtonClick(0) }
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagesRemembered[0].thumb)
                            .crossfade(500)
                            .build(),
                        contentDescription = imagesRemembered[0].alt,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                PostImageWrapper(
                    modifier = Modifier
                        .clip(RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = blackRippleEffect,
                            onClick = { onImageClick(1) }
                        )
                        .fillMaxHeight(),
                    altExists = imagesRemembered[1].alt.isNotEmpty(),
                    onAltButtonClick = { onAltButtonClick(1) }
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagesRemembered[1].thumb)
                            .crossfade(500)
                            .build(),
                        contentDescription = imagesRemembered[1].alt,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
            }
        }
        3 -> {
            Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                PostImageWrapper(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = blackRippleEffect,
                            onClick = { onImageClick(0) }
                        )
                        .fillMaxHeight(),
                    altExists = imagesRemembered[0].alt.isNotEmpty(),
                    onAltButtonClick = { onAltButtonClick(0) }
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagesRemembered[0].thumb)
                            .crossfade(500)
                            .build(),
                        contentDescription = imagesRemembered[0].alt,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 12.dp, 0.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(1) }
                            ),
                        altExists = imagesRemembered[1].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(1) }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[1].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[1].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(2) }
                            ),
                        altExists = imagesRemembered[2].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(2) }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[2].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[2].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }
            }
        }
        4 -> {
            Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(0) }
                            )
                            .fillMaxHeight(),
                        altExists = imagesRemembered[0].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(0) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[0].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[0].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 12.dp, 0.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(2) }
                            )
                            .fillMaxHeight(),
                        altExists = imagesRemembered[2].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(2) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[2].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[2].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 0.dp, 0.dp, 12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(1) }
                            )
                            .fillMaxHeight(),
                        altExists = imagesRemembered[1].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(1) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[1].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[1].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                    PostImageWrapper(
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = blackRippleEffect,
                                onClick = { onImageClick(3) }
                            )
                            .fillMaxHeight(),
                        altExists = imagesRemembered[3].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(3) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[3].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[3].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }
            }
        }
        else -> {
            Row(modifier) {
                repeat(imagesRemembered.size) {
                    var imageModifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = blackRippleEffect,
                            onClick = { onImageClick(it) }
                        )

                    if (imagesRemembered[it].aspectRatio?.ratio != null
                        && (imagesRemembered[it].aspectRatio!!.width > 0
                                || imagesRemembered[it].aspectRatio!!.height > 0)
                        && imagesRemembered[it].aspectRatio!!.ratio > 0) {
                        imageModifier = imageModifier.aspectRatio(imagesRemembered[it].aspectRatio!!.ratio.toFloat())
                    }

                    PostImageWrapper(
                        modifier = imageModifier,
                        altExists = imagesRemembered[it].alt.isNotEmpty(),
                        onAltButtonClick = { onAltButtonClick(it) }
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onImageClick(it) }
                                .fillMaxWidth()
                                .requiredHeightIn(min = 48.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imagesRemembered[it].thumb)
                                .crossfade(500)
                                .build(),
                            contentDescription = imagesRemembered[it].alt,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostImageWrapper(
    modifier: Modifier = Modifier,
    altExists: Boolean = false,
    onAltButtonClick: @DisallowComposableCalls () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier.heightIn(min = 120.dp)) {
        content()

        if (altExists) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(12.dp, (-12).dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onAltButtonClick() }
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                        .padding(8.dp, 4.dp),
                    text = "ALT",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostImageGridPreview() {
    PostImageGrid(
        modifier = Modifier
            .height(350.dp)
            .fillMaxWidth()
            .padding(8.dp),
        onImageClick = {},
        onAltButtonClick = {},
        images = {
            listOf(
                EmbedImage(
                    "",
                    "",
                    "Alt text"
                ),
                EmbedImage(
                    "",
                    "",
                    ""
                ),
                EmbedImage(
                    "",
                    "",
                    ""
                )
            ).toImmutableList()
        }
    )
}
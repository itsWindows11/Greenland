package dev.itswin11.greenland.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.request.CachePolicy
import coil.request.ImageRequest
import dev.itswin11.greenland.util.conditional
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ZoomableImagePopup(
    isOpen: Boolean,
    onDismiss: @DisallowComposableCalls () -> Unit,
    imageUrl: String,
    placeholderImageUrl: String? = null
) {
    val coroutineScope = rememberCoroutineScope()

    val zoomState = rememberZoomableImageState()
    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, 350f to 1, -200f to -1)

    val isImageBeingDragged = swipeableState.offset.value != 0f

    var imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
        .diskCachePolicy(CachePolicy.DISABLED)
        .data(imageUrl)
        .crossfade(500)

    if (placeholderImageUrl != null) {
        imageRequestBuilder = imageRequestBuilder.placeholderMemoryCacheKey(placeholderImageUrl)
    }

    if (isOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                onDismiss()
                coroutineScope.launch {
                    swipeableState.snapTo(0)
                    zoomState.zoomableState.resetZoom(withAnimation = false)
                }
            },
            content = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
                        .conditional(
                            condition = (zoomState.zoomableState.zoomFraction ?: 0f) < 0.1f,
                            modifier = Modifier.swipeable(
                                state = swipeableState,
                                anchors = anchors,
                                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                orientation = Orientation.Vertical
                            )
                        )
                ) {
                    ZoomableAsyncImage(
                        model = imageRequestBuilder.build(),
                        contentDescription = null,
                        state = zoomState,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                    )

                    AnimatedVisibility(
                        modifier = Modifier
                            .offset((-16).dp, 16.dp)
                            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                            .align(Alignment.TopEnd),
                        visible = !isImageBeingDragged,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        OutlinedButton(
                            onClick = { onDismiss() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(28.dp),
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                if (swipeableState.isAnimationRunning) {
                    LaunchedEffect(Unit) {
                        if (swipeableState.offset.value < -170 || swipeableState.offset.value > 300) {
                            onDismiss()
                            swipeableState.snapTo(0)
                            zoomState.zoomableState.resetZoom(withAnimation = false)
                        } else {
                            swipeableState.animateTo(0)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ZoomableImagePopup(isOpen: Boolean, imageUrls: Iterable<String>) {
    // TODO: Use a pager to put the images in, for now we will only use ZoomableImagePopup(Boolean, String)
}
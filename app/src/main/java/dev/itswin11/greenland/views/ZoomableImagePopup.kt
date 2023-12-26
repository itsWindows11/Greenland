package dev.itswin11.greenland.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.request.CachePolicy
import coil.request.ImageRequest
import dev.itswin11.greenland.util.conditional
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomableImageState
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

    val showImageOptionsSheet = rememberSaveable { mutableStateOf(false) }

    val isImageBeingDragged = swipeableState.offset.value !in -0.01f..0f

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
                        .background(MaterialTheme.colorScheme.background)
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

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        AnimatedVisibility(
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            visible = !isImageBeingDragged,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                { onDismiss() },
                                Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        AnimatedVisibility(
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            visible = !isImageBeingDragged,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                { /*TODO: Bottom sheet for saving the image*/ },
                                Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Rounded.MoreHoriz,
                                    contentDescription = "More",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
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

        ImageOptionsBottomSheet(
            isOpen = showImageOptionsSheet.value,
            onSaveImageClick = {  },
            onShareClick = {  },
            onDismiss = {
                showImageOptionsSheet.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ZoomableImagePopup(
    isOpen: Boolean,
    onDismiss: @DisallowComposableCalls () -> Unit,
    imageUrls: ImmutableList<String>,
    placeholderImageUrls: ImmutableList<String>,
    initialPage: Int = 0
) {
    val coroutineScope = rememberCoroutineScope()

    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, 350f to 1, -200f to -1)

    val isImageBeingDragged = swipeableState.offset.value !in -0.01f..0f

    val pagerState = rememberPagerState(initialPage) { imageUrls.count() }
    val showImageOptionsSheet = rememberSaveable { mutableStateOf(false) }
    val selectedImageIndex = rememberSaveable { mutableIntStateOf(-1) }

    val context = LocalContext.current

    var currentZoomState: ZoomableImageState? = null

    if (isOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                onDismiss()
                coroutineScope.launch {
                    swipeableState.snapTo(0)
                    currentZoomState?.zoomableState?.resetZoom(withAnimation = false)
                }
            },
            content = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .conditional(
                            condition = (currentZoomState?.zoomableState?.zoomFraction
                                ?: 0f) < 0.1f,
                            modifier = Modifier.swipeable(
                                state = swipeableState,
                                anchors = anchors,
                                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                orientation = Orientation.Vertical
                            )
                        )
                ) {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = !(isImageBeingDragged || (currentZoomState?.zoomableState?.zoomFraction ?: 0f) >= 0.1f),
                        beyondBoundsPageCount = (imageUrls.size / 2).coerceAtLeast(2)
                    ) {
                        val imageRequestBuilder = remember {
                            ImageRequest.Builder(context)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .data(imageUrls[it])
                                .placeholderMemoryCacheKey(placeholderImageUrls[it])
                        }

                        val zoomState = rememberZoomableImageState()
                        currentZoomState = zoomState

                        ZoomableAsyncImage(
                            model = imageRequestBuilder.build(),
                            contentDescription = null,
                            state = zoomState,
                            modifier = Modifier
                                .fillMaxSize()
                                .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                        )

                        DisposableEffect(Unit) {
                            onDispose {
                                coroutineScope.launch {
                                    swipeableState.snapTo(0)
                                    currentZoomState?.zoomableState?.resetZoom(withAnimation = false)
                                }
                            }
                        }
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        AnimatedVisibility(
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            visible = !isImageBeingDragged,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                { onDismiss() },
                                Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        AnimatedVisibility(
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            visible = !isImageBeingDragged,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                {
                                    showImageOptionsSheet.value = true
                                    selectedImageIndex.intValue = pagerState.currentPage
                                },
                                Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Rounded.MoreHoriz,
                                    contentDescription = "More",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                if (swipeableState.isAnimationRunning) {
                    LaunchedEffect(Unit) {
                        if (swipeableState.offset.value < -170 || swipeableState.offset.value > 300) {
                            onDismiss()
                            swipeableState.snapTo(0)
                            currentZoomState?.zoomableState?.resetZoom(withAnimation = false)
                        } else {
                            swipeableState.animateTo(0)
                        }
                    }
                }
            }
        )
    }

    ImageOptionsBottomSheet(
        isOpen = selectedImageIndex.intValue > -1 && showImageOptionsSheet.value,
        onSaveImageClick = {  },
        onShareClick = {  },
        onDismiss = {
            showImageOptionsSheet.value = false
            selectedImageIndex.intValue = -1
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptionsBottomSheet(
    isOpen: Boolean,
    onSaveImageClick: () -> Unit,
    onShareClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    if (isOpen) {
        ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save image")
                Text("Share image")
                Divider()
                Text("Save all images")
            }
        }
    }
}
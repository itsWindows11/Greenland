package dev.itswin11.greenland.views.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.itswin11.greenland.enums.PostAction
import dev.itswin11.greenland.models.SelectedPostData
import dev.itswin11.greenland.util.emptyImmutableList
import dev.itswin11.greenland.viewmodels.HomeViewModel
import dev.itswin11.greenland.views.PostsList
import dev.itswin11.greenland.views.ZoomableImagePopup
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onPostClick: (SelectedPostData) -> Unit
) {
    val scrollState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()

    val posts = viewModel.posts

    val isFabVisible = rememberSaveable { mutableStateOf(true) }
    val showImageViewer = rememberSaveable { mutableStateOf(false) }
    val showAltBottomSheet = rememberSaveable { mutableStateOf(false) }

    val selectedPostData = viewModel.selectedPostData.collectAsStateWithLifecycle()

    val fabNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -2) {
                    isFabVisible.value = false
                }

                if (available.y > 6) {
                    isFabVisible.value = true
                }

                return Offset.Zero
            }
        }
    }

    val pinnedScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Surface(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        OutlinedButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.dp, Color.Transparent),
                            contentPadding = PaddingValues(8.dp, 0.dp),
                        ) {
                            Text("Home", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Icon(
                                Icons.Rounded.ChevronRight,
                                contentDescription = "",
                                modifier = Modifier
                                    .rotate(90f)
                                    .offset(2.dp, (-4).dp)
                            )
                        }
                    },
                    scrollBehavior = pinnedScrollBehavior
                )

                PostsList(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    scrollState,
                    posts,
                    pinnedScrollBehavior.nestedScrollConnection,
                    fabNestedScrollConnection
                ) { action, data ->
                    // Make sure nothing is open so we get a squeaky clean state.
                    showAltBottomSheet.value = false
                    showImageViewer.value = false

                    when (action) {
                        PostAction.ViewImage -> {
                            viewModel.selectedPostData.value = data
                            showImageViewer.value = true
                        }

                        PostAction.ViewAltText -> {
                            viewModel.selectedPostData.value = data
                            showAltBottomSheet.value = true
                        }

                        PostAction.ViewPost -> {
                            onPostClick(data)
                        }

                        PostAction.More -> {
                            // TODO: Open bottom sheet for more actions
                        }

                        PostAction.Reply -> {
                            // TODO: Open popup for replying
                        }

                        PostAction.Repost -> {
                            viewModel.selectedPostData.value = data

                            // TODO: Open bottom sheet for choosing whether to quote or repost
                        }

                        PostAction.Like -> {
                            // TODO: Find the index of the post, update the state after adding
                            // a like record.
                        }

                        PostAction.ViewProfile -> {
                            // TODO: Expose ability to control the navigation or use Activity-based approach
                        }

                        PostAction.Share -> {
                            // TODO: Open share sheet and provide post content or URL
                        }

                        PostAction.ViewFeed -> TODO()
                        PostAction.ViewGraphList -> TODO()
                    }
                }
            }

            AnimatedVisibility(
                visible = isFabVisible.value,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 20.dp, 20.dp)
            ) {
                FloatingActionButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Create")
                }
            }
        }
    }

    ZoomableImagePopup(
        selectedPostData.value?.selectedImageIndex != null
                && (selectedPostData.value?.imagesFeature?.images?.size ?: 0) == 1
                && showImageViewer.value,
        {
            showImageViewer.value = false
            viewModel.selectedPostData.value = null
        },
        selectedPostData.value?.imagesFeature?.images?.get(selectedPostData.value?.selectedImageIndex ?: 0)?.fullsize ?: "",
        selectedPostData.value?.imagesFeature?.images?.get(selectedPostData.value?.selectedImageIndex ?: 0)?.thumb ?: ""
    )

    ZoomableImagePopup(
        selectedPostData.value?.selectedImageIndex != null
                && (selectedPostData.value?.imagesFeature?.images?.size ?: 0) > 1
                && showImageViewer.value,
        {
            showImageViewer.value = false
            viewModel.selectedPostData.value = null
        },
        selectedPostData.value?.imagesFeature?.images?.map { it.fullsize }?.toImmutableList() ?: emptyImmutableList(),
        selectedPostData.value?.imagesFeature?.images?.map { it.thumb }?.toImmutableList() ?: emptyImmutableList(),
        selectedPostData.value?.selectedImageIndex ?: 0
    )

    AltBottomSheet(
        selectedPostData.value?.selectedImageIndex != null && showAltBottomSheet.value,
        selectedPostData.value?.selectedImageIndex?.let {
            selectedPostData.value?.imagesFeature?.images?.get(selectedPostData.value?.selectedImageIndex ?: 0)?.alt
        } ?: "No alt text."
    ) {
        showAltBottomSheet.value = false
        viewModel.selectedPostData.value = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AltBottomSheet(isOpen: Boolean, altText: String, onDismiss: @DisallowComposableCalls () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    if (isOpen) {
        ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Image description",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)
                )

                Column(Modifier.verticalScroll(rememberScrollState()).heightIn(max = 700.dp)) {
                    Text(
                        altText,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                    )
                }
            }
        }
    }
}
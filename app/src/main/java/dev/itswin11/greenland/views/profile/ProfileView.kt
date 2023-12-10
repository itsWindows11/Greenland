package dev.itswin11.greenland.views.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.App
import dev.itswin11.greenland.enums.UserProfileOverviewTabType
import dev.itswin11.greenland.models.FullProfile
import dev.itswin11.greenland.viewmodels.ProfileViewModel
import dev.itswin11.greenland.views.AppTab
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtIdentifier

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileView(actor: AtIdentifier? = null, viewModel: ProfileViewModel = viewModel()) {
    val profile = viewModel.profile.collectAsStateWithLifecycle()
    val selectedTab = viewModel.selectedTab.collectAsStateWithLifecycle()

    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    val shouldChangeTabByPager = remember { mutableStateOf(true) }

    val tabs = remember { listOf("Posts", "Replies", "Media", "Likes") }

    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            // We do not currently track the progress of re-fetching the profile.
            viewModel.isRefreshing.value = true
            viewModel.getProfile(actor)
        },
        refreshingOffset = 100.dp,
        refreshThreshold = 108.dp
    )

    LaunchedEffect(profile) {
        viewModel.getProfile(actor, true)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (shouldChangeTabByPager.value)
                viewModel.setSelectedTab(page)
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing.value)
            viewModel.getProfile(actor)
    }

    if (profile.value != null) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            val height = maxHeight
            val headerHeight = remember {
                mutableStateOf(0.dp)
            }

            val animatedPadding by animateDpAsState(
                targetValue = if (scrollState.value.dp >= headerHeight.value) 72.dp else 0.dp,
                label = "padding"
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                ProfileViewHeader(Modifier.onGloballyPositioned {
                    headerHeight.value = it.size.height.dp - 2.dp
                }, profile.value!!)

                Column(Modifier.height(height)) {
                    Surface(Modifier.padding(top = animatedPadding)) {
                        TabRow(
                            selectedTabIndex = selectedTab.value,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    height = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        ) {
                            tabs.forEachIndexed { index, title ->
                                AppTab(
                                    title = title,
                                    onClick = {
                                        shouldChangeTabByPager.value = false
                                        viewModel.setSelectedTab(index)
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                            shouldChangeTabByPager.value = true
                                        }
                                    }
                                )
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .nestedScroll(remember {
                                object : NestedScrollConnection {
                                    override fun onPreScroll(
                                        available: Offset,
                                        source: NestedScrollSource
                                    ): Offset {
                                        return if (available.y > 0) Offset.Zero else Offset(
                                            x = 0f,
                                            y = -scrollState.dispatchRawDelta(-available.y)
                                        )
                                    }
                                }
                            }),
                        beyondBoundsPageCount = 0
                    ) {
                        when (it) {
                            0 -> UserProfileOverview(
                                UserProfileOverviewTabType.POSTS,
                                Modifier.fillMaxSize(),
                                viewModel
                            )

                            1 -> UserProfileOverview(
                                UserProfileOverviewTabType.REPLIES,
                                Modifier.fillMaxSize(),
                                viewModel
                            )

                            2 -> UserProfileOverview(
                                UserProfileOverviewTabType.MEDIA,
                                Modifier.fillMaxSize(),
                                viewModel
                            )

                            3 -> UserProfileOverview(
                                UserProfileOverviewTabType.LIKES,
                                Modifier.fillMaxSize(),
                                viewModel
                            )
                        }
                    }
                }
            }



            Box(
                Modifier
                    .fillMaxWidth()
                    .height(114.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            0.5f to MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            1f to Color.Transparent
                        )
                    )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            PullRefreshIndicator(
                isRefreshing.value,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                scale = true
            )
        }
    } else {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 12.dp, 0.dp, 0.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.requiredWidth(48.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileViewHeader(modifier: Modifier = Modifier, profile: FullProfile) {
    val displayName = remember { profile.displayName ?: profile.handle.handle }

    Box(modifier) {
        AsyncImage(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .height(180.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(profile.banner)
                .crossfade(500)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "Banner of $displayName"
        )

        Box(Modifier.padding(12.dp, 148.dp, 12.dp, 0.dp)) {
            Column {
                AsyncImage(
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.avatar)
                        .crossfade(500)
                        .build(),
                    contentDescription = "Profile picture of $displayName"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        displayName,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (profile.followingMe) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.offset(y = 5.dp),
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
                }

                if (profile.displayName != null) {
                    Text("@${profile.handle.handle}")
                }

                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            pushStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            append(profile.followersCount.toString())

                            pop()
                            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant))

                            append(" followers")
                        }
                    )

                    Text(
                        buildAnnotatedString {
                            pushStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            append(profile.followsCount.toString())

                            pop()
                            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant))

                            append(" following")
                        }
                    )
                }

                if (profile.description != null) {
                    Text(profile.description, Modifier.padding(vertical = 8.dp))
                }
            }

            Row(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 44.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    if (App.currentUser?.did?.did == profile.did.did) {
                        Text("Edit Profile")
                    } else if (profile.followedByMe) {
                        Text("Following")
                    } else {
                        // TODO: Filled button for this
                        Text("Follow")
                    }
                }
            }
        }
    }
}
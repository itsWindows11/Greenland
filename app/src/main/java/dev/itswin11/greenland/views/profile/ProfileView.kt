package dev.itswin11.greenland.views.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.enums.UserProfileOverviewTabType
import dev.itswin11.greenland.models.FullProfile
import dev.itswin11.greenland.viewmodels.ProfileViewModel
import dev.itswin11.greenland.views.AppTab
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtIdentifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileView(actor: AtIdentifier? = null, viewModel: ProfileViewModel = viewModel()) {
    val profile = viewModel.profile.collectAsStateWithLifecycle()
    val selectedTab = viewModel.selectedTab.collectAsStateWithLifecycle()

    val shouldChangeTabByPager = remember { mutableStateOf(true) }

    val tabs = remember { listOf("Posts", "Replies", "Media", "Likes") }

    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    LaunchedEffect(profile) {
        viewModel.getProfile(actor)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (shouldChangeTabByPager.value)
                viewModel.setSelectedTab(page)
        }
    }

    if (profile.value != null) {
        BoxWithConstraints {
            val height = maxHeight

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                ProfileViewHeader(profile.value!!)

                Column(Modifier.height(height)) {
                    Surface(Modifier.statusBarsPadding()) {
                        TabRow(
                            selectedTabIndex = selectedTab.value,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                )
                            },
                        ) {
                            tabs.forEachIndexed { index, title ->
                                AppTab(
                                    title = title,
                                    onClick = {
                                        viewModel.setSelectedTab(index)
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
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
                            0 -> UserProfileOverview(UserProfileOverviewTabType.POSTS, Modifier.fillMaxSize(), viewModel)
                            1 -> UserProfileOverview(UserProfileOverviewTabType.REPLIES, Modifier.fillMaxSize(), viewModel)
                            2 -> UserProfileOverview(UserProfileOverviewTabType.MEDIA, Modifier.fillMaxSize(), viewModel)
                            3 -> UserProfileOverview(UserProfileOverviewTabType.LIKES, Modifier.fillMaxSize(), viewModel)
                        }
                    }
                }
            }
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

@Composable
private fun ProfileViewHeader(profile: FullProfile) {
    val displayName = remember { profile.displayName ?: profile.handle.handle }

    Box {
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

        Row(Modifier.padding(12.dp, 148.dp, 12.dp, 12.dp)) {
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

                Text(
                    displayName,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (profile.displayName != null) {
                    Text("@${profile.handle.handle}")
                }
            }

            Spacer(Modifier.weight(1f))

            Row(Modifier.offset(0.dp, 44.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Edit Profile")
                }
            }
        }
    }
}
package dev.itswin11.greenland.views.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.models.FullProfile
import dev.itswin11.greenland.viewmodels.ProfileViewModel
import sh.christian.ozone.api.AtIdentifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileView(actor: AtIdentifier? = null, viewModel: ProfileViewModel = viewModel()) {
    val profile = viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(profile) {
        viewModel.getProfile(actor)
    }

    if (profile.value != null) {
        LazyColumn(Modifier.fillMaxWidth()) {
            item {
                ProfileViewHeader(profile.value!!)
            }

            stickyHeader {
                Surface {
                    Text(
                        "Posts",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
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

        Row(Modifier.padding(12.dp, 148.dp, 12.dp, 0.dp)) {
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
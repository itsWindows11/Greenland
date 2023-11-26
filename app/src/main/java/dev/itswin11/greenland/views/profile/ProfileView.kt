package dev.itswin11.greenland.views.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import dev.itswin11.greenland.viewmodels.ProfileViewModel
import sh.christian.ozone.api.AtIdentifier

@Composable
fun ProfileView(actor: AtIdentifier? = null, viewModel: ProfileViewModel = viewModel()) {
    val profile = viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(profile) {
        viewModel.getProfile(actor)
    }

    if (profile.value != null) {
        Column {
            val profileValue = profile.value!!
            val displayName = remember { profileValue.displayName ?: profileValue.handle.handle }

            AsyncImage(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .height(220.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileValue.banner)
                    .crossfade(500)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "Banner of $displayName"
            )

            Row(Modifier.offset(0.dp, (-36).dp).padding(12.dp)) {
                Column {
                    AsyncImage(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profileValue.avatar)
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

                    if (profileValue.displayName != null) {
                        Text("@${profileValue.handle.handle}")
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(Modifier.offset(0.dp, 36.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text("Edit Profile")
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
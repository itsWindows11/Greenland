package dev.itswin11.greenland.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.itswin11.greenland.models.TimelinePostFeature
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import sh.christian.ozone.api.Uri as ApiUri

@Composable
fun PostExternalEmbed(modifier: Modifier = Modifier, embed: TimelinePostFeature.ExternalFeature) {
    val context = LocalContext.current

    Column(modifier
        .heightIn(min = 64.dp)
        .border(
            1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            RoundedCornerShape(12.dp)
        )
        .clip(RoundedCornerShape(12.dp))
        .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(embed.uri.uri))) }
    ) {
        if (embed.thumb != null) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(1.dp),
                model = ImageRequest.Builder(context)
                    .data(embed.thumb)
                    .crossfade(500)
                    .build(),
                contentDescription = "Thumbnail link for external website.",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }

        Column(modifier = Modifier.padding(16.dp, 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = getHostName(embed.uri.uri) ?: embed.uri.uri, fontSize = 14.sp)

            Text(
                text = embed.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            if (embed.description.isNotBlank()) {
                Text(
                    text = embed.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostExternalEmbedPreview() {
    GreenlandTheme {
        PostExternalEmbed(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            embed = TimelinePostFeature.ExternalFeature(
                uri = ApiUri("https://example.com"),
                title = "Example Embed",
                description = "This is an example embed.",
                thumb = ""
            )
        )
    }
}

private fun getHostName(url: String): String? {
    val uri = Uri.parse(url)
    val host = uri.host ?: return null

    return if (host.startsWith("www.")) host.substring(4) else host
}
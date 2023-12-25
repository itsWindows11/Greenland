package dev.itswin11.greenland.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.itswin11.greenland.models.LinkTarget
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.TimelinePostFeature
import dev.itswin11.greenland.models.TimelinePostLink
import dev.itswin11.greenland.models.UserDid
import dev.itswin11.greenland.models.UserHandle
import dev.itswin11.greenland.models.UserReference
import dev.itswin11.greenland.util.byteOffsets
import kotlinx.collections.immutable.ImmutableList
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

@OptIn(ExperimentalTextApi::class)
@Composable
fun FacetFormattedText(
    post: TimelinePost? = null,
    text: String,
    textLinks: ImmutableList<TimelinePostLink>,
    onClick: () -> Unit,
    onUserClick: (UserReference) -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val maybeExternalLink = (post?.feature as? TimelinePostFeature.ExternalFeature)?.uri?.uri
    val trimmedText = text.removeSuffix(maybeExternalLink.orEmpty()).trim()

    if (trimmedText.isBlank()) {
        Spacer(Modifier.height(0.dp))
    } else {
        val postText = rememberFormattedTextPost(trimmedText, textLinks, color)

        val uriHandler = LocalUriHandler.current
        ClickableText(
            text = postText,
            style = LocalTextStyle.current.copy(color = LocalContentColor.current),
            onClick = { index ->
                var performedAction = false
                postText.getStringAnnotations("hashtag", index, index).firstOrNull()?.item?.let { hashtag ->
                    // TODO: handle hashtag click
                }
                postText.getStringAnnotations("did", index, index).firstOrNull()?.item?.let { did ->
                    performedAction = true
                    onUserClick(UserDid(Did(did)))
                }
                postText.getStringAnnotations("handle", index, index).firstOrNull()?.item?.let { handle ->
                    performedAction = true
                    onUserClick(UserHandle(Handle(handle)))
                }
                postText.getUrlAnnotations(index, index).firstOrNull()?.item?.url?.let { url ->
                    performedAction = true
                    uriHandler.openUri(url)
                }
                if (!performedAction) {
                    onClick()
                }
            },
        )
    }
}

@Composable
fun rememberFormattedTextPost(
    text: String,
    textLinks: ImmutableList<TimelinePostLink>,
    color: Color
): AnnotatedString {
    return remember(text, textLinks) { formatTextPost(text, textLinks, color) }
}

@OptIn(ExperimentalTextApi::class)
fun formatTextPost(
    text: String,
    textLinks: List<TimelinePostLink>,
    color: Color
): AnnotatedString {
    return buildAnnotatedString {
        append(text)

        val byteOffsets = text.byteOffsets()
        textLinks.forEach { link ->
            if (link.start < byteOffsets.size && link.end < byteOffsets.size) {
                val start = byteOffsets[link.start]
                val end = byteOffsets[link.end]

                addStyle(
                    style = SpanStyle(color = color),
                    start = start,
                    end = end,
                )

                when (link.target) {
                    is LinkTarget.ExternalLink -> {
                        addUrlAnnotation(UrlAnnotation(link.target.uri.uri), start, end)
                    }
                    is LinkTarget.Hashtag -> {
                        addStringAnnotation("hashtag", link.target.tag, start, end)
                    }
                    is LinkTarget.UserDidMention -> {
                        addStringAnnotation("did", link.target.did.did, start, end)
                    }
                    is LinkTarget.UserHandleMention -> {
                        addStringAnnotation("handle", link.target.handle.handle, start, end)
                    }
                }
            }
        }
    }
}
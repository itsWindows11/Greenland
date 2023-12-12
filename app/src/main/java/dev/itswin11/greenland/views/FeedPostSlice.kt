package dev.itswin11.greenland.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.itswin11.greenland.models.LitePost
import dev.itswin11.greenland.models.TimelinePost

@Composable
fun FeedPostSlice(post: TimelinePost, showParentReply: Boolean = true, onPostClick: (post: LitePost) -> Unit) {
    Column {
        if (post.reply?.parent != null && showParentReply) {
            PostView(post.reply.parent, hasThreadChild = true, onPostClick = onPostClick)
        }

        PostView(post, isThreadChild = post.reply?.parent != null && showParentReply, onPostClick = onPostClick)
    }
}
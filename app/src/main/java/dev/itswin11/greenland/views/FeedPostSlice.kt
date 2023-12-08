package dev.itswin11.greenland.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.itswin11.greenland.models.TimelinePost

@Composable
fun FeedPostSlice(post: TimelinePost, showParentReply: Boolean = true) {
    Column {
        if (post.reply?.parent != null && showParentReply) {
            PostView(post.reply.parent, hasThreadChild = true)
        }

        PostView(post, isThreadChild = post.reply?.parent != null && showParentReply)
    }
}
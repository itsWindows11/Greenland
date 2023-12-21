package dev.itswin11.greenland.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.itswin11.greenland.enums.PostAction
import dev.itswin11.greenland.models.SelectedPostData
import dev.itswin11.greenland.models.TimelinePost

@Composable
fun FeedPostSlice(
    post: TimelinePost,
    showParentReply: Boolean = true,
    onInteraction: (action: PostAction, postData: SelectedPostData) -> Unit
) {
    Column {
        if (post.reply?.parent != null && showParentReply) {
            PostView(post.reply.parent, hasThreadChild = true, onInteraction = onInteraction)
        }

        PostView(post, isThreadChild = post.reply?.parent != null && showParentReply, onInteraction = onInteraction)
    }
}
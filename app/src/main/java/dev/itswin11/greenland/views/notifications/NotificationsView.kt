package dev.itswin11.greenland.views.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.models.Notification
import dev.itswin11.greenland.viewmodels.NotificationsViewModel
import dev.itswin11.greenland.views.PostView

@Composable
fun NotificationsView(viewModel: NotificationsViewModel = viewModel()) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        items(notifications.itemCount) {
            val notification = notifications[it]

            if (notification != null) {
                Column {
                    if (notification.isRead) {
                        Text("(read)")
                    } else  {
                        Text("(unread)")
                    }

                    Text(notification.author.handle.handle)

                    when (notification.reason) {
                        Notification.Reason.LIKE -> {
                            Text("Liked your post")
                        }
                        Notification.Reason.REPLY -> {
                            Text("Replied to your post")
                        }
                        Notification.Reason.REPOST -> {
                            Text("Reposted your post")
                        }
                        Notification.Reason.MENTION -> {
                            Text("Mentioned you in a post")
                        }
                        Notification.Reason.FOLLOW -> {
                            Text("Followed you")
                        }
                        else -> {}
                    }

                    if (notification.content is Notification.Content.Liked) {
                        val postContent = remember {
                            if (notification.content.post.text.length > 50)
                                notification.content.post.text.substring(0, 50)
                            else
                                notification.content.post.text
                        }

                        Text(postContent)
                    } else if (notification.content is Notification.Content.Quoted) {
                        PostView(notification.content.post)
                    }
                }

                Divider()
            }
        }
    }
}
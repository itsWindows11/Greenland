package dev.itswin11.greenland.views.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import dev.itswin11.greenland.util.conditional
import dev.itswin11.greenland.viewmodels.NotificationsViewModel

@Composable
fun NotificationsView(viewModel: NotificationsViewModel = viewModel()) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        items(notifications.itemCount) {
            val notification = notifications[it]

            if (notification != null) {
                NotificationItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .conditional(
                            !notification.isRead,
                            Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        ),
                    notification = notification,
                    onClick = {}
                )

                Divider()
            }
        }
    }
}
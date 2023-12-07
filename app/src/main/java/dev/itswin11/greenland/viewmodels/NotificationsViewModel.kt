package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.itswin11.greenland.models.Notification
import dev.itswin11.greenland.pagination.UserNotificationsSource
import kotlinx.coroutines.flow.Flow

class NotificationsViewModel : ViewModel() {
    val notifications: Flow<PagingData<Notification>> = Pager(PagingConfig(pageSize = 100)) {
        UserNotificationsSource()
    }.flow.cachedIn(viewModelScope)
}
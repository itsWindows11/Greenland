package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.pagination.PostFeedSource
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {
    val posts: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        PostFeedSource()
    }.flow.cachedIn(viewModelScope)
}
package dev.itswin11.greenland.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.itswin11.greenland.models.Profile
import dev.itswin11.greenland.pagination.ProfileFollowingSource
import kotlinx.coroutines.flow.Flow
import sh.christian.ozone.api.AtIdentifier

class ProfileFollowingViewModel : ViewModel() {
    val follows: Flow<PagingData<Profile>> = Pager(PagingConfig(pageSize = 100)) {
        ProfileFollowingSource(
            { profile.value!! },
            { isFollowingPage.value }
        )
    }.flow.cachedIn(viewModelScope)

    val isFollowingPage = mutableStateOf(false)
    val profile = mutableStateOf<AtIdentifier?>(null)
}
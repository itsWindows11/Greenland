package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.bsky.actor.GetProfileQueryParams
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.FullProfile
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.toProfile
import dev.itswin11.greenland.pagination.PostFeedSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import sh.christian.ozone.api.AtIdentifier

class ProfileViewModel : ViewModel() {
    val posts: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        PostFeedSource()
    }.flow.cachedIn(viewModelScope)

    private val _profile = MutableStateFlow<FullProfile?>(null)
    val profile = _profile.asStateFlow()

    suspend fun getProfile(actor: AtIdentifier? = null): FullProfile {
        if (actor == null || App.currentUser != null) {
            _profile.value = App.currentUser!!
            return App.currentUser!!
        }

        val profile = App.atProtoClient.getProfile(GetProfileQueryParams(actor)).requireResponse().toProfile()
        _profile.value = profile

        return profile
    }
}
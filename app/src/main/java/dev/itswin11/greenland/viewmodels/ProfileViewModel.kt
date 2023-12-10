package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.bsky.actor.GetProfileQueryParams
import app.bsky.feed.GetAuthorFeedFilter
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.FullProfile
import dev.itswin11.greenland.models.TimelinePost
import dev.itswin11.greenland.models.toProfile
import dev.itswin11.greenland.pagination.UserPostFeedSource
import dev.itswin11.greenland.pagination.UserPostLikesFeedSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtIdentifier

class ProfileViewModel : ViewModel() {
    val posts: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        UserPostFeedSource({ AtIdentifier(profile.value!!.did.did) },
            filter = GetAuthorFeedFilter.POSTS_NO_REPLIES)
    }.flow.cachedIn(viewModelScope)

    val postsWithReplies: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        UserPostFeedSource({ AtIdentifier(profile.value!!.did.did) },
            filter = GetAuthorFeedFilter.POSTS_WITH_REPLIES)
    }.flow.cachedIn(viewModelScope)

    val media: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        UserPostFeedSource({ AtIdentifier(profile.value!!.did.did) },
            filter = GetAuthorFeedFilter.POSTS_WITH_MEDIA)
    }.flow.cachedIn(viewModelScope)

    val likes: Flow<PagingData<TimelinePost>> = Pager(PagingConfig(pageSize = 100)) {
        UserPostLikesFeedSource { AtIdentifier(profile.value!!.did.did) }
    }.flow.cachedIn(viewModelScope)

    private val _profile = MutableStateFlow<FullProfile?>(null)
    val profile = _profile.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    // Temporary workaround until there's a better way
    // to update states that are outside composables.
    val isRefreshing = MutableStateFlow(false)

    fun getProfile(actor: AtIdentifier? = null, useCache: Boolean = false) {
        if (useCache && (actor == null || App.currentUser != null)) {
            _profile.value = App.currentUser!!
            return
        }

        viewModelScope.launch {
            val profile = App.atProtoClient
                .getProfile(GetProfileQueryParams(actor ?: AtIdentifier(App.currentUser!!.did.did)))
                .requireResponse()
                .toProfile()

            _profile.value = profile

            if (App.currentUser != null && profile.did == App.currentUser!!.did)
                App.currentUser = profile
        }
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }
}
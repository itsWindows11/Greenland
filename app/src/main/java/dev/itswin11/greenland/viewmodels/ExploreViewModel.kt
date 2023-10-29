package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.itswin11.greenland.App
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.models.BskyFeedGeneratorView
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.BskyProfileView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val _initiallyLoaded = MutableStateFlow(false)
    val initiallyLoaded = _initiallyLoaded.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _suggestedFeeds = MutableStateFlow<ImmutableList<BskyFeedGeneratorView>?>(null)
    val suggestedFeeds = _suggestedFeeds.asStateFlow()

    private val _suggestedFollows = MutableStateFlow<ImmutableList<BskyProfileView>?>(null)
    val suggestedFollows = _suggestedFollows.asStateFlow()

    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_initiallyLoaded.value && !isRefresh)
                return@launch

            if (isRefresh)
                _refreshing.value = true

            awaitAll(
                async {
                    _suggestedFeeds.value = App.atProtoClient.getSuggestedFeeds(BskyGetSuggestedFeedsInput(5))
                        .feeds.subList(0, 5).toImmutableList()
                },
                async {
                    val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
                    val did = App.instance.authDataStore.data
                        .map { preferences -> preferences.authInfoList[currentAccountIndex].did }.first()

                    _suggestedFollows.value = App.atProtoClient.getSuggestedFollowsByActor(did)
                        .suggestions.toImmutableList()
                }
                // TODO: Users on network
            )

            if (!isRefresh)
                _initiallyLoaded.value = true
            else
                _refreshing.value = false
        }
    }
}
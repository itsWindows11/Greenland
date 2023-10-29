package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyFeedGeneratorView
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.BskyProfileViewBasic
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val _initiallyLoaded = MutableStateFlow(false)
    val initiallyLoaded = _initiallyLoaded.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _suggestedFeeds = MutableStateFlow<List<BskyFeedGeneratorView>?>(null)
    val suggestedFeeds = _suggestedFeeds.asStateFlow()

    private val _suggestedFollows = MutableStateFlow<List<BskyProfileViewBasic>?>(null) // TODO: BskyProfileView
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
                        .feeds.subList(0, 5)
                },
                async {
                    _suggestedFollows.value = App.atProtoClient.getSuggestedFollowsByActor("simplebear.bsky.social")
                        .suggestions
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
package dev.itswin11.greenland.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetSuggestedFeedsQueryParams
import app.bsky.graph.GetSuggestedFollowsByActorQueryParams
import dev.itswin11.greenland.App
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.models.FeedGeneratorListing
import dev.itswin11.greenland.models.LiteProfile
import dev.itswin11.greenland.models.toFeedGeneratorListing
import dev.itswin11.greenland.models.toProfile
import dev.itswin11.greenland.util.mapImmutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtIdentifier

class ExploreViewModel : ViewModel() {
    private val _initiallyLoaded = MutableStateFlow(false)
    val initiallyLoaded = _initiallyLoaded.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _suggestedFeeds = MutableStateFlow<ImmutableList<FeedGeneratorListing>?>(null)
    val suggestedFeeds = _suggestedFeeds.asStateFlow()

    private val _suggestedFollows = MutableStateFlow<ImmutableList<LiteProfile>?>(null)
    val suggestedFollows = _suggestedFollows.asStateFlow()

    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_initiallyLoaded.value && !isRefresh)
                return@launch

            if (isRefresh)
                _refreshing.value = true

            awaitAll(
                async {
                    try {
                        _suggestedFeeds.value = App.atProtoClient.getSuggestedFeeds(
                            GetSuggestedFeedsQueryParams(5)
                        )
                            .requireResponse()
                            .feeds
                            .subList(0, 5)
                            .mapImmutable { it.toFeedGeneratorListing() }
                    } catch (_: Exception) {
                        // pass
                    }
                },
                async {
                    val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
                    val did = App.instance.authDataStore.data
                        .map { preferences -> preferences.authInfoList[currentAccountIndex].did }.first()

                    try {
                        _suggestedFollows.value = App.atProtoClient.getSuggestedFollowsByActor(
                            GetSuggestedFollowsByActorQueryParams(AtIdentifier(did))
                        )
                            .requireResponse()
                            .suggestions
                            .mapImmutable { it.toProfile() as LiteProfile }
                    } catch (e: Exception) {
                        Toast.makeText(App.instance, "Couldn't load explore page content. Try again later.", Toast.LENGTH_SHORT).show()
                    }
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
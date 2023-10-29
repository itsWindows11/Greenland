package dev.itswin11.greenland.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.itswin11.greenland.App
import dev.itswin11.greenland.models.BskyFeedViewPost
import dev.itswin11.greenland.models.BskyGetTimelineInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<BskyFeedViewPost>?>(null)
    val posts = _posts.asStateFlow()

    private val _postsInitiallyLoaded = MutableStateFlow(false)
    val postsInitiallyLoaded = _postsInitiallyLoaded.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _cursor = MutableStateFlow<String?>(null)
    val cursor = _cursor.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            if (_postsInitiallyLoaded.value)
                return@launch

            _posts.value = fetchPosts()

            _postsInitiallyLoaded.value = true
        }
    }

    fun refreshPosts(callback: () -> Unit) {
        viewModelScope.launch {
            _refreshing.value = true

            _posts.value = fetchPosts()

            _refreshing.value = false

            callback()
        }
    }

    private suspend fun fetchPosts(): List<BskyFeedViewPost> {
        val postUris: HashSet<String> = HashSet()

        // We filter here because there are cases where the PDS
        // will provide us the post as parent/main, and another
        // where the same post is just the main post. So we
        // filter out the items that meet the second case.
        return App.atProtoClient.getHomeTimeline(
            BskyGetTimelineInput(limit = 100)
        ).feed.filter { !(!postUris.add(it.post.uri) && it.reply?.parent == null) }
    }
}
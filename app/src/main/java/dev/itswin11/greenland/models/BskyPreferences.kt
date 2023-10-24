package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

class BskyPreferencesModel(preferences: BskyPreferences) {
    private var _savedFeeds: SavedFeedPrefs = SavedFeedPrefs(emptyList(), emptyList())
    private var _adultContentPrefs: AdultContentPrefs = AdultContentPrefs(false)
    private var _contentLabelPrefs: ContentLabelPrefs = ContentLabelPrefs("", "")
    private var _personalDetailsPrefs: PersonalDetailsPrefs = PersonalDetailsPrefs()
    private var _feedViewPrefs: FeedViewPrefs = FeedViewPrefs("")
    private var _threadViewPrefs: ThreadViewPrefs = ThreadViewPrefs()

    val savedFeeds: SavedFeedPrefs get() {
        return _savedFeeds
    }
    val adultContentPrefs: AdultContentPrefs get() {
        return _adultContentPrefs
    }
    val contentLabelPrefs: ContentLabelPrefs get() {
        return _contentLabelPrefs
    }
    val personalDetailsPrefs: PersonalDetailsPrefs get() {
        return _personalDetailsPrefs
    }
    val feedViewPrefs: FeedViewPrefs get() {
        return _feedViewPrefs
    }
    val threadViewPrefs: ThreadViewPrefs get() {
        return _threadViewPrefs
    }

    init {
        val serializer = Json { ignoreUnknownKeys = true }
        for (item: JsonObject in preferences.preferences) {
            when (item["\$type"]?.jsonPrimitive?.content) {
                "app.bsky.actor.defs#adultContentPref" -> {
                    _adultContentPrefs = serializer.decodeFromJsonElement(item)
                }
                "app.bsky.actor.defs#contentLabelPref" -> {
                    _contentLabelPrefs = serializer.decodeFromJsonElement(item)
                }
                "app.bsky.actor.defs#savedFeedsPref" -> {
                    _savedFeeds = serializer.decodeFromJsonElement(item)
                }
                "app.bsky.actor.defs#personalDetailsPref" -> {
                    _personalDetailsPrefs = serializer.decodeFromJsonElement(item)
                }
                "app.bsky.actor.defs#feedViewPref" -> {
                    _feedViewPrefs = serializer.decodeFromJsonElement(item)
                }
                "app.bsky.actor.defs#threadViewPref" -> {
                    _threadViewPrefs = serializer.decodeFromJsonElement(item)
                }
            }
        }
    }
}

@Serializable
data class BskyPreferences(
    val preferences: List<JsonObject>
)

@Serializable
data class AdultContentPrefs(
    val enabled: Boolean
)

@Serializable
data class ContentLabelPrefs(
    val label: String,
    val visibility: String // show, hide, warn
)

@Serializable
data class SavedFeedPrefs(
    val saved: List<String>,
    val pinned: List<String>
)

@Serializable
data class PersonalDetailsPrefs(
    val birthDate: String? = null // datetime
)

@Serializable
data class FeedViewPrefs(
    val feed: String, // URI
    val hideReplies: Boolean? = null,
    val hideRepliesByUnfollowed: Boolean? = null,
    val hideRepliesByLikeCount: Int? = null,
    val hideReposts: Boolean? = null,
    val hideQuotePosts: Boolean? = null
)

@Serializable
data class ThreadViewPrefs(
    val sort: String? = null,
    val prioritizeFollowedUsers: Boolean? = null,
)
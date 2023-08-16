package me.wxc.feature.feed

import android.util.Log
import androidx.compose.runtime.Immutable
import me.wxc.mvicore.MviIntent
import me.wxc.mvicore.MviSingleEvent
import me.wxc.mvicore.MviViewState


@Immutable
data class FeedItem(
    val banner: List<Story>? = null,
    val story: Story? = null,
    val date: String? = null
) {
    val id: Int
        get() = story?.id ?: let {
            date?.hashCode()
        } ?: 0
}


@Immutable
sealed interface FeedIntent : MviIntent {
    object Initial : FeedIntent
    object Refresh : FeedIntent
    data class LoadMore(val date: String) : FeedIntent
    data class ClickItem(val item: FeedItem) : FeedIntent
}

@Immutable
data class FeedState(
    val items: List<FeedItem> = emptyList(),
    val error: Throwable? = null,
    val currentDate: String = "",
    val refreshing: Boolean = false,
    val loading: Boolean = false
) : MviViewState {
    companion object {
        fun initial() = FeedState()
    }
}

sealed interface PartialStateChange {
    fun reduce(state: FeedState): FeedState

    sealed interface Refresh : PartialStateChange {
        override fun reduce(state: FeedState): FeedState {
            Log.i("wxc", "reduce refreshing: ${state.refreshing}")
            return when (this) {
                Loading -> state.copy(refreshing = true)
                is Success -> state.copy(
                    items = entity.map2Items(),
                    error = null,
                    refreshing = false
                )

                is Error -> state.copy(error = throwable, refreshing = false)
            }
        }

        object Loading : Refresh
        data class Success(val entity: NewsEntity) : Refresh
        data class Error(val throwable: Throwable) : Refresh
    }

    sealed interface LoadMore : PartialStateChange {
        override fun reduce(state: FeedState): FeedState {
            Log.i("wxc", "reduce loading: ${state.loading}")
            return when (this) {
                Loading -> state.copy(loading = true)
                is Success -> state.copy(
                    items = state.items + entity.map2Items(),
                    error = null,
                    loading = false
                )

                is Error -> state.copy(error = throwable, loading = false)
            }
        }

        object Loading : LoadMore
        data class Success(val entity: NewsEntity) : LoadMore
        data class Error(val throwable: Throwable) : LoadMore
    }
}

sealed interface FeedEffect : MviSingleEvent {
    data class LoadMoreSuccess(val date: String) : FeedEffect
}

fun NewsEntity.map2Items(): List<FeedItem> {
    return mutableListOf<FeedItem>().apply {
        top_stories?.let {
            add(FeedItem(banner = it))
        }
        add(FeedItem(date = date))
        addAll(stories.map { FeedItem(story = it) })
    }
}
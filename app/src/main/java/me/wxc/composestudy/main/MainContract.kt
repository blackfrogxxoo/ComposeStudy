package me.wxc.composestudy.main

import android.util.Log
import androidx.compose.runtime.Immutable
import me.wxc.mvicore.MviIntent
import me.wxc.mvicore.MviSingleEvent
import me.wxc.mvicore.MviViewState


@Immutable
data class MainItem(
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
sealed interface MainIntent : MviIntent {
    object Initial : MainIntent
    object Refresh : MainIntent
    data class LoadMore(val date: String) : MainIntent
}

@Immutable
data class MainState(
    val items: List<MainItem> = emptyList(),
    val error: Throwable? = null,
    val currentDate: String = "",
    val refreshing: Boolean = false,
    val loading: Boolean = false
) : MviViewState {
    companion object {
        fun initial() = MainState()
    }
}

sealed interface PartialStateChange {
    fun reduce(state: MainState): MainState

    sealed interface Refresh : PartialStateChange {
        override fun reduce(state: MainState): MainState {
            Log.i("fuck", "reduce refreshing: ${state.refreshing}")
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
        override fun reduce(state: MainState): MainState {
            Log.i("fuck", "reduce loading: ${state.loading}")
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

sealed interface MainEffect : MviSingleEvent {

}

fun NewsEntity.map2Items(): List<MainItem> {
    return mutableListOf<MainItem>().apply {
        top_stories?.let {
            add(MainItem(banner = it))
        }
        add(MainItem(date = date))
        addAll(stories.map { MainItem(story = it) })
    }
}
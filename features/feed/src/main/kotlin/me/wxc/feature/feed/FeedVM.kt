package me.wxc.feature.feed

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import me.wxc.framework.http.ApiClient
import me.wxc.mvicore.AbstractMviViewModel
import javax.inject.Inject

@HiltViewModel
class FeedVM @Inject constructor(
    private val apiClient: ApiClient
) : AbstractMviViewModel<FeedIntent, FeedState, FeedEffect>() {
    override val viewState: StateFlow<FeedState>

    init {
        val initialState = FeedState.initial()
        viewState = merge(
            intentFlow.filterIsInstance<FeedIntent.Initial>().take(1),
            intentFlow.filterIsInstance<FeedIntent.LoadMore>().distinctUntilChanged(),
            intentFlow.filterNot { it is FeedIntent.Initial || it is FeedIntent.LoadMore }
        ).shareWhileSubscribed()
            .toPartialStateChange()
            .sendEvent()
            .scan(initialState) { vs, change -> change.reduce(vs) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }

    private fun SharedFlow<FeedIntent>.toPartialStateChange(): Flow<PartialStateChange> {
        return merge(
            filterIsInstance<FeedIntent.Initial>().toInitialStateChange(),
            filterIsInstance<FeedIntent.Refresh>().toInitialStateChange(),
            filterIsInstance<FeedIntent.LoadMore>().toLoadMoreStateChange()
        )
    }

    private fun Flow<FeedIntent>.toInitialStateChange(): Flow<PartialStateChange.Refresh> {
        val changes = flow {
            val result = apiClient.get<NewsEntity>("/api/4/news/latest", NewsEntity::class)
            emit(result)
        }.map { it ->
            it.fold(
                onSuccess = {
                    PartialStateChange.Refresh.Success(it)
                },
                onFailure = {
                    PartialStateChange.Refresh.Error(it)
                }
            )
        }.onStart {
            emit(PartialStateChange.Refresh.Loading)
        }
        return changes
    }

    private fun Flow<FeedIntent.LoadMore>.toLoadMoreStateChange(): Flow<PartialStateChange.LoadMore> {
        val changes = map { it.date }.map {
            apiClient.get<NewsEntity>("/api/4/news/before/$it", NewsEntity::class)
        }.map { it ->
            it.fold(
                onSuccess = {
                    PartialStateChange.LoadMore.Success(it)
                },
                onFailure = {
                    PartialStateChange.LoadMore.Error(it)
                }
            )
        }.onStart { PartialStateChange.LoadMore.Loading }
        return changes
    }

    private fun Flow<PartialStateChange>.sendEvent(): SharedFlow<PartialStateChange> {
        return onEach {
            when (it) {
                is PartialStateChange.LoadMore.Success -> {
                    sendEvent(FeedEffect.LoadMoreSuccess(it.entity.date))
                }
                else -> {
                    // do nothing
                }
            }
        }.shareWhileSubscribed()
    }
}
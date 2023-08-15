package me.wxc.composestudy.main

import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import me.wxc.composestudy.Http
import me.wxc.composestudy.baseUrl
import me.wxc.mvicore.AbstractMviViewModel
import me.wxc.mvicore.collectIn

class MainVM : AbstractMviViewModel<MainIntent, MainState, MainEffect>() {
    override val viewState: StateFlow<MainState>

    init {
        val initialState = MainState.initial()
        viewState = merge(
            intentFlow.filterIsInstance<MainIntent.Initial>().take(1),
            intentFlow.filterNot { it is MainIntent.Initial }
        ).shareWhileSubscribed()
            .toPartialStateChange()
            .scan(initialState) { vs, change -> change.reduce(vs) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }

    private fun SharedFlow<MainIntent>.toPartialStateChange(): Flow<PartialStateChange> {
        return merge(
            filterIsInstance<MainIntent.Initial>().toInitialStateChange(),
            filterIsInstance<MainIntent.Refresh>().toInitialStateChange(),
            filterIsInstance<MainIntent.LoadMore>().toLoadMoreStateChange()
        )
    }

    private fun Flow<MainIntent>.toInitialStateChange(): Flow<PartialStateChange.Refresh> {
        val changes = flow {
            val entity: NewsEntity = Http.client.get("${baseUrl}latest").body()
            emit(Result.success(entity))
        }.catch {
            emit(Result.failure(it))
        }.map { it ->
            it.fold(
                onSuccess = {
                    PartialStateChange.Refresh.Success(it)
                },
                onFailure = {
                    PartialStateChange.Refresh.Error(it)
                }
            )
        }
        return merge(
            flow {
                emit(0)
            }.map {
                PartialStateChange.Refresh.Loading
            },
            changes
        )
    }

    private fun Flow<MainIntent.LoadMore>.toLoadMoreStateChange(): Flow<PartialStateChange.LoadMore> {
        val changes = map { it.date }.map {
            Http.client.get("${baseUrl}before/$it").body() as NewsEntity
        }.catch {
            PartialStateChange.LoadMore.Error(it)
        }.map {
            PartialStateChange.LoadMore.Success(it)
        }.onStart { PartialStateChange.LoadMore.Loading }
        return changes
    }
}
package me.wxc.composestudy

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.wxc.composestudy.main.MainEffect
import me.wxc.composestudy.main.MainIntent
import me.wxc.composestudy.main.MainState
import me.wxc.composestudy.main.MainVM
import me.wxc.composestudy.ui.theme.ComposeStudyTheme
import me.wxc.mvicore.AbstractMviActivity
import me.wxc.pullrefresh.PullRefreshIndicator
import me.wxc.pullrefresh.pullRefresh
import me.wxc.pullrefresh.rememberPullRefreshState

class MainActivity : AbstractMviActivity<MainIntent, MainState, MainEffect, MainVM>() {
    override val vm: MainVM = MainVM()
    private val intentChannel = Channel<MainIntent>(Channel.UNLIMITED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupViews() {

    }

    override fun viewIntents(): Flow<MainIntent> = merge(
        intentChannel.consumeAsFlow().onStart { MainIntent.Initial }.onEach(vm::processIntent)
    )

    override fun handleSingleEvent(event: MainEffect) {

    }

    override fun render(viewState: MainState) {
        onLoaded(viewState)
    }

    private fun onLoaded(state: MainState) {
        Log.i("fuck", "onLoaded: ${state.refreshing}, ${state.loading}, ${state.error}")
        setContent {
            ComposeStudyTheme {
                val lastState = remember {
                    state
                }
                Log.i("fuck", "onLoaded: remembered: ${lastState.refreshing}")
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = state.refreshing,
                    onRefresh = {
                        Log.i("fuck", "refresh")
                        intentChannel.trySend(MainIntent.Refresh)
                    })

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {

                    when {
                        state.refreshing -> Loading()
                        state.error != null -> LoadError(intentChannel)
                        state.items.isNotEmpty() -> StoryList(this@MainActivity, state) {
                            lifecycleScope.launch {
                                vm.processIntent(MainIntent.LoadMore(state.items.mapNotNull { it.date }.last()))
                            }
//                            intentChannel.trySend(MainIntent.LoadMore(state.items.mapNotNull { it.date }.last()))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = state.refreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun StoryList(context: Context, state: MainState, onLoadMore: () -> Unit) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(state.items, key = { item -> item.id }) { item ->
                if (item.id == state.items.last().id) {
                    onLoadMore()
                }
                when {
                    item.banner != null -> {
                        HorizontalPager(pageCount = item.banner.size, key = {
                            item.banner[it].id
                        }) {
                            AsyncImage(
                                model = item.banner[it].image ?: "",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                    }

                    item.date != null -> {
                        Text(text = item.date)
                    }

                    item.story != null -> {
                        Row(
                            modifier = Modifier
                                .clickable { WebActivity.browse(context, item.story.url) }
                                .fillParentMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 6.dp)
                        ) {
                            AsyncImage(
                                model = item.story.images?.get(0) ?: "",
                                contentDescription = item.story.title,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                            )
                            Text(
                                text = item.story.title,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun Loading() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "loading", modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
        }
    }

    @Composable
    fun LoadError(intentChannel: Channel<MainIntent>) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Button(onClick = {
                lifecycleScope.launch {
                    Log.i("fuck", "click refresh")
                    intentChannel.trySend(MainIntent.Refresh)
                }
            }) {
                Text(
                    text = "Click Retry", modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
        }
    }
}

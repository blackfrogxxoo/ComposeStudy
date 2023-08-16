package me.wxc.feature.feed

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import me.wxc.pullrefresh.PullRefreshIndicator
import me.wxc.pullrefresh.pullRefresh
import me.wxc.pullrefresh.rememberPullRefreshState

@Composable
fun FeedListScreen(
    onItemClick: Story.() -> Unit = {},
    vm: FeedVM = hiltViewModel()
) {
    val context = LocalContext.current
    val state by vm.viewState.collectAsStateWithLifecycle()
    val intentChannel = remember {
        Channel<FeedIntent>(Channel.UNLIMITED)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            intentChannel
                .consumeAsFlow()
                .onStart { emit(FeedIntent.Initial) }
                .onEach(vm::processIntent)
                .collect()
        }
    }
    vm.singleEvent.collectInLaunchedEffectWithLifecycle { event ->
        when (event) {
            is FeedEffect.LoadMoreSuccess -> {
                Toast.makeText(context, "${event.date} loaded", Toast.LENGTH_SHORT).show()
            }
        }
    }
    Log.i("wxc", "onLoaded: remembered: ${state.refreshing}")
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.refreshing,
        onRefresh = {
            Log.i("wxc", "refresh")
            intentChannel.trySend(FeedIntent.Refresh)
        })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        when {
            state.refreshing -> Loading()
            state.error != null -> LoadError(intentChannel)
            state.items.isNotEmpty() -> StoryList(state, onItemClick) {
                intentChannel.trySend(
                    FeedIntent.LoadMore(state.items.mapNotNull { it.date }.last())
                )
            }
        }

        PullRefreshIndicator(
            refreshing = state.refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun StoryList(
    state: FeedState,
    onItemClick: Story.() -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(state.items, key = { item -> item.id }) { item ->
            if (item.id == state.items.last().id) {
                onLoadMore()
            }
            when {
                item.banner != null -> {
                    val configuration = LocalConfiguration.current
                    BannerItem(
                        item = item,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .requiredHeight(configuration.screenWidthDp.dp),
                        onItemClick = onItemClick
                    )
                }

                item.date != null -> {
                    Text(text = item.date)
                }

                item.story != null -> {
                    FeedItem(
                        item = item,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .clickable { onItemClick.invoke(item.story) }
                            .padding(horizontal = 15.dp, vertical = 6.dp),
                    )
                }
            }

        }
    }
}


@Composable
fun FeedItem(item: FeedItem, modifier: Modifier) {
    check(item.story != null)
    Row(modifier) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerItem(item: FeedItem, modifier: Modifier, onItemClick: Story.() -> Unit) {
    check(item.banner != null)
    HorizontalPager(
        modifier = modifier,
        pageCount = item.banner.size, key = {
            item.banner[it].id
        }) {
        AsyncImage(
            model = item.banner[it].image ?: "",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick.invoke(item.banner[it]) }
        )
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
fun LoadError(intentChannel: Channel<FeedIntent>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(onClick = {
            intentChannel.trySend(FeedIntent.Refresh)
        }) {
            Text(
                text = "Click Retry", modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
        }
    }
}
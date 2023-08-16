package me.wxc.composestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import me.wxc.composestudy.ui.theme.ComposeStudyTheme
import me.wxc.feature.feed.FeedListScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStudyTheme {
                FeedListScreen(onItemClick = {
                    WebActivity.browse(this@MainActivity, url)
                })
            }
        }
    }
}

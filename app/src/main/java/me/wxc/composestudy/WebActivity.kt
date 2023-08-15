package me.wxc.composestudy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.wxc.composestudy.ui.theme.ComposeStudyTheme

class WebActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url") ?: ""
        setContent {
            ComposeStudyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            loadUrl(url)
                        }
                    })
                }
            }
        }
    }

    companion object {
        fun browse(context: Context, url: String) {
            Intent(context, WebActivity::class.java).apply {
                putExtra("url", url)
                context.startActivity(this)
            }
        }
    }
}
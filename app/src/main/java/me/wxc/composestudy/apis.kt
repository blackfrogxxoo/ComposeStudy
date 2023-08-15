package me.wxc.composestudy

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


// 最新： https://news-at.zhihu.com/api/4/news/latest
// 历史： https://news-at.zhihu.com/api/4/news/before/20191005

const val baseUrl = "https://news-at.zhihu.com/api/4/news/"

object Http {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
}
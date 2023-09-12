package me.wxc.feature.feed

import me.wxc.framework.http.ApiClient
import me.wxc.framework.http.get
import javax.inject.Inject

class FeedApi @Inject constructor(
    private val apiClient: ApiClient
) {
    suspend fun latest() = apiClient.get<NewsEntity>("/api/4/news/latest")
    suspend fun before(date: String): Result<NewsEntity> =
        apiClient.get("/api/4/news/before/$date")
}
package me.wxc.framework.http

interface ApiClient {
    val baseUrl: String
    suspend fun <T> get(path: String) : Result<T>
}
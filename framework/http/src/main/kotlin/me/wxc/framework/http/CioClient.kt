package me.wxc.framework.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CioClient @Inject constructor() : ApiClient {
    override val baseUrl: String = "https://news-at.zhihu.com/api/4/news/"
    val _client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun <T> get(path: String): Result<T> {
        _client.get("$baseUrl$path").body<String>()
        return Result.failure(IllegalStateException(""))
    }

    suspend inline fun <reified T> performGet(path: String): Result<T> {
        return try {
            val response = _client.get("$baseUrl$path")
            Result.success(response.body() as T)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend inline fun <reified T> post(path: String, params: Map<String, Any?>): Result<T> {
        return try {
            val response = _client.post("$baseUrl$path") {
                params.forEach { (t, u) -> parameter(t, u) }
            }
            Result.success(response.body() as T)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
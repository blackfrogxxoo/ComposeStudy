package me.wxc.framework.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.reflect.KClass

class CioClient @Inject constructor() : ApiClient {
    override val baseUrl: String = "news-at.zhihu.com"
    private val clientInstance = HttpClient(CIO) {
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun <T> get(path: String, kClass: KClass<*>): Result<T> {
        return runCatching {
            clientInstance.get(path).body(TypeInfo(kClass, kClass.java))
        }
    }

    override suspend fun <T> post(
        path: String,
        params: Map<String, Any?>,
        kClass: KClass<*>
    ): Result<T> {
        return runCatching {
            clientInstance.post(path) {
                params.forEach { (t, u) -> parameter(t, u) }
            }.body(TypeInfo(kClass, kClass.java))
        }
    }
}
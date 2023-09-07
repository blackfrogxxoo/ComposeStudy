package me.wxc.framework.http

import kotlin.reflect.KClass

interface ApiClient {
    val baseUrl: String
    suspend fun <T> get(path: String, kClass: KClass<*>): Result<T>
    suspend fun <T> post(path: String, params: Map<String, Any?>, kClass: KClass<*>): Result<T>
}
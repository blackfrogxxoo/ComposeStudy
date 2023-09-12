package me.wxc.framework.http

suspend inline fun <reified T> ApiClient.get(path: String) : Result<T> {
    return get(path, T::class)
}

suspend inline fun <reified T> ApiClient.post(path: String, params: Map<String, Any?>) : Result<T> {
    return post(path, params, T::class)
}
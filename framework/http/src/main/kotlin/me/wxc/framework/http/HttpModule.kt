package me.wxc.framework.http

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HttpModule {
    @Binds
    @Singleton
    abstract fun bindHttp(client: CioClient) : ApiClient
}
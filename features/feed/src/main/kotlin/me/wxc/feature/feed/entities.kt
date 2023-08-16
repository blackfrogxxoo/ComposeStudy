package me.wxc.feature.feed

import kotlinx.serialization.Serializable


@Serializable
data class NewsEntity(
    val date: String,
    val stories: List<Story>,
    val top_stories: List<Story>? = null,
)

@Serializable
data class Story(
    val ga_prefix: String,
    val hint: String,
    val id: Int,
    val image_hue: String,
    val images: List<String>? = null,
    val image: String? = null,
    val title: String,
    val type: Int,
    val url: String
)
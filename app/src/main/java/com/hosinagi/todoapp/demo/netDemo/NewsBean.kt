package com.hosinagi.todoapp.demo.netDemo

data class NewsBean(
    val date: String,
    val display_date: String,
    val is_today: Boolean,
    val news: List<New>,
    val top_stories: List<TopStory>
)

data class New(
    val ga_prefix: String,
    val hint: String,
    val id: Int,
    val image: String,
    val image_hue: String,
    val share_url: String,
    val thumbnail: String,
    val title: String,
    val url: String
)

data class TopStory(
    val ga_prefix: String,
    val hint: String,
    val id: Int,
    val image: String,
    val image_hue: String,
    val image_source: String,
    val share_url: String,
    val title: String,
    val url: String
)

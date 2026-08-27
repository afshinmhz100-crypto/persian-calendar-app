package com.example.calendar.news

data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val category: String, // "علم و فناوری", "فرهنگی و اجتماعی", "اقتصادی", "ورزشی", "رویدادها"
    val sourceName: String,
    val publishTimeAgo: String,
    val timestamp: Long,
    val url: String = ""
)

sealed class NewsState {
    object Idle : NewsState()
    object Loading : NewsState()
    data class Success(val newsList: List<NewsItem>, val lastUpdatedTimestamp: Long, val isOfflineCache: Boolean = false) : NewsState()
    data class Error(val message: String, val cachedNews: List<NewsItem>? = null) : NewsState()
}

package com.dicoding.asclepius.data.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @field:SerializedName("articles")
    val articles: List<ArticlesItem?>? = null,
)

data class ArticlesItem(

    @field:SerializedName("urlToImage")
    val urlToImage: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null,
)

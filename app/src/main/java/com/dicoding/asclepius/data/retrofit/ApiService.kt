package com.dicoding.asclepius.data.retrofit

import com.dicoding.asclepius.data.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("top-headlines?country=id&category=health&q=kanker")
    fun getNews(): Call<NewsResponse>
}
package com.dicoding.asclepius.data.retrofit

import com.dicoding.asclepius.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
        private const val API_KEY = BuildConfig.API_KEY

        fun getApiService(): ApiService {
            val httpClient = createHttpClient()
            val retrofit = createRetrofit(httpClient)
            return retrofit.create(ApiService::class.java)
        }

        private fun createHttpClient(): OkHttpClient {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(TokenInterceptor())
                .build()
        }

        private fun createRetrofit(httpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

        class TokenInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest: Request = chain.request()
                val originalHttpUrl = originalRequest.url
                val url =
                    originalHttpUrl.newBuilder().addQueryParameter("apikey", API_KEY)
                        .build()
                val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
                val request: Request = requestBuilder.build()
                return chain.proceed(request)
            }
        }
    }
}


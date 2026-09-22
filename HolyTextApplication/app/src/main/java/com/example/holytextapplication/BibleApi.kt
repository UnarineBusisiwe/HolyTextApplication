package com.example.holytextapplication

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class BibleResponse(
    val reference: String?,
    val verses: List<Verse>?,
    val text: String?
)

data class Verse(
    val book_name: String?,
    val chapter: Int?,
    val verse: Int?,
    val text: String?
)

interface BibleApiService {
    @GET("{passage}")
    suspend fun getPassage(
        @Path("passage", encoded = true) passage: String,
        @Query("translation") translation: String = "kjv"
    ): BibleResponse
}

object BibleApiClient {
    private const val BASE_URL = "https://bible-api.com/"

    val service: BibleApiService by lazy {
        val logging = HttpLoggingInterceptor { msg ->
            android.util.Log.d("BibleApiHTTP", msg)
        }.apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BibleApiService::class.java)
    }
}
package com.example.rednotetranslator.translation

import retrofit2.http.Body
import retrofit2.http.POST

interface TranslationApi {
    @POST("translate")
    suspend fun translate(@Body request: TranslationRequest): TranslationResponse
}

data class TranslationRequest(
    val q: String,
    val source: String = "zh",
    val target: String = "en"
)

data class TranslationResponse(
    val translatedText: String
) 
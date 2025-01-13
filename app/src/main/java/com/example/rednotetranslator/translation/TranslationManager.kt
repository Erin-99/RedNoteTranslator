package com.example.rednotetranslator.translation

import android.content.Context
import androidx.room.Room
import com.example.rednotetranslator.db.Translation
import com.example.rednotetranslator.db.TranslationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TranslationManager(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        TranslationDatabase::class.java,
        "translations.db"
    ).build()

    private val api = Retrofit.Builder()
        .baseUrl("https://libretranslate.de/") // 使用公共LibreTranslate服务
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TranslationApi::class.java)

    suspend fun translate(text: String): String = withContext(Dispatchers.IO) {
        // 先查询缓存
        db.translationDao().getTranslation(text)?.let {
            return@withContext it.translatedText
        }

        try {
            // 调用API翻译
            val response = api.translate(TranslationRequest(q = text))
            
            // 保存到缓存
            db.translationDao().insertTranslation(
                Translation(
                    originalText = text,
                    translatedText = response.translatedText
                )
            )
            
            response.translatedText
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }
} 
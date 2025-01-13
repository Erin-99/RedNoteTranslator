package com.example.rednotetranslator.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Entity(tableName = "translations")
data class Translation(
    @PrimaryKey val originalText: String,
    val translatedText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations WHERE originalText = :text")
    suspend fun getTranslation(text: String): Translation?

    @Insert
    suspend fun insertTranslation(translation: Translation)
}

@Database(entities = [Translation::class], version = 1)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
} 
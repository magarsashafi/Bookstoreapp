package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val description: String,
    val coverColorHex: String,
    val pagesCount: Int,
    val priceEtb: Double, // 0.0 means free (Tola)
    val rating: Float = 4.8f,
    val reviewsCount: Int = 124,
    val isDownloaded: Boolean = false,
    val fileSizeMb: Double = 3.5,
    val isPurchased: Boolean = false, // Purchased or free
    val isFavorite: Boolean = false,
    val isUserListed: Boolean = false, // Created by user to sell
    val currentPage: Int = 1,
    val lastReadTimestamp: Long = 0L,
    val content: String // Chapters and body text
)

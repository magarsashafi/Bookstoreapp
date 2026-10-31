package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String,
    val chapterTitle: String,
    val pageNumber: Int,
    val noteText: String,
    val quoteText: String,
    val createdAt: Long = System.currentTimeMillis()
)

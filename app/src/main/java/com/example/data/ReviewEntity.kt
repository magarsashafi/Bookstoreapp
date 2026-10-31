package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val userId: String,
    val userName: String,
    val rating: Int, // 1 to 5
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis()
)

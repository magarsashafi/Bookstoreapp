package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val bio: String,
    val avatarColorHex: String = "#8C2D19",
    val favoriteGenre: String = "Asoosama",
    val isLoggedIn: Boolean = true
)

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class SaleTransactionEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val amountEtb: Double,
    val buyerName: String,
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis(),
    val transactionRef: String
)

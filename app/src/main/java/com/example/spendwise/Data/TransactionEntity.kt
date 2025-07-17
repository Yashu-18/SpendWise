package com.example.spendwise.Data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val category: String,
    val date: Long,

    // Recurring expense settings
    val isRecurring: Boolean = false,
    val recurrenceInterval: Int? = null,
    val recurrenceUnit: String? = null,
    val nextOccurrence: Long? = null
)

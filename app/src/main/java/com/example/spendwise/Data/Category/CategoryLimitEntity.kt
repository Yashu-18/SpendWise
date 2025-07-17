package com.example.spendwise.Data.Category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_limits")
data class CategoryLimitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val timeSpan: String // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
)
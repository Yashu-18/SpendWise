package com.example.spendwise.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spendwise.Data.Category.CategoryLimitDao
import com.example.spendwise.Data.Category.CategoryLimitEntity



@Database(entities = [TransactionEntity::class, CategoryLimitEntity::class], version = 1, exportSchema = false)
abstract class SpendWiseDatabase : RoomDatabase() {

    // DAOs for the entities
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryLimitDao(): CategoryLimitDao

    companion object {
        @Volatile
        private var INSTANCE: SpendWiseDatabase? = null

        fun getDatabase(context: Context): SpendWiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendWiseDatabase::class.java,
                    "spendwise_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


package com.example.spendwise.Data.Category


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryLimitDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryLimit(categoryLimit: CategoryLimitEntity)


    @Update
    suspend fun updateCategoryLimit(categoryLimit: CategoryLimitEntity)


    @Query("SELECT * FROM category_limits WHERE category = :category")
    fun getCategoryLimit(category: String): LiveData<CategoryLimitEntity>


    @Delete
    suspend fun deleteCategoryLimit(categoryLimit: CategoryLimitEntity)
}


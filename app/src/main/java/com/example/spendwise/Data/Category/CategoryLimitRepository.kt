package com.example.spendwise.Data.Category

import androidx.lifecycle.LiveData


class CategoryLimitRepository(private val categoryLimitDao: CategoryLimitDao) {

    // Insert a category limit
    suspend fun insertCategoryLimit(categoryLimit: CategoryLimitEntity) {
        categoryLimitDao.insertCategoryLimit(categoryLimit)
    }

    // Update a category limit
    suspend fun updateCategoryLimit(categoryLimit: CategoryLimitEntity) {
        categoryLimitDao.updateCategoryLimit(categoryLimit)
    }

    // Get a specific category limit by category
    fun getCategoryLimit(category: String): LiveData<CategoryLimitEntity> {
        return categoryLimitDao.getCategoryLimit(category)
    }

    // Delete a category limit
    suspend fun deleteCategoryLimit(categoryLimit: CategoryLimitEntity) {
        categoryLimitDao.deleteCategoryLimit(categoryLimit)
    }
}

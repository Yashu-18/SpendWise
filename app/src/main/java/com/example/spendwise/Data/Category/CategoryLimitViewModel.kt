package com.example.spendwise.Data.Category



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.spendwise.Data.SpendWiseDatabase
import kotlinx.coroutines.launch

class CategoryLimitViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryLimitRepository: CategoryLimitRepository


    init {
        val categoryLimitDao = SpendWiseDatabase.getDatabase(application).categoryLimitDao()
        val transitionDao = SpendWiseDatabase.getDatabase(application).transactionDao()
        categoryLimitRepository = CategoryLimitRepository(categoryLimitDao)
    }


    // Get category limit by category name
    fun getCategoryLimit(category: String): LiveData<CategoryLimitEntity> {
        return categoryLimitRepository.getCategoryLimit(category)
    }

    // Insert a new category limit
    fun insertCategoryLimit(categoryLimit: CategoryLimitEntity) = viewModelScope.launch {
        categoryLimitRepository.insertCategoryLimit(categoryLimit)
    }

    // Update an existing category limit
    fun updateCategoryLimit(categoryLimit: CategoryLimitEntity) = viewModelScope.launch {
        categoryLimitRepository.updateCategoryLimit(categoryLimit)
    }

    // Delete a category limit
    fun deleteCategoryLimit(categoryLimit: CategoryLimitEntity) = viewModelScope.launch {
        categoryLimitRepository.deleteCategoryLimit(categoryLimit)
    }
}

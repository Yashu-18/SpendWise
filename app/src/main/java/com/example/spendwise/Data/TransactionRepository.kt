package com.example.spendwise.Data

import androidx.lifecycle.LiveData
import com.example.spendwise.UI.Models.CategoryExpense
import com.example.spendwise.UI.Models.GroupedExpense
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: LiveData<List<TransactionEntity>> = dao.getAllTransactions()
    val recurringTransactions: LiveData<List<TransactionEntity>> = dao.getRecurringTransactions()
    val nonrecurringTransaction : LiveData<List<TransactionEntity>> = dao.getNonRecurringTransactions()
    val getTotalIncome : LiveData<Int> = dao.getTotalIncome()
    val getTotalExpense : LiveData<Int> = dao.getTotalExpense()

    fun getGroupedExpenses(start: Long, end: Long, format: String): LiveData<List<GroupedExpense>> =
        dao.getGroupedExpenses(start, end, format)

    // In your Repository
    suspend fun getExpensesByCategory(): List<CategoryExpense> {
        return dao.getExpensesByCategory()
    }



    suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)
    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    suspend fun deleteAll() = dao.deleteAll()
}


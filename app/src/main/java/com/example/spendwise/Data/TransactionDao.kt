package com.example.spendwise.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.spendwise.UI.Models.CategoryExpense
import com.example.spendwise.UI.Models.GroupedExpense
import java.time.LocalDate

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<TransactionEntity>>


    @Query("SELECT SUM(amount) FROM transactions WHERE category = 'Income'")
    fun getTotalIncome(): LiveData<Int>



    @Query("SELECT SUM(amount) FROM transactions WHERE category != 'Income'")
    fun getTotalExpense(): LiveData<Int>

    @Query("SELECT * FROM transactions WHERE isRecurring = 1")
    fun getRecurringTransactions(): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isRecurring = 0")
    fun getNonRecurringTransactions(): LiveData<List<TransactionEntity>>

    @Query("""
    SELECT 
        strftime(:groupFormat, date / 1000, 'unixepoch') AS timeGroup,
        SUM(amount) AS totalExpense
    FROM transactions
    WHERE date BETWEEN :startDate AND :endDate
    AND category != 'Income'
    GROUP BY timeGroup
    ORDER BY timeGroup ASC
""")
    fun getGroupedExpenses(
        startDate: Long,
        endDate: Long,
        groupFormat: String
    ): LiveData<List<GroupedExpense>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions WHERE category != 'Income' GROUP BY category")
    suspend fun getExpensesByCategory(): List<CategoryExpense>

}



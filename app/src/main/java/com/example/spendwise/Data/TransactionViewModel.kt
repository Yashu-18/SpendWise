package com.example.spendwise.Data

import android.app.Application
import androidx.lifecycle.*
import com.example.spendwise.UI.Models.CategoryExpense
import com.example.spendwise.UI.Models.ExpenseChartData
import com.example.spendwise.UI.Models.GroupedExpense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val _chartState = MutableLiveData<ChartState>(ChartState.Loading)
    val chartState: LiveData<ChartState> = _chartState

    sealed class ChartState {
        object Loading : ChartState()
        data class Success(val data: List<ExpenseChartData>) : ChartState()
        data class Error(val message: String) : ChartState()
    }

    enum class TimeFilter(val groupFormat: String, val range: Int) {
        DAY("%Y-%m-%d", 7),
        WEEK("%Y-%W", 6),
        MONTH("%Y-%m", 12),
        YEAR("%Y", 5)
    }

    init {
        val dao = SpendWiseDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    // Existing LiveData properties
    val allTransactions = repository.allTransactions
    val recurringTransactions = repository.recurringTransactions
    val nonrecurringTransactions = repository.nonrecurringTransaction
    val getTotalIncome = repository.getTotalIncome
    val getTotalExpense = repository.getTotalExpense

    // ViewModel
    fun loadCategoryExpenses(): LiveData<List<CategoryExpense>> {
        val result = MutableLiveData<List<CategoryExpense>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Directly use repository result (DAO already filtered)
                result.postValue(repository.getExpensesByCategory())
            } catch (e: Exception) {
                result.postValue(emptyList())
            }
        }
        return result
    }



    // New chart data loading function
    fun loadGroupedExpenses(filter: TimeFilter) {
        viewModelScope.launch {
            _chartState.value = ChartState.Loading
            try {
                val (startDate, endDate) = calculateDateRange(filter)

                // Fetch grouped expenses and observe LiveData
                repository.getGroupedExpenses(startDate, endDate, filter.groupFormat).observeForever { rawData ->
                    val filledData = fillMissingTimeUnits(rawData, filter)
                    val chartData = filledData.map {
                        ExpenseChartData(
                            label = formatLabel(it.timeGroup, filter),
                            amount = it.totalExpense
                        )
                    }
                    _chartState.value = ChartState.Success(chartData)
                }
            } catch (e: Exception) {
                _chartState.value = ChartState.Error("Failed to load data: ${e.message}")
            }
        }
    }

    private fun calculateDateRange(filter: TimeFilter): Pair<Long, Long> {
        val endDate = LocalDate.now().atTime(23, 59, 59)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        val startDate = when (filter) {
            TimeFilter.DAY -> LocalDate.now().minusDays(filter.range.toLong() - 1)
            TimeFilter.WEEK -> LocalDate.now().minusWeeks(filter.range.toLong() - 1)
            TimeFilter.MONTH -> LocalDate.now().minusMonths(filter.range.toLong() - 1)
            TimeFilter.YEAR -> LocalDate.now().minusYears(filter.range.toLong() - 1)
        }.atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        return Pair(startDate, endDate)
    }

    private fun fillMissingTimeUnits(
        rawData: List<GroupedExpense>,
        filter: TimeFilter
    ): List<GroupedExpense> {
        val allTimeUnits = generateAllTimeUnits(filter)
        return allTimeUnits.map { timeUnit ->
            rawData.find { it.timeGroup == timeUnit } ?: GroupedExpense(timeUnit, 0.0)
        }
    }

    private fun generateAllTimeUnits(filter: TimeFilter): List<String> {
        return (0 until filter.range).map { i ->
            when (filter) {
                TimeFilter.DAY -> LocalDate.now().minusDays(i.toLong())
                TimeFilter.WEEK -> LocalDate.now().minusWeeks(i.toLong())
                TimeFilter.MONTH -> LocalDate.now().minusMonths(i.toLong())
                TimeFilter.YEAR -> LocalDate.now().minusYears(i.toLong())
            }.let { date ->
                when (filter) {
                    TimeFilter.DAY -> date.format(DateTimeFormatter.ISO_DATE)
                    TimeFilter.WEEK -> date.format(DateTimeFormatter.ofPattern("yyyy-ww"))
                    TimeFilter.MONTH -> date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    TimeFilter.YEAR -> date.format(DateTimeFormatter.ofPattern("yyyy"))
                }
            }
        }.reversed()
    }

    private fun formatLabel(timeGroup: String, filter: TimeFilter): String {
        return when (filter) {
            TimeFilter.DAY -> LocalDate.parse(timeGroup).format(DateTimeFormatter.ofPattern("MMM dd"))
            TimeFilter.WEEK -> "Week ${timeGroup.split("-")[1]}"
            TimeFilter.MONTH -> LocalDate.parse("$timeGroup-01").format(DateTimeFormatter.ofPattern("MMM yyyy"))
            TimeFilter.YEAR -> timeGroup
        }
    }

    // Existing CRUD operations
    fun insert(transaction: TransactionEntity) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun delete(transaction: TransactionEntity) = viewModelScope.launch {
        repository.delete(transaction)
    }

    fun update(transaction: TransactionEntity) = viewModelScope.launch {
        repository.update(transaction)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getBalance(): LiveData<Int> {
        val balanceLiveData = MediatorLiveData<Int>()
        balanceLiveData.addSource(getTotalIncome) { income ->
            val expense = getTotalExpense.value ?: 0
            balanceLiveData.value = (income ?: 0) - expense
        }
        balanceLiveData.addSource(getTotalExpense) { expense ->
            val income = getTotalIncome.value ?: 0
            balanceLiveData.value = income - (expense ?: 0)
        }
        return balanceLiveData
    }
}

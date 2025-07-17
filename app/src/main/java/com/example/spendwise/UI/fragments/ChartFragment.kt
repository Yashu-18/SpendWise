package com.example.spendwise.UI.fragments

import android.graphics.Color
import com.github.mikephil.charting.data.PieData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.spendwise.Data.TransactionViewModel
import com.example.spendwise.R
import com.example.spendwise.UI.Models.CategoryExpense
import com.example.spendwise.UI.Models.ExpenseChartData
import com.example.spendwise.databinding.FragmentChartBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // Initialize chart with default day view
        setupChart()
        setupFilterButtons()

        //Load initial data (day view)
        loadChartData(TransactionViewModel.TimeFilter.DAY)
        binding.dayButton.setBackgroundResource(R.drawable.filterfilled)

        observeData()

    }

    private fun setupChart() {
        with(binding.curve) {
            // Basic chart configuration
            setTouchEnabled(true)
            setPinchZoom(true)
            description.isEnabled = false
            legend.isEnabled = false

            // Configure Y-axis (left)
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                axisLineColor = Color.BLACK
                textColor = Color.BLACK
            }

            // Configure Y-axis (right) - disable
            axisRight.isEnabled = false

            // Configure X-axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                axisLineColor = Color.BLACK
                textColor = Color.BLACK
                granularity = 1f
                labelCount = 7
            }
        }
    }

    private fun setupFilterButtons() {
        binding.apply {
            // Set click listeners for each button
            binding.dayButton.setOnClickListener {
                loadChartData(TransactionViewModel.TimeFilter.DAY)
                updateButtonState(binding.dayButton)
            }

            binding.weekButton.setOnClickListener {
                loadChartData(TransactionViewModel.TimeFilter.WEEK)
                updateButtonState(binding.weekButton)
            }

            binding.monthButton.setOnClickListener {
                loadChartData(TransactionViewModel.TimeFilter.MONTH)
                updateButtonState(binding.monthButton)
            }

            binding.yearButton.setOnClickListener {
                loadChartData(TransactionViewModel.TimeFilter.YEAR)
                updateButtonState(binding.yearButton)
            }
        }
    }

    // Function to update the button's background to green
    private fun updateButtonState(selectedButton: Button) {
        // Reset all buttons to outlined state
        resetButtonState()

        // Change the background color of the selected button to green
        selectedButton.setBackgroundResource(R.drawable.filterfilled)
    }

    // Function to reset all buttons to their outlined state
    private fun resetButtonState() {
        binding.apply {
            // Set the background to outlined drawable for each button
            dayButton.setBackgroundResource(R.drawable.filteroutline)
            weekButton.setBackgroundResource(R.drawable.filteroutline)
            monthButton.setBackgroundResource(R.drawable.filteroutline)
            yearButton.setBackgroundResource(R.drawable.filteroutline)
        }
    }




    private fun loadChartData(filter: TransactionViewModel.TimeFilter) {
        viewModel.loadGroupedExpenses(filter)

        viewModel.chartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TransactionViewModel.ChartState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is TransactionViewModel.ChartState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    updateChart(state.data, filter)
                }
                is TransactionViewModel.ChartState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("ChartFragment", "Error loading chart data: ${state.message}")
                }
            }
        }
    }

    private fun updateChart(data: List<ExpenseChartData>, filter: TransactionViewModel.TimeFilter) {
        val entries = data.mapIndexed { index, item ->
            Entry(index.toFloat(), item.amount.toFloat())
        }

        val dataSet = LineDataSet(entries, "Expenses").apply {
            color = Color.parseColor("#FF5722")  // Orange color
            lineWidth = 2.5f
            setDrawCircles(true)
            circleRadius = 4f
            circleHoleRadius = 2f
            setCircleColor(Color.parseColor("#FF5722"))
            valueTextColor = Color.BLACK
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha = 65
            fillColor = Color.parseColor("#FF5722")
            setDrawFilled(true)
        }

        // Set X-axis labels
        val xLabels = data.map { it.label }
        binding.curve.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

        // Adjust X-axis based on filter
        binding.curve.xAxis.apply {
            when (filter) {
                TransactionViewModel.TimeFilter.DAY -> {
                    labelCount = 7
                    granularity = 1f
                }
                TransactionViewModel.TimeFilter.WEEK -> {
                    labelCount = 6
                    granularity = 1f
                }
                TransactionViewModel.TimeFilter.MONTH -> {
                    labelCount = 6
                    granularity = 1f
                }
                TransactionViewModel.TimeFilter.YEAR -> {
                    labelCount = 5
                    granularity = 1f
                }
            }
        }

        binding.curve.data = LineData(dataSet)
        binding.curve.invalidate() // Refresh chart
    }

    private fun observeData() {
        viewModel.loadCategoryExpenses().observe(viewLifecycleOwner) { expenses ->
            if (expenses.isNotEmpty()) {
                updatePieChart(expenses)
            } else {
                binding.pieChart.clear()
                binding.pieChart.invalidate()
                binding.pieChart.setCenterText("No expenses found")
                binding.pieChart.setCenterTextSize(16f)
                binding.pieChart.setCenterTextColor(Color.GRAY)
            }
        }
    }

    private fun updatePieChart(expenses: List<CategoryExpense>) {
        val entries = expenses.map { expense ->
            PieEntry(
                expense.totalAmount.toFloat(),
                expense.category.replaceFirstChar { it.uppercase() }
            )
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = getColorPalette()
            valueTextSize = 12f
            valueTextColor = Color.WHITE
            sliceSpace = 3f // spacing between slices
            valueFormatter = PercentFormatter(binding.pieChart) // show % values
        }

        binding.pieChart.apply {
            data = PieData(dataSet)

            setHoleRadius(40f) // default is 58f, increase for a bigger hole size
            setTransparentCircleRadius(45f) // default is 61f, increase for larger transparent circle
            setUsePercentValues(true) // show values as percentages
            description.isEnabled = false // remove description
            setHoleColor(Color.TRANSPARENT) // transparent hole
            setCenterText("Expenses") // center text when data available
            setCenterTextSize(18f)
            setCenterTextColor(Color.DKGRAY)
            setEntryLabelColor(Color.BLACK) // labels (categories) color
            setEntryLabelTextSize(12f)

            animateY(1000, Easing.EaseInOutQuad) // animation on Y axis
            invalidate() // refresh
        }
    }

    private fun getColorPalette(): List<Int> {
        return listOf(
            Color.parseColor("#FFA726"), // Orange
            Color.parseColor("#66BB6A"), // Green
            Color.parseColor("#29B6F6"), // Light Blue
            Color.parseColor("#EF5350"), // Red
            Color.parseColor("#AB47BC")  // Purple
        )
    }

}
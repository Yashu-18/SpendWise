package com.example.spendwise.UI.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.Data.TransactionEntity
import com.example.spendwise.Data.TransactionViewModel
import com.example.spendwise.UI.observeOnce
import com.example.spendwise.databinding.FragmentAddTransactionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddTransactionBinding
    private lateinit var  viewModel:TransactionViewModel
    private var selectedDateInMillis: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        setupSpinners()
        setupDatePicker()
        binding.materialButton.setOnClickListener{
            insertData()
            dismiss()
        }
    }
    private fun insertData() {
        val amountText = binding.amt.text.toString().toDouble()
        val category = binding.categorySpinner.selectedItem.toString()
        val date = selectedDateInMillis // This should be the date in milliseconds from the DatePicker
        val isRecc = binding.toggleButton.isChecked

            // Recurrence logic (if balance is sufficient)
            var nextOccurrence: Long? = null
            if (isRecc) {
                // Get recurrence interval and unit from spinners
                val ri = binding.spinnerNumber.selectedItem.toString().toInt()
                val ru = binding.spinnerUnit.selectedItem.toString()

                // Calculate the next occurrence based on the selected interval and unit
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date

                when (ru.lowercase()) {
                    "day" -> calendar.add(Calendar.DAY_OF_YEAR, ri) // Add days
                    "week" -> calendar.add(Calendar.DAY_OF_YEAR, ri * 7) // Add weeks
                    "month" -> calendar.add(Calendar.MONTH, ri) // Add months
                    "year" -> calendar.add(Calendar.YEAR, ri) // Add years
                }

                // Get the next occurrence date in milliseconds
                nextOccurrence = calendar.timeInMillis
            }

            // Create a TransactionEntity to insert into Room database
            val transaction = TransactionEntity(
                amount = amountText,
                category = category,
                date = date,
                isRecurring = isRecc,
                recurrenceInterval = if (isRecc) binding.spinnerNumber.selectedItem.toString().toInt() else null,
                recurrenceUnit = if (isRecc) binding.spinnerUnit.selectedItem.toString() else null,
                nextOccurrence = nextOccurrence
            )
            if(category =="Income"){
                // Insert the transaction into the database using your ViewModel
                viewModel.insert(transaction)
                Toast.makeText(requireContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show()
            }else {
                val amount = amountText.toString().toDouble()
                if (amount == null) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                    return
                }

                viewModel.getBalance().observeOnce(viewLifecycleOwner) { balance ->
                    val currentBalance = balance ?: 0
                    if (amount > currentBalance) {
                        Toast.makeText(requireContext(), "Insufficient balance", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.insert(transaction)
                        Toast.makeText(requireContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }


    }


    private fun setupSpinners() {
        setupSpinner(binding.categorySpinner, listOf(
            "Select Category",
            "Food",
            "Transport",
            "Shopping",
            "Bill",
            "Entertainment",
            "Income",
            "Transfer",
            "Rent",
            "Utilities",
            "Education",
            "Groceries",
            "Loan",
            "Gifts",
            "Miscellaneous"
        ))
        setupSpinner(binding.spinnerNumber, listOf(1, 2, 3, 4, 5, 6))
        setupSpinner(binding.spinnerUnit, listOf("Day", "Week", "Month", "Year"))
    }

    private fun <T> setupSpinner(spinner: android.widget.Spinner, items: List<T>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.date.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDateInMillis = selection

                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formattedDate = formatter.format(Date(selection))
                binding.date.text = formattedDate
            }
        }
    }
}

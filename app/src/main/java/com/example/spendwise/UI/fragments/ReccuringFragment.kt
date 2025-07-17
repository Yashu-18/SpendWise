package com.example.spendwise.UI.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendwise.Adapters.ReccuringAdapter
import com.example.spendwise.Data.TransactionViewModel
import com.example.spendwise.R
import com.example.spendwise.UI.Models.ReccuringModel
import com.example.spendwise.UI.Models.TransactionModel
import com.example.spendwise.databinding.FragmentReccuringBinding
import java.util.Date

class ReccuringFragment : Fragment() {

    private var binding: FragmentReccuringBinding? = null

    // Define arrays for Spinner items
    private val statusOptions = arrayOf("Status", "All", "Active", "Inactive")
    private val frequencyOptions = arrayOf("Frequency", "Monthly", "Quarterly", "Yearly")
    private val dueOptions = arrayOf("Due", "Overdue", "Due this week", "Due this month")
    private lateinit var  viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        binding = FragmentReccuringBinding.inflate(inflater, container, false)

        // Return the root view of the binding object
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // Set up the spinners using a reusable function
        setupSpinner(binding!!.statusSpinner, statusOptions)
        setupSpinner(binding!!.frequencySpinner, frequencyOptions)
        setupSpinner(binding!!.dueSpinner, dueOptions)

        setupRecyclerView()
    }

    // Reusable function to set up a spinner with an adapter
    private fun setupSpinner(spinner: android.widget.Spinner, options: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
    }

    private fun setupRecyclerView() {
        val recyclerView = binding!!.recyclerView1
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the LiveData for transactions from the database
        viewModel.recurringTransactions.observe(viewLifecycleOwner) { transactions ->
            val recurringList = transactions.map { entity ->
                ReccuringModel(
                    // Convert Long date to Date
                    0,
                    timeSpan = entity.recurrenceUnit.toString(),
                    amount = entity.amount.toInt(),
                    category = entity.category
                )
            }

            val adapter = ReccuringAdapter(recurringList)
            recyclerView.adapter = adapter
        }


    }
}

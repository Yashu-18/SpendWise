package com.example.spendwise.UI.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlin.random.Random
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendwise.Adapters.TransactionAdapter
import com.example.spendwise.Data.TransactionEntity
import com.example.spendwise.Data.TransactionViewModel
import com.example.spendwise.UI.Models.TransactionModel
import com.example.spendwise.databinding.FragmentHomeBinding
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private  lateinit var viewModel: TransactionViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // updateData
        updateData()

        binding.clearButton.setOnClickListener {
            showClearDataDialog()
        }


    }




    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to delete all transactions?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAll()
                Toast.makeText(requireContext(), "All data deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    private fun updateData() {
        viewModel.getTotalIncome.observe(viewLifecycleOwner) { income ->
            viewModel.getTotalExpense.observe(viewLifecycleOwner) { expense ->
                // Ensure both income and expense are not null
                val balance = (income ?: 0) - (expense ?: 0)
                binding.totalBalance.text = balance.toString()
                binding.totalIncome.text = (income ?: 0).toString()
                binding.totalExpense.text = (expense ?: 0).toString()
            }
        }
    }




    private fun setupRecyclerView() {
        // Observe the LiveData for transactions from the database
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            val simplifiedTransactions = transactions.map { entity ->
                TransactionModel(
                    // Convert Long date to Date
                    0,
                    date = Date(entity.date),
                    amount = entity.amount.toInt(),
                    category = entity.category
                )
            }

            // Pass the filtered and simplified list to the adapter
            val adapter = TransactionAdapter(simplifiedTransactions)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter
        }
    }




}

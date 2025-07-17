package com.example.spendwise.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.UI.Models.TransactionModel
import com.example.spendwise.R
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactionList: List<TransactionModel>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.transactionImage)
        val date: TextView = itemView.findViewById(R.id.transactionDate)
        val amount: TextView = itemView.findViewById(R.id.reccuringAmount)
        val category: TextView = itemView.findViewById(R.id.reccuringCategory)
        val addorsub : ImageView = itemView.findViewById(R.id.addorsub)
        val rupeeIcon : ImageView = itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        // Dynamically load image from drawable using category name
        val context = holder.itemView.context
        val drawableId = context.resources.getIdentifier(
            transaction.category.lowercase(Locale.getDefault()),
            "drawable",
            context.packageName
        )
        holder.image.setImageResource(drawableId)

        // Format date like "Jan 16, 2022"
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.date.text = formatter.format(transaction.date)

        // Display amount properly
        holder.amount.text = transaction.amount.toString()

        // Display category with first letter capitalized
        holder.category.text = transaction.category.replaceFirstChar { it.uppercase() }

        if(holder.category.text !="Income"){
            holder.addorsub.setImageResource(R.drawable.minus)
            holder.rupeeIcon.setImageResource(R.drawable.rupeered)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
    }

    override fun getItemCount(): Int = transactionList.size
}

package com.example.spendwise.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.UI.Models.ReccuringModel
import java.util.*

class ReccuringAdapter(private val recurringList: List<ReccuringModel>) :
    RecyclerView.Adapter<ReccuringAdapter.ReccuringViewHolder>() {

    class ReccuringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.reccuringImage)
        val category: TextView = itemView.findViewById(R.id.reccuringCategory)
        val amount: TextView = itemView.findViewById(R.id.reccuringAmount)
        val timeSpan: TextView = itemView.findViewById(R.id.reccuringTimeSpan)
        val swtich : Switch = itemView.findViewById(R.id.reccSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReccuringViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reccuring, parent, false)
        return ReccuringViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReccuringViewHolder, position: Int) {
        val item = recurringList[position]

        // Load image dynamically based on category name
        val context = holder.itemView.context
        val drawableId = context.resources.getIdentifier(
            item.category.lowercase(Locale.getDefault()),
            "drawable",
            context.packageName
        )
        holder.image.setImageResource(drawableId)

        // Set other views
        holder.category.text = item.category.replaceFirstChar { it.uppercase() }
        holder.amount.text = "₹${item.amount}"
        holder.timeSpan.text = item.timeSpan
        holder.swtich.isChecked = true
    }

    override fun getItemCount(): Int = recurringList.size
}

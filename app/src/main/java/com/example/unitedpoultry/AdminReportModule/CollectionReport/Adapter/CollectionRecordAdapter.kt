package com.example.unitedpoultry.AdminReportModule.CollectionReport.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminReportModule.CollectionReport.model.CollectionRecordModel
import com.example.unitedpoultry.R

class CollectionRecordAdapter(
    private val list: MutableList<CollectionRecordModel>
) : RecyclerView.Adapter<CollectionRecordAdapter.ViewHolder>() {

    private lateinit var colorSets: List<ColorSet>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)

        if (!::colorSets.isInitialized) {
            colorSets = listOf(
                ColorSet(
                    ContextCompat.getColor(parent.context, R.color.primary),
                    ContextCompat.getColor(parent.context, R.color.primary20),
                    ContextCompat.getColor(parent.context, R.color.primary)
                ),
                ColorSet(
                    ContextCompat.getColor(parent.context, R.color.sub_primary),
                    ContextCompat.getColor(parent.context, R.color.sub_primary18),
                    ContextCompat.getColor(parent.context, R.color.sub_primary)
                ),
                ColorSet(
                    ContextCompat.getColor(parent.context, R.color.blue),
                    ContextCompat.getColor(parent.context, R.color.blue15),
                    ContextCompat.getColor(parent.context, R.color.blue)
                ),
                ColorSet(
                    ContextCompat.getColor(parent.context, R.color.purple),
                    ContextCompat.getColor(parent.context, R.color.purple15),
                    ContextCompat.getColor(parent.context, R.color.purple)
                )
            )
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
        holder.tvInvoice.text = item.invoiceNumber
        holder.tvAmount.text = item.amount
        holder.tvPaymentType.text = item.paymentType

        val color = colorSets[position % colorSets.size]

        holder.iconContainer.setCardBackgroundColor(color.full)
        holder.tvAmount.setTextColor(color.full)
        holder.card.background.mutate().setTint(color.light20)
        holder.statusLayout.background.mutate().setTint(color.light50)
        holder.tvPaymentType.setTextColor(color.full)
    }

    override fun getItemCount(): Int = list.size

    // 🔹 IMPORTANT: Update list for filters
    fun updateList(newList: List<CollectionRecordModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: View = v.findViewById(R.id.card)
        val iconContainer: CardView = v.findViewById(R.id.iconContainer)
        val statusLayout: View = v.findViewById(R.id.statusLayout)

        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDescription: TextView = v.findViewById(R.id.tvDescription)
        val tvPaymentType: TextView = v.findViewById(R.id.paymentType)
        val tvInvoice: TextView = v.findViewById(R.id.invoiceNumber)
        val tvAmount: TextView = v.findViewById(R.id.amount)
    }

    data class ColorSet(
        val full: Int,
        val light20: Int,
        val light50: Int
    )
}

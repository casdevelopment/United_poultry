package com.example.unitedpoultry.AdminReportModule.RiderReport.Adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminReportModule.RiderReport.model.RiderReportModel
import com.example.unitedpoultry.R

class RiderReportAdapter(
    private val list: MutableList<RiderReportModel>
) : RecyclerView.Adapter<RiderReportAdapter.ViewHolder>() {

    private lateinit var colorSets: List<ColorSet>

    fun updateList(newList: List<RiderReportModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_rider_report, parent, false)

        if (!::colorSets.isInitialized) {
            colorSets = listOf(
                ColorSet(
                    full = ContextCompat.getColor(parent.context, R.color.primary),
                    light = ContextCompat.getColor(parent.context, R.color.primary20)
                ),
                ColorSet(
                    full = ContextCompat.getColor(parent.context, R.color.sub_primary),
                    light = ContextCompat.getColor(parent.context, R.color.sub_primary18)
                ),
                ColorSet(
                    full = ContextCompat.getColor(parent.context, R.color.blue),
                    light = ContextCompat.getColor(parent.context, R.color.blue15)
                ),
                ColorSet(
                    full = ContextCompat.getColor(parent.context, R.color.purple),
                    light = ContextCompat.getColor(parent.context, R.color.purple15)
                )
            )
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val color = colorSets[position % colorSets.size]

        // Data
        holder.tvInitials.text = item.name.take(2).uppercase()
        holder.tvName.text = item.name
        holder.tvAreaShops.text = "${item.areas} areas • ${item.shops} shops"

        holder.tvSales.text = item.sales
        holder.tvCollected.text = item.collected
        holder.tvEfficiency.text = item.efficiency

        // 🎨 COLORS (CONNECTED)
        holder.tvInitials.background.mutate().setTint(color.full)
        holder.card.setCardBackgroundColor(color.light)

        holder.tvSales.setTextColor(color.full)

    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.card)

        val tvInitials: TextView = v.findViewById(R.id.tvInitials)
        val tvName: TextView = v.findViewById(R.id.tvRiderName)
        val tvAreaShops: TextView = v.findViewById(R.id.tvAreaAndShops)

        val tvSales: TextView = v.findViewById(R.id.tvSales)
        val tvCollected: TextView = v.findViewById(R.id.tvCollected)
        val tvEfficiency: TextView = v.findViewById(R.id.tvEfficiency)
    }

    data class ColorSet(
        val full: Int,
        val light: Int
    )
}

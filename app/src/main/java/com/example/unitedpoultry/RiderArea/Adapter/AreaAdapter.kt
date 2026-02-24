package com.example.unitedpoultry.RiderArea.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.ShopListActivity

class AreaAdapter(
    private var areaList: MutableList<AreaModel>
) : RecyclerView.Adapter<AreaAdapter.AreaViewHolder>() {

    private var filteredList = areaList.toMutableList()

    private val cardColors = listOf(R.color.primary9, R.color.sub_primary9, R.color.purple9, R.color.blue9)
    private val iconColors = listOf(R.color.primary, R.color.sub_primary, R.color.purple, R.color.blue)

    inner class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAreaName: TextView = itemView.findViewById(R.id.tvAreaName)
        val tvAreaAddress: TextView = itemView.findViewById(R.id.tvAreaAddress)
        val tvTotalShops: TextView = itemView.findViewById(R.id.tvTotalShops)
//        val tvRiders: TextView = itemView.findViewById(R.id.tvRiders)
//        val tvRecieveable: TextView = itemView.findViewById(R.id.tvRecieveable)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val iconContainer: CardView = itemView.findViewById(R.id.iconContainer)
        val ivNext: ImageView = itemView.findViewById(R.id.ivNext)
//        val btnViewShops: MaterialButton = itemView.findViewById(R.id.btnViewShops)



        val totalShopsLayout: LinearLayout = itemView.findViewById(R.id.totalShopsLayout)
        val visitedTodayLayout: LinearLayout = itemView.findViewById(R.id.visitedTodayLayout)
        val pendingLayout: LinearLayout = itemView.findViewById(R.id.pendingLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_area, parent, false)
        return AreaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvAreaName.text = item.name
        holder.tvAreaAddress.text = "${item.city}, ${item.description}"
        holder.tvTotalShops.text = item.shops_count.toString()
//        holder.tvRiders.text = "-"        // ignore for now
//        holder.tvRecieveable.text = "-"

        val cardColor = cardColors[position % cardColors.size]
        val iconColor = iconColors[position % iconColors.size]

        holder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, iconColor))
       // holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, iconColor))



        holder.totalShopsLayout.background.setTint(
            ContextCompat.getColor(holder.itemView.context, cardColor)
        )
        holder.visitedTodayLayout.background.setTint(
            ContextCompat.getColor(holder.itemView.context, cardColor)
        )
        holder.pendingLayout.background.setTint(
            ContextCompat.getColor(holder.itemView.context, cardColor)
        )

//        // Start EditAreaActivity normally
//        holder.ivEdit.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, EditAreaActivity::class.java)
//            intent.putExtra("AREA_NAME", item.name)
//            intent.putExtra("AREA_DESC", item.description)
//            intent.putExtra("AREA_CITY", item.city)
//            intent.putExtra("AREA_Status", item.is_active)
//            intent.putExtra("AREA_Id", item.id)
//            context.startActivity(intent)
//        }

        // Start AdminShopListActivity normally
        holder.ivNext.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShopListActivity::class.java)
            intent.putExtra("AREA_NAME", item.name)
            intent.putExtra("AREA_Id", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    // Update full list and reset filtered list
    fun updateList(newList: List<AreaModel>) {
        areaList.clear()
        areaList.addAll(newList)
        filteredList = areaList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) areaList.toMutableList()
        else areaList.filter {
            it.name.contains(query, true) ||
                    it.description.contains(query, true) ||
                    it.city.contains(query, true)
        }.toMutableList()
        notifyDataSetChanged()
    }
}

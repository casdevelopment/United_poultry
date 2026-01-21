package com.example.unitedpoultry.rider_home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminHome.model.ShopvisitedModel
import com.example.unitedpoultry.R

class RiderHomeAdapter(
    private val list: MutableList<ShopvisitedModel>
) : RecyclerView.Adapter<RiderHomeAdapter.ViewHolder>() {

    private val originalList = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_shops, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvShopName.text = item.name
        holder.subTitle.text = item.subTitle
        holder.ivIcon.setImageResource(item.image)




//        holder.itemView.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = if (item.type.equals("Sale", true)) {
//                Intent(context, SaleHistoryActivity::class.java)
//            } else {
//                Intent(context, CollectionHistoryActivity::class.java)
//            }
//
//            // Pass data
//            intent.putExtra("name", item.name)
//            intent.putExtra("amount", item.amount)
//
//            context.startActivity(intent)
//        }

    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvShopName: TextView = v.findViewById(R.id.tvName)
        val subTitle: TextView = v.findViewById(R.id.subTitle)
        val ivIcon: ImageView = v.findViewById(R.id.ivIcon)
    }
}

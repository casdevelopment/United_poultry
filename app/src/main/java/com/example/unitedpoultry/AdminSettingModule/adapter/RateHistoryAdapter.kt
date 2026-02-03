package com.example.unitedpoultry.AdminSettingModule.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.AdminRateHistoryActivity
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.AdminShopModule.AdminShopDetailsActivity
import com.example.unitedpoultry.R

class RateHistoryAdapter(
    originalList: MutableList<RatesData>,
) : RecyclerView.Adapter<RateHistoryAdapter.ViewHolder>() {

    private var filteredList: MutableList<RatesData> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rate_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tVDate.text = item.date
        holder.tVPrice.text = item.price+"/"+item.product_name


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminRateHistoryActivity::class.java)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = filteredList.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tVDate: TextView = v.findViewById(R.id.tVDate)
        val tVPrice: TextView = v.findViewById(R.id.tVPrice)


    }


}

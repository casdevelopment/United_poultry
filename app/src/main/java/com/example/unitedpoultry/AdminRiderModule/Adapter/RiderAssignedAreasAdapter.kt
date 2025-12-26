package com.example.unitedpoultry.AdminRiderModule.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminRiderModule.model.AdminAssignedAreasModel
import com.example.unitedpoultry.R


class RiderAssignedAreasAdapter(
    private val list: MutableList<AdminAssignedAreasModel>
) : RecyclerView.Adapter<RiderAssignedAreasAdapter.RiderViewHolder>() {

    private val originalList = ArrayList(list)

    inner class RiderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvShopsAssigned: TextView = itemView.findViewById(R.id.tvShopsAssigned)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_rider_assignedarea, parent, false)
        return RiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        val item = list[position]


        holder.tvName.text = item.name
        holder.tvShopsAssigned.text = "${item.assigned} assigned shops"

//        holder.btnViewDetails.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, AdminRiderDetailsActivity::class.java)
//
//            // Pass data
//            intent.putExtra("RIDER_NAME", item.name)
//            intent.putExtra("STATUS", item.status)
//            intent.putExtra("AREAS", item.areas)
//            intent.putExtra("SHOPS", item.shops)
//            intent.putExtra("SALE", item.todaySale)
//
//            context.startActivity(intent)
//        }

    }

    override fun getItemCount(): Int = list.size


}

package com.example.unitedpoultry.AdminRiderModule.Adapter


import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminArea.EditAreaActivity
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminRiderModule.AdminRiderDetailsActivity
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminShopModule.AdminShopListActivity
import com.example.unitedpoultry.R
import com.google.android.material.button.MaterialButton

class AdminRidersAdapter(
    private val riderList: MutableList<RiderModel>
) : RecyclerView.Adapter<AdminRidersAdapter.RiderViewHolder>() {

    private val originalList = mutableListOf<RiderModel>()
    private val filteredList = mutableListOf<RiderModel>()

    private val iconColors = listOf(
        R.color.primary,
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )

    inner class RiderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRiderName: TextView = itemView.findViewById(R.id.tvRiderName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val riderStatus: TextView = itemView.findViewById(R.id.riderStatus)
        val statusLayout: LinearLayout = itemView.findViewById(R.id.statusLayout)
        val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_rider, parent, false)
        return RiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvRiderName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvInitials.text = getInitials(item.name)

        val iconColor = iconColors[position % iconColors.size]


        if (item.is_active) {

            holder.statusLayout.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.context, R.color.green)
                )
            holder.riderStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.mint)
            )

            holder.tvInitials.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, iconColor)

        } else {
            holder.riderStatus.text="Inactive"
            holder.statusLayout.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.context, R.color.black60)
                )
            holder.riderStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.black60)
            )

            holder.tvInitials.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.black17)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminRiderDetailsActivity::class.java)
//            intent.putExtra("INITIALS",getInitials(item.name))
            intent.putExtra("ID", item.id)
//            intent.putExtra("EMAIL", item.email)
//            intent.putExtra("USER_NAME", item.username)
//            intent.putExtra("CNIC", item.cnic)
//            intent.putExtra("PHONE_NUMBER", item.phone_number)
//            intent.putExtra("ADDRESS", item.address)
//            intent.putExtra("IS_ACTIVE", item.is_active)
//            intent.putExtra("IMAGE", item.image)
//            intent.putExtra("ROLE_ID", item.role_id)

            context.startActivity(intent)
        }




    }

    override fun getItemCount(): Int = filteredList.size

    // 🔹 Called from Fragment after API response
    fun updateList(newList: List<RiderModel>) {
        originalList.clear()
        originalList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }

    // 🔹 SEARCH + STATUS FILTER
    fun filter(query: String, status: String) {
        filteredList.clear()

        val search = query.trim().lowercase()

        for (item in originalList) {
            val matchSearch =
                item.name.lowercase().contains(search) ||
                        item.address!!.lowercase().contains(search)

            val matchStatus =
                status == "ALL" ||
                        (status == "Active" && item.is_active) ||
                        (status == "Inactive" && !item.is_active)

            if (matchSearch && matchStatus) {
                filteredList.add(item)
            }
        }

        notifyDataSetChanged()
    }

    fun countByStatus(status: String): Int {
        return when (status) {
            "Active" -> originalList.count { it.is_active }
            "Inactive" -> originalList.count { !it.is_active }
            else -> originalList.size
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}

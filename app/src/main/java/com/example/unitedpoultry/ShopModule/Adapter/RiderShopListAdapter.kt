package com.example.unitedpoultry.ShopModule.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.unitedpoultry.AdminShopModule.model.ShopModel
import com.example.unitedpoultry.Collection.CollectionformActivity
import com.example.unitedpoultry.NewSale.SaleFormActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.ShopDetailsActivity
import com.example.unitedpoultry.status_check.UserStatusChecker
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppConstants
import com.google.android.material.button.MaterialButton

class RiderShopListAdapter(
    private var originalList: MutableList<ShopModel>,
    private val areaId: Int,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: UserStatusViewModel
) : RecyclerView.Adapter<RiderShopListAdapter.ViewHolder>() {

    private var filteredList: MutableList<ShopModel> = originalList.toMutableList()

    private val iconColors = listOf(
        R.color.primary,
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvCashIn.text = "Rs ${item.cash_in}"
        holder.tvBorrowed.text = "Rs ${item.borrowed}"
        holder.tvRepaid.text = "Rs ${item.repaid}"
      //  holder.statusText.text = if (item.is_active == true) "Active" else "Inactive"

      //  holder.tvInitials.text = getInitials(item.name)

        val imageUrl = item.image

        if (!imageUrl.isNullOrEmpty()) {
            val fullImageUrl = AppConstants.ImageURL + imageUrl

            Glide.with(holder.itemView.context)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(R.drawable.homeingreen)
                .error(R.drawable.homeingreen)
                .into(holder.imgShop)
        } else {
            holder.imgShop.setImageResource(R.drawable.homeingreen)
        }

        val iconColor = iconColors[position % iconColors.size]
//
//        holder.tvTotalShops.text = "-"
//        holder.tvReceivable.text = "-"

//        when (item.is_active) {
//            true -> {
////                holder.statusLayout.backgroundTintList =
////                    ColorStateList.valueOf(
////                        ContextCompat.getColor(holder.itemView.context, R.color.green)
////                    )
////                holder.statusText.setTextColor(
////                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
////                )
//
////                holder.tvInitials.backgroundTintList =
////                    ContextCompat.getColorStateList(holder.itemView.context, iconColor)
//
//                holder.statusText.setTextColor(
//                    ContextCompat.getColor(holder.itemView.context, R.color.mint)
//                )
//            }
//
//            false -> {
////                holder.statusLayout.backgroundTintList =
////                    ColorStateList.valueOf(
////                        ContextCompat.getColor(holder.itemView.context, R.color.black)
////                    )
//                holder.statusText.setTextColor(
//                    ContextCompat.getColor(holder.itemView.context, R.color.black60)
//                )
//
////                holder.tvInitials.backgroundTintList =
////                    ContextCompat.getColorStateList(holder.itemView.context, R.color.black17)
//
//                holder.statusText.setTextColor(
//                    ContextCompat.getColor(holder.itemView.context, R.color.black44)
//                )
//            }
//
//            else -> {
////                holder.statusLayout.backgroundTintList =
////                    ColorStateList.valueOf(
////                        ContextCompat.getColor(holder.itemView.context, R.color.gray)
////                    )
//                holder.statusText.setTextColor(
//                    ContextCompat.getColor(holder.itemView.context, R.color.gray)
//                )
//            }
//        }


        holder.statusText.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("SHOP_ID", item.id)
            intent.putExtra("AREA_ID", areaId)
            context.startActivity(intent)
        }

        holder.btnNewSale.setOnClickListener {
            UserStatusChecker.check(
                lifecycleOwner = lifecycleOwner,
                viewModel = viewModel,

                onActive = {
                    val context = holder.itemView.context
                    val intent = Intent(context, SaleFormActivity::class.java)
                    intent.putExtra("SHOP_ID", item.id)
                    intent.putExtra("AREA_ID", areaId)
                    intent.putExtra("NAME", item.name)
                    intent.putExtra("ADDRESS", item.address)
                    intent.putExtra("DISCOUNT", item.discount_per_petti)
                    context.startActivity(intent)
                },

                onInactive = {
                    Toast.makeText(
                        holder.itemView.context,
                        "Your account is inactive. Contact admin.",
                        Toast.LENGTH_LONG
                    ).show()
                },

                onError = { message ->
                    Toast.makeText(holder.itemView.context, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                }
            )
        }


        holder.btnCollect.setOnClickListener {
            UserStatusChecker.check(
                lifecycleOwner = lifecycleOwner,
                viewModel = viewModel,

                onActive = {
                    val context = holder.itemView.context
                    val intent = Intent(context, CollectionformActivity::class.java)
                    intent.putExtra("SHOP_ID", item.id)
                   // intent.putExtra("AREA_ID", areaId)
                    intent.putExtra("NAME", item.name)
                    intent.putExtra("ADDRESS", item.address)
                    intent.putExtra("BORROWED", item.borrowed)
                    context.startActivity(intent)
                },

                onInactive = {
                    Toast.makeText(
                        holder.itemView.context,
                        "Your account is inactive. Contact admin.",
                        Toast.LENGTH_LONG
                    ).show()
                },

                onError = { message ->
                    Toast.makeText(holder.itemView.context, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                }
            )
        }



//        holder.btnNewSale.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, SaleFormActivity::class.java)
//            intent.putExtra("SHOP_ID", item.id)
//            intent.putExtra("AREA_ID", areaId)
//            intent.putExtra("NAME", item.name)
//            intent.putExtra("ADDRESS", item.address)
//            intent.putExtra("DISCOUNT", item.discount_per_petti)
//            context.startActivity(intent)
//        }

//        holder.btnExchange.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, ExchangeOrReturnActivity::class.java)
//            intent.putExtra("SHOP_ID", item.id)
//            intent.putExtra("AREA_ID", areaId)
//            intent.putExtra("NAME", item.name)
//            intent.putExtra("ADDRESS", item.address)
//            intent.putExtra("DISCOUNT", item.discount_per_petti)
//            context.startActivity(intent)
//        }

//        holder.btnCollect.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, CollectionformActivity::class.java)
//            intent.putExtra("ID", item.id)
//            intent.putExtra("NAME", item.name)
//            intent.putExtra("ADDRESS", item.address)
//            intent.putExtra("DISCOUNT", item.discount_per_petti)
//            context.startActivity(intent)
//        }

    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<ShopModel>) {
        originalList.clear()
        originalList.addAll(newList)
        filteredList = originalList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, true) || it.address.contains(query, true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvAddress: TextView = v.findViewById(R.id.tvAddress)

        val statusText: TextView = v.findViewById(R.id.statusText)
        // val tvInitials: TextView = v.findViewById(R.id.tvInitials)
        //val statusLayout: LinearLayout = v.findViewById(R.id.statusLayout)
        val btnNewSale: MaterialButton = v.findViewById(R.id.btnNewSale)
//        val btnCollect: MaterialButton = v.findViewById(R.id.btnCollect)
        val btnCollect: MaterialButton = v.findViewById(R.id.btnCollect)

        val imgShop: ImageView = v.findViewById(R.id.imgShop)

         val tvCashIn: TextView = v.findViewById(R.id.tvCashIn)
        val tvBorrowed: TextView = v.findViewById(R.id.tvBorrowed)
        val tvRepaid: TextView = v.findViewById(R.id.tvRepaid)

    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}

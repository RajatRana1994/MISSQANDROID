package com.msq.code.customer.servicebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.MSQService
import com.msq.rest.entity.NearServiceResult
import com.socialgalaxyApp.util.extension.loadWallRound
import java.util.*

class ServiceBookAdapter(
    private val mList: List<MSQService>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_type, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvService.text = mList[position].name.capitalize(Locale.ENGLISH)
                holder.ivService.loadWallRound(if (mList[position].image.isNullOrEmpty().not()) mList[position].image else R.drawable.app_logo)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivService = view.findViewById<RoundedImageView>(R.id.ivService)
        val tvService = view.findViewById<TextView>(R.id.tvService)
    }
}
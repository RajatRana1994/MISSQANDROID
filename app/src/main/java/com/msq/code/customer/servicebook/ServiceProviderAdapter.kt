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
import com.msq.util.ext.onOffVisibility
import com.msq.util.ext.oneDecimal
import com.msq.util.ext.twoDecimal
import com.socialgalaxyApp.util.extension.loadWallRound
import java.util.*

class ServiceProviderAdapter(
    private val mList: List<NearServiceResult>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvUser.text = mList[position].providerName.capitalize(Locale.ENGLISH)
                holder.tvUserRating.apply {
                    onOffVisibility(true)
                    text= (mList[position].rating?:0.0).oneDecimal()
                }
                holder.ivUser.loadWallRound(if (mList[position].providerImage.isNullOrEmpty().not()) mList[position].providerImage else R.drawable.app_logo)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvUser = view.findViewById<TextView>(R.id.tvUser)
        val tvUserRating = view.findViewById<TextView>(R.id.tvUserRating)
    }
}
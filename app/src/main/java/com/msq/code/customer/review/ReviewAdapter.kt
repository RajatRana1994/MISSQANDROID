package com.msq.code.customer.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.GetReviewRating
import com.socialgalaxyApp.util.extension.loadUserImage
import com.socialgalaxyApp.util.extension.loadWallRound

class ReviewAdapter(
    private val mList: List<GetReviewRating>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                if (mList[position].providerInfo.providerProfile.isNotEmpty()) holder.ivService.loadWallRound(mList[position].providerInfo.providerProfile)
                holder.tvCustomer.text = mList[position].providerInfo.providerName
                holder.tvService.text = mList[position].comment
                holder.rbRating.rating= mList[position].rating
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivService = view.findViewById<RoundedImageView>(R.id.ivService)
        val rbRating = view.findViewById<RatingBar>(R.id.rbRating)
        val tvCustomer = view.findViewById<TextView>(R.id.tvCustomer)
        val tvService = view.findViewById<TextView>(R.id.tvService)
    }
}
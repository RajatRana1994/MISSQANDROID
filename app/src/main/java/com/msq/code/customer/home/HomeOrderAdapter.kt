package com.msq.code.customer.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.BookingListParseData
import com.msq.util.ext.getFormatTime
import com.socialgalaxyApp.util.extension.loadUserImage

class HomeOrderAdapter(
    private val mList: List<BookingListParseData>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_order, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvCurrentService.text = mList[position].serviceDetails.name
                if (mList[position].serviceDetails.image.isNotEmpty()) holder.ivUser.loadUserImage(mList[position].serviceDetails.image)
                holder.tvDate.text = (mList[position].bookingDate * 1000).getFormatTime("MMMM dd,yyyy")
                holder.tvName.text = mList[position].serviceDetails.name
                holder.tvOrderNum.text = "RFG ${mList[position].serviceId}"
                holder.tvCurrentService.text = "1 Service"
                holder.tvCancelledStatus.text = if (mList[position].status == 0) holder.tvCancelledStatus.context.getString(R.string.pending) else if (mList[position].status == 2) "Rejected" else if (mList[position].status == 3 && mList[position].setup == 4) "Completed" else holder.tvCancelledStatus.context.getString(
                                    R.string.ongoing)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvOrderNum = view.findViewById<TextView>(R.id.tvOrderNum)
        val tvCancelledStatus = view.findViewById<TextView>(R.id.tvCancelledStatus)
        val tvCurrentService = view.findViewById<TextView>(R.id.tvCurrentService)
    }
}
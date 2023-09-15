package com.msq.code.provider.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.msq.R
import com.msq.rest.entity.BookingListParseData
import com.msq.util.ext.getFormatTime
import java.util.*

class BookingPROAdapter(
    private val mList: List<BookingListParseData>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_booking_pro, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvCustomerAddress.text =
                    mList[position].serviceDetails.name.capitalize(Locale.ENGLISH)
                holder.tvCustomerLocation.text = mList[position].userInfo?.userAddress?.capitalize(Locale.ENGLISH)
                holder.tvCustomerSitepoint.text = mList[position].userInfo?.userAddress?.capitalize(Locale.ENGLISH)
                holder.tvCompleted.isVisible = mList[position].status != 2
                holder.tvReject.isVisible = mList[position].status == 0||mList[position].status == 2
                holder.tvTime.text =
                    "${(mList[position].bookingStartTime * 1000).getFormatTime("hh:mm a")}\n\n${
                        (mList[position].bookingEndTime * 1000).getFormatTime("hh:mm a")
                    }"
                holder.tvCompleted.text =
                    if (mList[position].status == 0) holder.tvCompleted.context.getString(R.string.accept) else if (mList[position].status == 3||(mList[position].status == 1 && mList[position].setup == 4)) holder.tvCompleted.context.getString(R.string.completed) else holder.tvCompleted.context.getString(
                        R.string.ongoing)
                holder.tvReject.text = if (mList[position].status == 0) holder.tvCompleted.context.getString(R.string.reject) else  holder.tvCompleted.context.getString(
                                    R.string.rejected)
                holder.tvCompleted.setOnClickListener {
                    if (mList[position].status == 0) onClick.invoke("accept", position)
                }
                holder.tvReject.setOnClickListener {
                    if (mList[position].status == 0) onClick.invoke("reject", position)
                }
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvCustomerAddress = view.findViewById<TextView>(R.id.tvCustomerAddress)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvCustomerLocation = view.findViewById<TextView>(R.id.tvCustomerLocation)
        val tvCustomerSitepoint = view.findViewById<TextView>(R.id.tvCustomerSitepoint)
        val tvCompleted = view.findViewById<TextView>(R.id.tvCompleted)
        val tvReject = view.findViewById<TextView>(R.id.tvReject)
    }
}
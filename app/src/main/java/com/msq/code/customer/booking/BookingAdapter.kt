package com.msq.code.customer.booking

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.base.MSQApp.Companion.session
import com.msq.rest.entity.BookingListParseData
import com.msq.util.SessionManager
import com.msq.util.ext.getFormatTime
import com.socialgalaxyApp.util.extension.loadWallRound
import java.util.*

class BookingAdapter(
    private val mList: List<BookingListParseData>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvDoPayment.isVisible= mList[position].paymentType == 2 && mList[position].isAccept==1 && mList[position].isPaymentDone==0
                holder.tvDate.text =
                    (mList[position].bookingDate * 1000).getFormatTime("MMMM dd,EEEE")
                holder.tvServiceName.text =
                    mList[position].serviceDetails.name.capitalize(Locale.ENGLISH)
                holder.tvClient.text = holder.tvClient.context.getString(R.string.booking_cline_s,
                    mList[position].providerInfo.providerName?:"".capitalize(Locale.ENGLISH))
                holder.tvAddress.text =
                    holder.tvAddress.context.getString(R.string.booking_address_s,
                        mList[position].serviceProviderLocation?.address ?: "")
                holder.tvEmployeeID.text =
                    holder.tvEmployeeID.context.getString(R.string.booking_employee_s,
                        mList[position].serviceProviderId.toString())
                holder.tvTrackingNum.text =
                    Html.fromHtml(holder.tvTrackingNum.context.getString(R.string.booking_tracking_s,
                        "<font color=\"#0C95FF\">${mList[position].serviceId}</font>"))
                holder.tvStart.text =
                    holder.tvTrackingNum.context.getString(R.string.booking_start_s,
                        (mList[position].bookingStartTime * 1000).getFormatTime("hh:mm a")
                            .capitalize(Locale.ENGLISH))
                if (mList[position].serviceDetails.image.isNullOrEmpty()
                        .not()
                ) holder.ivUser.loadWallRound(mList[position].serviceDetails.image)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
                holder.tvDoPayment.setOnClickListener {
                    onClick.invoke("payment", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvServiceName = view.findViewById<TextView>(R.id.tvServiceName)
        val tvClient = view.findViewById<TextView>(R.id.tvClient)
        val tvAddress = view.findViewById<TextView>(R.id.tvAddress)
        val tvStart = view.findViewById<TextView>(R.id.tvStartTime)
        val tvEmployeeID = view.findViewById<TextView>(R.id.tvEmployeeID)
        val tvTrackingNum = view.findViewById<TextView>(R.id.tvTrackingNum)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvDoPayment = view.findViewById<TextView>(R.id.tvDoPayment)
    }
}
package com.msq.code.provider.wallet.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.WalletTransection
import com.msq.util.ext.twoDecimal
import com.socialgalaxyApp.util.extension.loadWallImage
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(
    private val mList: ArrayList<WalletTransection>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvName.text = mList[position].text.capitalize(Locale.ENGLISH)
                holder.tvId.text = "ID ${mList[position].id}"
                holder.tvAmount.text = "₱ "+mList[position].amount.twoDecimal()
                holder.tvName.text = mList[position].text.capitalize(Locale.ENGLISH)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<ImageView>(R.id.ivUser)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvId = view.findViewById<TextView>(R.id.tvId)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
    }
}
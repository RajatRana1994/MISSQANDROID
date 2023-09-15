package com.msq.code.auth

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.MSQService
import okhttp3.internal.notifyAll
import java.util.*

class ServiceSelectAdapter(
    private val mList: MutableList<MSQService>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_select, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                val nms=(mList[position].name).split(" ").filter { it.trim().isNotEmpty() }
                holder.tvService.text = if (nms.isNotEmpty()) TextUtils.join(" ",nms) else mList[position].name.capitalize(Locale.ENGLISH)
                holder.tvService.isChecked = mList[position].selected
                holder.itemView.setOnClickListener {
                    mList.onEach { it.selected=false }
                    mList[position].selected=true
                    onClick.invoke(mList[position].id.toString(), position)
                    this@ServiceSelectAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvService = view.findViewById<CheckedTextView>(R.id.cbServiec)
    }
}
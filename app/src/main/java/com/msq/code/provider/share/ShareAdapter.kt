package com.msq.code.provider.share

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.socialgalaxyApp.util.extension.loadWallImage

class ShareAdapter(
    private val mList: MutableList<Pair<String,Int>>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_share, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvUser.text = mList[position].first
                holder.ivUser.setImageResource(mList[position].second)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    private class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<ImageView>(R.id.ivUser)
        val tvUser = view.findViewById<TextView>(R.id.tvUser)
    }
}
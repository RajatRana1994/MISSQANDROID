package com.msq.code.customer.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.rest.entity.ChatUserListData
import com.socialgalaxyApp.util.extension.loadWallRound

class ChatUserAdapter(
    private val mList: List<ChatUserListData>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyHolder -> {
                holder.tvUser.text = mList[position].friendInfo.name
                if (mList[position].friendInfo.profile.isNotEmpty()) holder.ivUser.loadWallRound(
                    mList[position].friendInfo.profile)
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
    }
}
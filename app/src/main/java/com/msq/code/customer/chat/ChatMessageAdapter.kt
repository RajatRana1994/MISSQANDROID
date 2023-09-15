package com.msq.code.customer.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.msq.base.MSQApp
import com.msq.rest.entity.MessageData
import com.msq.util.SessionManager
import com.socialgalaxyApp.util.extension.loadWallRound

class ChatMessageAdapter(
    private val context:Context,
    private val mList: ArrayList<MessageData>,
    private val onClick: (String, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val pref=MSQApp.session(context).getUserData()
    val ITEM_SENDER=0
    val ITEM_RECEIVER=1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (ITEM_SENDER==viewType) SenderHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message_sender, parent, false)) else ReceiverHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message_receiver, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SenderHolder -> {
                holder.tvUserMessage.text = mList[position].message
                if (pref?.profile!!.isNotEmpty()) holder.ivUser.loadWallRound(pref?.profile!!)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
            is ReceiverHolder -> {
                holder.tvUserMessage.text = mList[position].message
                if (mList[position].friendInfo.profile.isNotEmpty()) holder.ivUser.loadWallRound(mList[position].friendInfo.profile)
                holder.itemView.setOnClickListener {
                    onClick.invoke("detail", position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].sender_id.toString()== pref!!.id.toString()) ITEM_SENDER
        else ITEM_RECEIVER
    }
    override fun getItemCount(): Int = mList.size

    private class ReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvUserMessage = view.findViewById<TextView>(R.id.tvUserMessage)
    }
    private class SenderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUser = view.findViewById<RoundedImageView>(R.id.ivUser)
        val tvUserMessage = view.findViewById<TextView>(R.id.tvUserMessage)
    }
}
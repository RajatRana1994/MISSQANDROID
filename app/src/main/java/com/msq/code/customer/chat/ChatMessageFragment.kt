package com.msq.code.customer.chat

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ChatUserViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.rest.entity.MessageData
import com.msq.util.ConstUtils
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_chat_message.*


class ChatMessageFragment : BaseFragment(), View.OnClickListener {
    val friend_id by lazy { requireArguments().getString("friend_id") ?: "" }
    val bookingId by lazy { requireArguments().getString("bookingId") ?: "" }
    lateinit var viewModel: ChatUserViewModel
    val mMessageData = arrayListOf<MessageData>()
    private val handle by lazy { Handler() }
    var runnable :Runnable?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
            .get(ChatUserViewModel::class.java).also {
                it.getAllMessage(bookingId)
            }
        onClicks()
        rvChatMessage.adapter = ChatMessageAdapter(requireActivity(),mMessageData) { t, p ->

        }
        observers()
        runnable= Runnable {
            viewModel.getAllMessage(bookingId)
        }
        handle.postDelayed(runnable!!,3000)

    }

    private fun observers() {
        viewModel.mGetAllMessageEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                val data=it.data.filter {m-> mMessageData.contains(m).not() }
                mMessageData.addAll(data)
                rvChatMessage.adapter?.notifyDataSetChanged()
                if (runnable!=null){
                    handle.removeCallbacks(runnable!!)
                    handle.postDelayed(runnable!!,3000)
                }
            }
        }
        viewModel.mSendMessageEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                if (mMessageData.contains(it.data).not())mMessageData.add(it.data)
                rvChatMessage.adapter?.notifyDataSetChanged()
                edtMessage.setText("")
            }
        }
    }

    private fun onClicks() {
        ivBack.setOnClickListener(this)
        ivSend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack -> requireActivity().onBackPressed()
            ivSend -> {
                if (edtMessage.text.isEmpty()) toast("Enter message")
                else {
                    viewModel.sendMessage(JsonObject().also {
                        it.addProperty("friend_id", friend_id)
                        it.addProperty("bookingId", bookingId)
                        it.addProperty("message_type", ConstUtils.TEXT_MSG)
                        it.addProperty("message", edtMessage.text.toString())
                    })
                }
            }
        }
    }


}
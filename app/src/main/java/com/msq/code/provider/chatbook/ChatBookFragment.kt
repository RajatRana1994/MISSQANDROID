package com.msq.code.provider.chatbook

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ChatBookViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.rest.entity.MessageData
import com.msq.util.ConstUtils
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_chat_book.*


class ChatBookFragment : BaseFragment(), View.OnClickListener {
    val user_id by lazy { requireArguments().getString("user_id") ?: "" }
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    lateinit var viewModel: ChatBookViewModel
    val mMessageData = arrayListOf<MessageData>()
    private val handle by lazy { Handler() }
    var runnable :Runnable?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ChatBookViewModel::class.java).also {
                    it.getAllMessage(booking_id)
                }
        tvchatId.text = "Quote #${booking_id}"
        onClicks()
        rvChatMessage.adapter = ChatBookAdapter(requireActivity(), mMessageData) { t, p ->

        }
        observers()
        runnable= Runnable {
            viewModel.getAllMessage(booking_id)
        }
        handle.postDelayed(runnable!!,3000)
    }

    private fun observers() {
        viewModel.mGetAllMessageEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                val data = it.data.filter { m -> mMessageData.contains(m).not() }
                mMessageData.addAll(data)
                rvChatMessage.adapter?.notifyDataSetChanged()
                if (runnable != null) {
                    handle.removeCallbacks(runnable!!)
                    handle.postDelayed(runnable!!, 3000)
                }
            }
        }
        viewModel.mSendMessageEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                if (mMessageData.contains(it.data).not()) mMessageData.add(it.data)
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
                        it.addProperty("friend_id", user_id)
                        it.addProperty("bookingId", booking_id)
                        it.addProperty("message_type", ConstUtils.TEXT_MSG)
                        it.addProperty("message", edtMessage.text.toString())
                    })
                }
            }
        }
    }

}
package com.msq.code.customer.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ChatUserViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_chat_user.*


class ChatUserFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ChatUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ChatUserViewModel::class.java).also {
                    it.lastChat()
                }
        onClicks()
        observers()
    }

    private fun observers() {
        viewModel.mChatUserListEntity.observe(viewLifecycleOwner, {
            tvNoChat.isVisible=it.data.isEmpty()
            rvChatUser.isVisible=it.data.isNotEmpty()
            rvChatUser.adapter = ChatUserAdapter(it.data) { t, p ->
                showFragment(FragConst.CHAT_MSG, bundleOf("bookingId" to it.data[p].bookingId.toString(),"friend_id" to if (it.data[p].sender_id.toString()==pref.getPrefString(SessionManager.USER_ID)!!) it.data[p].receiver_id.toString() else it.data[p].sender_id.toString()))
            }

        })
    }

    private fun onClicks() {
        tvChat.setOnClickListener {
            pref.clearPrefs()
            showFragment(FragConst.WELCOME)
        }
//        btnGetStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
//            btnGetStarted -> {
//                showFragment(FragConst.GETSTARTED)
//            }
        }
    }

}
package com.msq.code.provider.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.msq.R
import com.msq.rest.entity.MSQService

class ServicePopAdapter(val ctx: Context, val cardsList: MutableList<MSQService>) :
    ArrayAdapter<MSQService>(ctx, android.R.layout.simple_list_item_1) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = null
        if (convertView == null)
            view =
                LayoutInflater.from(ctx).inflate(R.layout.pop_item, parent, false)
        else view = convertView

        view!!.findViewById<TextView>(R.id.tvService).text = cardsList[position].name

        return view!!
    }

    override fun getCount(): Int {
        return cardsList.size
    }
}
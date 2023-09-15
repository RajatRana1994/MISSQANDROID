package com.msq.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DisplayAdapter(
    private val layout: Int,
    private val count: Int,
    private val callback: (DisplayAdapter,RecyclerView.ViewHolder, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var SELECTED_POSITION = 0
    var LAST_SELECTED_POSITION = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DisplayHolder(
            LayoutInflater.from(parent.context).inflate(this.layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        callback.invoke(this,holder, position)
    }

    override fun getItemCount(): Int = count

    public class DisplayHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}
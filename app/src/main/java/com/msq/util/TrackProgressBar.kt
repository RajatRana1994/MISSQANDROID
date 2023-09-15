package com.spotify.sdk.demo.kotlin

import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import java.util.concurrent.TimeUnit

class TrackProgressBar(private var seekBar: SeekBar,var start:TextView?=null,private val seekStopListener: (Long) -> Unit) {
    private val handler: Handler
    var removedHandler=false
    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            seekStopListener.invoke(seekBar.progress.toLong())
        }
    }

    init {
        removedHandler=false
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        handler = Handler()
    }

    fun changeViews(seekV: SeekBar,startV:TextView?=null){
        seekBar=seekV
        if (startV!=null) start=startV
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }

    private val seekUpdateRunnable = object : Runnable {
        override fun run() {
            val progress = seekBar.progress
            Log.e("removedHandler",removedHandler.toString())
            seekBar.progress = progress + LOOP_DURATION
            if (start!=null)
                start!!.text=getTimeInFormat(seekBar.progress.toLong())
            if (removedHandler.not())
                handler.postDelayed(this, LOOP_DURATION.toLong())
        }
    }

    fun setDuration(duration: Long) {
        seekBar.max = duration.toInt()
    }

    fun update(progress: Long) {
        seekBar.progress = progress.toInt()
        if (start!=null)
            start!!.text=getTimeInFormat(seekBar.progress.toLong())
    }

    fun pause() {
        handler.removeCallbacks(seekUpdateRunnable)
        removedHandler=true
    }

    fun unpause() {
        handler.removeCallbacks(seekUpdateRunnable)
        handler.postDelayed(seekUpdateRunnable, LOOP_DURATION.toLong())
        removedHandler=false
    }

    companion object {
        private const val LOOP_DURATION = 500
    }

    fun getTimeInFormat(toLong: Long): String {
        val totalSec = TimeUnit.MILLISECONDS.toSeconds(toLong)
        val min = totalSec / 60
        val sec = totalSec % 60
        return "${if (min > 0) String.format(
            "%02d",
            min
        ) else "00"}:${if (sec > 0) String.format("%02d", sec) else "00"}"
    }
}
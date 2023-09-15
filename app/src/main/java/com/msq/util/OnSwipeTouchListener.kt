package com.msq.util

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class OnSwipeTouchListener internal constructor(ctx: Context, mainView: View,val onLeft:()->Unit,val onRight:()->Unit) : View.OnTouchListener {
        companion object {
            private const val SWIPE_THRESHOLD = 100
            private const val SWIPE_VELOCITY_THRESHOLD = 100
        }

        private val gestureDetector: GestureDetector
        var context: Context
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
           override fun onDown(e: MotionEvent?): Boolean {
               Log.e("XY","${e!!.x} >> ${e!!.y}")
                return true
            }

            override  fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                var result = false
                try {
                    val diffY: Float = e2.getY() - e1.getY()
                    val diffX: Float = e2.getX() - e1.getX()
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > Companion.SWIPE_THRESHOLD && Math.abs(
                                velocityX
                            ) > Companion.SWIPE_VELOCITY_THRESHOLD
                        ) {
                            if (diffX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                            result = true
                        }
                    } else if (Math.abs(diffY) > Companion.SWIPE_THRESHOLD && Math.abs(
                            velocityY
                        ) > Companion.SWIPE_VELOCITY_THRESHOLD
                    ) {
                        if (diffY > 0) {
                            onSwipeBottom()
                        } else {
                            onSwipeTop()
                        }
                        result = true
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                return result
            }
        }

        fun onSwipeRight() {
//            Toast.makeText(context, "Swiped Right", Toast.LENGTH_SHORT).show()
//            onSwipe!!.swipeRight()
            onRight()
        }

        fun onSwipeLeft() {
//            Toast.makeText(context, "Swiped Left", Toast.LENGTH_SHORT).show()
//            onSwipe!!.swipeLeft()
            onLeft()
        }

        fun onSwipeTop() {
//            Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show()
            onSwipe!!.swipeTop()
        }

        fun onSwipeBottom() {
//            Toast.makeText(context, "Swiped Down", Toast.LENGTH_SHORT).show()
            onSwipe!!.swipeBottom()
        }

        interface onSwipeListener {
            fun swipeRight()
            fun swipeTop()
            fun swipeBottom()
            fun swipeLeft()
        }

        var onSwipe: onSwipeListener? = null

        init {
            gestureDetector = GestureDetector(ctx, GestureListener())
            mainView.setOnTouchListener(this)
            context = ctx
        }
    }

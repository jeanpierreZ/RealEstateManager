package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class ScaleViewListener(context: Context) : View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private lateinit var view: View
    private var scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private var scaleFactor: Float = 1f
    private var inScale = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        v?.let { view = it }
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        inScale = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        inScale = false
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        scaleFactor *= detector?.scaleFactor!!
        // To Zoom in or out with a factor of 10
        scaleFactor = 0.1f.coerceAtLeast(scaleFactor.coerceAtMost(10.0f))
        view.scaleX = scaleFactor
        view.scaleY = scaleFactor
        view.invalidate()
        return true
    }

}

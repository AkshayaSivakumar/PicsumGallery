package com.experiment.android.picsumgallery.utils.customcomponents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

/**
 * Custom horizontal dotted progress bar
 */
class CustomDottedProgressBar : View {
    private val mDotRadius = 8
    private val mBounceDotRadius = 11
    private var mDotPosition = 0
    private val mDotAmount = 10
    private var paint: Paint = Paint()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.parseColor("#000000")
        createDot(canvas, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    private fun createDot(canvas: Canvas, paint: Paint) {
        for (i in 0 until mDotAmount) {
            if (i == mDotPosition) {
                canvas.drawCircle(
                    (10 + i * 30).toFloat(),
                    mBounceDotRadius.toFloat(),
                    mBounceDotRadius.toFloat(),
                    paint
                )
            } else {
                canvas.drawCircle(
                    (10 + i * 30).toFloat(),
                    mBounceDotRadius.toFloat(),
                    mDotRadius.toFloat(),
                    paint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = 32 * 9
        val height = mBounceDotRadius * 2
        setMeasuredDimension(width, height)
    }

    private fun startAnimation() {
        val bounceAnimation = BounceAnimation()
        bounceAnimation.duration = 200
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.interpolator = LinearInterpolator()
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {
                mDotPosition++
                if (mDotPosition == mDotAmount) {
                    mDotPosition = 0
                }
            }
        })
        startAnimation(bounceAnimation)
    }

    private inner class BounceAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            invalidate()
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }
}
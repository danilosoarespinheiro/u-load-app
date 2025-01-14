package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.*
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //  The Size of the button
    private var widthSize = 0
    private var heightSize = 0

    // The current pos of the animation for the button
    private var currentWidth = 0

    // The current pos of the animation for the loading circle
    private var currentDegree = 0

    private val valueAnimator = ValueAnimator()

    private var btnColor = 0
    private var btnTxtColor = 0

    private var btnTxt = DOWNLOAD_LABEL

    // To check if the download started or not
    private var download = false

    private var circle = RectF(0f, 0f, 0f, 0f)

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Style.FILL
        textAlign = Align.CENTER
        textSize = 60f
        typeface = Typeface.create("", Typeface.BOLD_ITALIC)
    }

    private var buttonState: ButtonState
            by Delegates.observable(ButtonState.Completed) { _, _, new ->
                when (new) {
                    ButtonState.Clicked -> {
                        loadAnimation()
                        download = true
                        btnTxt = context.getString(R.string.downloading)
                    }

                    ButtonState.Loading -> {
                        if (valueAnimator.isPaused) valueAnimator.resume()
                        download = true
                        btnTxt = context.getString(R.string.please_wait)
                        invalidate()
                    }

                    ButtonState.Completed -> {
                        valueAnimator.end()
                        download = false
                        btnTxt = context.getString(R.string.download)
                        invalidate()
                    }
                }
            }


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            try {
                btnColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
                btnTxtColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            } finally {
                recycle()
            }
        }
    }

    fun changeButtonState(buttonState: ButtonState) {
        (context as Activity).runOnUiThread { this.buttonState = buttonState }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = btnColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if (download) {
            paint.color = getColor(context, R.color.colorPrimaryDark)
            canvas?.drawRect(0f, 0f, currentWidth.toFloat(), heightSize.toFloat(), paint)

            paint.color = getColor(context, R.color.colorAccent)
            canvas?.drawArc(circle, -90f, currentDegree.toFloat(), true, paint)
        }

        paint.color = btnTxtColor
        canvas?.drawText(btnTxt, widthSize / 2f, heightSize.toFloat() / 1.70f, paint)
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)

        val left = newWidth - 150f
        val right = left + 70f
        val top = newHeight / 2 - 35f
        val bottom = top + 70

        circle = RectF(left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun loadAnimation() {
        valueAnimator.apply {
            setValues(
                PropertyValuesHolder.ofInt("rect", 0, widthSize),
                PropertyValuesHolder.ofInt("arc", 0, 360)
            )

            interpolator = LinearInterpolator()
            duration = 1500
            repeatCount = INFINITE
            repeatMode = RESTART

            addUpdateListener {
                currentWidth = it.getAnimatedValue("rect") as Int
                currentDegree = it.getAnimatedValue("arc") as Int
                invalidate()
            }
            start()
        }
    }
}
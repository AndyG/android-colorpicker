package com.angarron.colorpicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class ColorItem : FrameLayout, View.OnClickListener {

    private var mItemCheckmark: ImageView? = null
    private var mOutlineWidth = 0

    private var mColor: Int

    private var mIsSelected = false
    private var listener: OnColorSelectedListener? = null

    constructor(context: Context, color: Int, isSelected: Boolean) : super(context) {

        mColor = color
        mIsSelected = isSelected

        init()
        setChecked(mIsSelected)
    }

    fun setOnColorSelectedListener(listener: OnColorSelectedListener) {
        this.listener = listener
    }

    private fun updateDrawables() {
        foreground = createForegroundDrawable()
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(createBackgroundDrawable())
        } else {
            background = createBackgroundDrawable()
        }
    }

    private fun init() {
        updateDrawables()

        setOnClickListener(this)

        LayoutInflater.from(context).inflate(R.layout.color_item, this, true)
        mItemCheckmark = findViewById(R.id.selected_checkmark) as ImageView
        mItemCheckmark!!.setColorFilter(if (isColorDark(mColor)) Color.WHITE else Color.BLACK)
    }

    /**
     * Computes if the color is considered "dark"; used to determine if the foreground
     * image (the checkmark) should be white or black.
     *
     *
     * Based on http://stackoverflow.com/a/24810681/2444312.

     * @return true if the color is "dark"
     */
    private fun isColorDark(color: Int): Boolean {
        val brightness = Color.red(color) * 0.299 +
                Color.green(color) * 0.587 +
                Color.blue(color) * 0.114

        return brightness < 160
    }

    /**
     * Change the size of the outlining

     * @param width in px
     */
    fun setOutlineWidth(width: Int) {
        mOutlineWidth = width
        updateDrawables()
    }

    fun setChecked(checked: Boolean) {
        val oldChecked = mIsSelected
        mIsSelected = checked

        if (!oldChecked && mIsSelected) {
            // Animate checkmark appearance

            setItemCheckmarkAttributes(0.0f)
            mItemCheckmark!!.visibility = View.VISIBLE

            mItemCheckmark!!.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(250).setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    setItemCheckmarkAttributes(1.0f)
                }
            }).start()
        } else if (oldChecked && !mIsSelected) {
            // Animate checkmark disappearance

            mItemCheckmark!!.visibility = View.VISIBLE
            setItemCheckmarkAttributes(1.0f)

            mItemCheckmark!!.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(250).setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    mItemCheckmark!!.visibility = View.INVISIBLE
                    setItemCheckmarkAttributes(0.0f)
                }
            }).start()
        } else {
            // Just sync the view's visibility
            updateCheckmarkVisibility()
        }
    }

    private fun updateCheckmarkVisibility() {
        mItemCheckmark!!.visibility = if (mIsSelected) View.VISIBLE else View.INVISIBLE
        setItemCheckmarkAttributes(1.0f)
    }

    /**
     * Convenience method for simultaneously setting the alpha, X scale, and Y scale of a view

     * @param value the value to be set
     */
    private fun setItemCheckmarkAttributes(value: Float) {
        mItemCheckmark!!.alpha = value
        mItemCheckmark!!.scaleX = value
        mItemCheckmark!!.scaleY = value
    }

    override fun onClick(v: View) {
        setChecked(!mIsSelected)
        if (mIsSelected && listener != null) {
            listener!!.onColorSelected(mColor)
        }
    }

    private fun createBackgroundDrawable(): Drawable {
        val mask = GradientDrawable()
        mask.shape = GradientDrawable.OVAL
        if (mOutlineWidth != 0) {
            mask.setStroke(mOutlineWidth, if (isColorDark(mColor)) Color.WHITE else Color.BLACK)
        }
        mask.setColor(mColor)
        return mask
    }

    private fun createForegroundDrawable(): Drawable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use a ripple drawable
            val mask = GradientDrawable()
            mask.shape = GradientDrawable.OVAL
            mask.setColor(Color.BLACK)

            return RippleDrawable(ColorStateList.valueOf(getRippleColor(mColor)), null, mask)
        } else {
            // Use a translucent foreground
            val foreground = StateListDrawable()
            foreground.alpha = 80
            foreground.setEnterFadeDuration(250)
            foreground.setExitFadeDuration(250)

            val mask = GradientDrawable()
            mask.shape = GradientDrawable.OVAL
            mask.setColor(getRippleColor(mColor))
            foreground.addState(intArrayOf(android.R.attr.state_pressed), mask)

            foreground.addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))

            return foreground
        }
    }

    private fun getRippleColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.5f
        return Color.HSVToColor(hsv)
    }
}

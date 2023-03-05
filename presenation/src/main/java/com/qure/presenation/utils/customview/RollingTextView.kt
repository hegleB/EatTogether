package com.qure.presenation.utils.customview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import com.qure.presenation.R
import com.qure.presenation.databinding.CustomRollingTextBinding
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy


@BindingMethods(
    value = [
        BindingMethod(
            type = RollingTextView::class,
            method = "setRollingText",
            attribute = "app:rollingText"
        )]
)


class RollingTextView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attr, defStyleAttr) {

    private var binding: CustomRollingTextBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_rolling_text,
        this,
        true
    )

    init {
        attr?.let {
            getAttrs(it)
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attr: AttributeSet) {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.RollingCountTextView)

        setTypeArray(typeArray)
    }

    private fun setTypeArray(typeArray: TypedArray) {
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            12f, context.resources.displayMetrics)
        val rollingTextSize = typeArray.getDimension(R.styleable.RollingCountTextView_rolling_textSize, textSize)
        binding.rollingText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rollingTextSize)
        val textResId = typeArray.getString(R.styleable.RollingCountTextView_rolling_type)
        val typeTextSize = typeArray.getDimension(R.styleable.RollingCountTextView_rolling_type_textSize, textSize)
        binding.rollingType.text = textResId
        binding.rollingType.setTextSize(TypedValue.COMPLEX_UNIT_PX, typeTextSize)
        typeArray.recycle()
    }

    fun setRollingText(count: String?) {
        val rollingTextView = binding.rollingText
        val animator = ValueAnimator.ofInt(0, 0)
        rollingTextView.animationDuration = 1000L
        rollingTextView.charStrategy = Strategy.CarryBitAnimation(Direction.SCROLL_UP)
        rollingTextView.addCharOrder(CharOrder.Number)
        rollingTextView.animationInterpolator =
            AccelerateDecelerateInterpolator()
        animator.animatedFraction
        animator.addUpdateListener { animation -> rollingTextView.setText(animation.animatedValue.toString()) }
        animator.start()
        val handler = Handler()
        handler.postDelayed({
            animator.cancel()
            rollingTextView.setText(count ?: "0")
        }, 0)
    }
}

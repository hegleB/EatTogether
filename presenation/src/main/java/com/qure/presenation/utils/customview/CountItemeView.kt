package com.qure.presenation.utils.customview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import com.qure.presenation.R
import com.qure.presenation.databinding.CustomCountItemBinding

@BindingMethods(
    BindingMethod(
        type = CountItemeView::class,
        method = "setItemBackground",
        attribute = "app:bg"
    ),
    BindingMethod(
        type = CountItemeView::class,
        method = "setItemBackgroundTint",
        attribute = "app:bgTint"
    ),
    BindingMethod(
        type = CountItemeView::class,
        method = "setItemCountText",
        attribute = "app:text"
    ),
)

class CountItemeView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attr, defStyleAttr) {

    private val binding: CustomCountItemBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_count_item,
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
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.CountItemView)
        setTypeArray(typeArray)
    }

    private fun setTypeArray(typeArray: TypedArray) {
        val backgroundResId =
            typeArray.getResourceId(R.styleable.CountItemView_bg, R.drawable.ic_faces)
        val backgorundTintResId =
            typeArray.getResourceId(R.styleable.CountItemView_bgTint, R.color.gray2)
        val textResId = typeArray.getString(R.styleable.CountItemView_text)

        binding.apply {
            countItemImage.setImageResource(backgroundResId)
            countItemImage.setColorFilter(ContextCompat.getColor(context, backgorundTintResId))
            countItemNumber.text = textResId
        }
        typeArray.recycle()
    }

    fun setItemBackground(image: Drawable?) {
        binding.countItemImage.setImageDrawable(image!!)
    }

    fun setItemBackgroundTint(imageTint: Int?) {
        binding.countItemImage.setColorFilter(imageTint!!)
    }

    fun setItemCountText(count: String?) {
        binding.countItemNumber.setText(count)
    }
}
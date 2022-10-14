package com.qure.presenation.adapter.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.qure.presenation.R

object ImageBindingAdapter {

    @BindingAdapter("userImage")
    @JvmStatic
    fun userImage(imageView: ImageView, uri : String?){
        Glide.with(imageView.context).load(uri).error(R.drawable.ic_user).into(imageView)
    }


}
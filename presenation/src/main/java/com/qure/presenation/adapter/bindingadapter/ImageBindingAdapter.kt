package com.qure.presenation.adapter.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qure.presenation.R

object ImageBindingAdapter {

    @BindingAdapter("userImage")
    @JvmStatic
    fun userImage(imageView: ImageView, uri: String?) {
        Glide.with(imageView.context).load(uri).error(R.drawable.ic_user).into(imageView)
    }

    @BindingAdapter("postCreateImage")
    @JvmStatic
    fun postCreateImage(imageView: ImageView, uri: String?) {
        Glide.with(imageView.context).load(uri).into(imageView)
    }

    @BindingAdapter("barcodeImage")
    @JvmStatic
    fun barcodeImage(imageView: ImageView, text: String?) {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        Glide.with(imageView.context).load(bitmap).into(imageView)
    }

    @BindingAdapter("postImage")
    @JvmStatic
    fun postImage(imageView: ImageView, image: String?) {
        Glide.with(imageView.context).load(image).into(imageView)
    }

    @BindingAdapter("setNavigationOnClick")
    @JvmStatic
    fun MaterialToolbar.setNavigationOnClick(back: () -> Unit) {
        setNavigationOnClickListener {
            back()
        }
    }

    @BindingAdapter("setNavigationIcon")
    @JvmStatic
    fun MaterialToolbar.setNavigationIcon(isVisiable: Boolean) {
        if (isVisiable) {
            setNavigationIcon(R.drawable.ic_back)
        }
    }
    @BindingAdapter(value = ["deleteCreateImage", "item"], requireAll = false)
    @JvmStatic
    fun deleteCreateImage(imageView: ImageView, imageList: ArrayList<String>, item: String) {
        imageView.setOnClickListener {
            imageList.remove(item)
        }
    }

    @BindingAdapter("postCategoryImage")
    @JvmStatic
    fun postCategoryImage(imageView: ImageView, categoryName: String) {
        val categoryImages = mapOf(
            "한식" to R.drawable.img_korean_food,
            "중식" to R.drawable.img_chinese_food,
            "분식" to R.drawable.img_school_food,
            "일식" to R.drawable.img_sushi,
            "양식" to R.drawable.img_spagetti,
            "치킨" to R.drawable.img_chicken,
            "피자" to R.drawable.img_pizza,
            "카페/디저트" to R.drawable.img_cake,
            "기타" to R.color.orange2,
        )
        val image = categoryImages[categoryName] ?: R.drawable.img_error
        imageView.setImageResource(image)
    }
}

package com.qure.presenation.adapter.bindingadapter

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qure.presenation.R
import de.hdodenhof.circleimageview.CircleImageView

object ImageBindingAdapter {

    @BindingAdapter("userImage")
    @JvmStatic
    fun userImage(imageView: ImageView, uri : String?){
        Glide.with(imageView.context).load(uri).error(R.drawable.ic_user).into(imageView)
    }

    @BindingAdapter("barcodeImage")
    @JvmStatic
    fun barcodeImage(imageView: ImageView, text : String?){
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        Glide.with(imageView.context).load(bitmap).into(imageView)
    }

    @BindingAdapter("postImage")
    @JvmStatic
    fun postImage(imageView: ImageView, image : String?){
        Glide.with(imageView.context).load(image).into(imageView)
    }
}
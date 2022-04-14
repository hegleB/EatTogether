package com.qure.eattogether2.bindingadapter

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


object ImageBindingAdapter {



    @BindingAdapter("userImage")
    @JvmStatic
    fun userImage(imageView: ImageView, uri : String?){

        Glide.with(imageView.context).load(uri).error(R.drawable.ic_user).into(imageView)
    }


    @BindingAdapter("postImage")
    @JvmStatic
    fun postImage(imageView: ImageView, uri: String?) {

        Glide.with(imageView.context).load(uri).centerCrop().listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }

        }).into(imageView)
    }

    @BindingAdapter("postCategoryImage")
    @JvmStatic
    fun postCategoryImage(imageView : ImageView, categoryName: String){

        var image : Int = 0

        when(categoryName){

            "한식" -> {
                image = R.drawable.img_korean_food
            }

            "중식" -> {
                image = R.drawable.img_chinese_food
            }

            "분식" -> {
                image = R.drawable.img_school_food
            }

            "일식" -> {
                image = R.drawable.img_sushi
            }

            "양식" -> {
                image = R.drawable.img_spagetti
            }

            "치킨" -> {
                image = R.drawable.img_chicken
            }

            "피자" -> {
                image = R.drawable.img_pizza
            }

            "카페/디저트" -> {
                image = R.drawable.img_cake
            }

            "기타" -> {
                image = R.color.orange2
            } else -> {
                image = R.drawable.img_error
            }
        }


        imageView.setImageResource(image)

    }





}
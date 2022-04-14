package com.qure.eattogether2.adapter

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qure.eattogether2.databinding.ItemCreateImageBinding

class CreatePostImageAdapter(val context : Context,val imageList: MutableList<Uri>, val onClickDeleteIcon: (uri : Uri) -> Unit) : RecyclerView.Adapter<CreatePostImageAdapter.ViewHolder>() {

    var list : MutableList<Uri> = imageList

    inner class ViewHolder(val binding: ItemCreateImageBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(image: Uri){

            System.out.println("어댑터이미지: "+ image)
            Glide.with(context).load(image).fitCenter().into(binding.createImage)

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCreateImageBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.createDeleteImage.setOnClickListener {
            onClickDeleteIcon.invoke(list[position])
        }
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
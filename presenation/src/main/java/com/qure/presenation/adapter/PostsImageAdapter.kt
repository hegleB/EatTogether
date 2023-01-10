package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qure.domain.model.PostModel.PostImage
import com.qure.presenation.databinding.ItemPostImageBinding

class PostsImageAdapter : RecyclerView.Adapter<PostsImageAdapter.ViewHolder>() {

    var imageList: List<String> = listOf()

    inner class ViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            binding.image = PostImage(image)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostImageBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
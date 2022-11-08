package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qure.presenation.databinding.ItemCreateCategoryBinding

class PostCreateCategotyAdapter(val itemClick: (String) -> Unit, val categoryList: Array<String>) :
    RecyclerView.Adapter<PostCreateCategotyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCreateCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String) {

            binding.apply {
                itemView.setOnClickListener {
                    itemClick(name)
                }
            }
            binding.name = name
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemCreateCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
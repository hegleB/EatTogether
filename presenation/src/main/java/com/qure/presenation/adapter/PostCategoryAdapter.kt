package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qure.presenation.databinding.ItemPostCategoryBinding

class PostCategoryAdapter(val categoryList: Array<String>, val itemclick: (String) -> Unit) :
    RecyclerView.Adapter<PostCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPostCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String) {
            binding.textViewItemPostCategoryFood.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostCategoryBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
        holder.itemView.setOnClickListener {
            itemclick(categoryList[position])
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}

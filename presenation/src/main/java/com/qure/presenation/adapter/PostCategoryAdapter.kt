package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.qure.domain.model.User
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemPeopleBinding
import com.qure.presenation.databinding.ItemPostCategoryBinding

class PostCategoryAdapter(val categoryList: Array<String>, val itemclick : () -> Unit) : RecyclerView.Adapter<PostCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPostCategoryBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(name: String){

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
            itemclick
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
package com.qure.eattogether2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qure.eattogether2.databinding.ItemCategoryFoodBinding
import com.qure.eattogether2.databinding.ItemCreateCategoryBinding

class PostCategoryAdapter(val categoryList: Array<String>) : RecyclerView.Adapter<PostCategoryAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var callBack: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.callBack = itemClickListener
    }

    inner class ViewHolder(val binding: ItemCategoryFoodBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(name: String){

            binding.categoryFood.text = name


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryFoodBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(categoryList[position])
        holder.itemView.setOnClickListener {
            callBack.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
package com.qure.presenation.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<VB: ViewDataBinding, E: Any>(protected val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    val context: Context
        get() { return itemView.context }

    lateinit var element: E

    open fun bind(element: E) {
        this.element = element
    }
}
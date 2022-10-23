package com.qure.presenation.base


import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseAdapter<E: Any>
    (itemCallback: DiffUtil.ItemCallback<E>)
    : ListAdapter<E, BaseViewHolder<out ViewDataBinding, E>>(itemCallback) {

    override fun onBindViewHolder(holder: BaseViewHolder<out ViewDataBinding, E>, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}
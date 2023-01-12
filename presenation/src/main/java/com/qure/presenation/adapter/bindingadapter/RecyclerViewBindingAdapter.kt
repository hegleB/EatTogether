package com.qure.presenation.adapter.bindingadapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qure.domain.model.Comments
import com.qure.presenation.adapter.PeopleAdapter
import com.qure.presenation.adapter.PostsImageAdapter
import com.qure.presenation.adapter.ReCommentsAdapter

object RecyclerViewBindingAdapter {

    @BindingAdapter("postImageAdapter")
    @JvmStatic
    fun postImageAdapter(recyclerView: RecyclerView, data: List<String>) {
        val adapter = PostsImageAdapter()
        recyclerView.adapter = adapter
        adapter.imageList = data
    }
}
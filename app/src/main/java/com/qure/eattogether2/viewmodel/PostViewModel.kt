package com.qure.eattogether2.viewmodel

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.paging.CommentsPagingSource
import com.qure.eattogether2.paging.PeoplePagingSource
import com.qure.eattogether2.paging.PostPagingSource
import com.qure.eattogether2.repository.PeopleRepository.Companion.TAG
import com.qure.eattogether2.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named


class PostViewModel @ViewModelInject constructor(

    private val postRepository: PostRepository

) : ViewModel() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private var _allPosts = MutableLiveData<MutableList<Post>>()
    val allPosts: LiveData<MutableList<Post>> get() = _allPosts

    private var _allCategoryPosts = MutableLiveData<MutableList<Post>>()
    val allCategoryPosts: LiveData<MutableList<Post>> get() = _allCategoryPosts

    private var _image = MutableLiveData<MutableList<Uri>>()
    val image: MutableLiveData<MutableList<Uri>> get() = _image

    private var _isTrue = MutableLiveData<Boolean>()
    val isTrue: MutableLiveData<Boolean> get() = _isTrue

    var postKey: String = ""

    private var _likeCount = MutableLiveData<Int>()
    val likeCount: MutableLiveData<Int> get() = _likeCount

    var categoryName : String = ""


    init {


        CoroutineScope(Dispatchers.IO).launch {

            postRepository.getAllPosts().collect {

                _allPosts.postValue(it.toMutableList())
            }
        }

    }


    val getPosts = Pager(
        PagingConfig(
            pageSize = 30
        )
    ) {
        PostPagingSource(allPosts)
    }.flow.cachedIn(viewModelScope)



    fun getComments(allComments: LiveData<List<Comments>>) = Pager(
        PagingConfig(
            pageSize = 30
        )
    ) {
        CommentsPagingSource(allComments)
    }.flow.cachedIn(viewModelScope)



    fun getReply(allComments: LiveData<List<Comments>>) = Pager(
        PagingConfig(
            pageSize = 30
        )
    ) {

        CommentsPagingSource(allComments)
    }.flow.cachedIn(viewModelScope)


}
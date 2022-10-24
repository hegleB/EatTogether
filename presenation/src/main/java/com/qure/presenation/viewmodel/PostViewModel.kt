package com.qure.presenation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel.Post
import com.qure.domain.model.User
import com.qure.domain.usecase.comment.*
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.usecase.post.CheckPostUseCase
import com.qure.domain.usecase.post.GetAllPostUseCase
import com.qure.domain.usecase.post.SetPostUseCase
import com.qure.domain.usecase.post.UpdateLikeCountUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getAllPostUseCase: GetAllPostUseCase,
    private val setPostUseCase: SetPostUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val setCommentsUseCase: SetCommentsUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikeCountUseCase: UpdateLikeCountUseCase,
    private val updateCommentLikeUseCase: UpdateCommentLikeUseCase,
    private val updateRecommentLikeUseCase: UpdateRecommentLikeUseCase,
    private val checkPostUseCase: CheckPostUseCase,
    private val checkCommentUseCase: CheckCommentUseCase,
    private val checkReCommentUseCase: CheckReCommentUseCase,
    private val getReCommentsUseCase: GetReCommentsUseCase,
    private val setReCommentsUseCase: SetReCommentsUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BaseViewModel() {

    val currentUid = firebaseAuth.currentUser?.uid ?: ""

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post>
        get() = _post

    private val _postList: MutableLiveData<List<Post>> = MutableLiveData()
    val postList: LiveData<List<Post>>
        get() = _postList

    private val _writerComment: MutableLiveData<User> = MutableLiveData()
    val writerComment: LiveData<User>
        get() = _writerComment

    private val _postKey: MutableLiveData<String> = MutableLiveData()
    val postKey: LiveData<String>
        get() = _postKey

    private val _commentKey: MutableLiveData<String> = MutableLiveData()
    val commentKey: LiveData<String>
        get() = _commentKey

    private val _comment: MutableLiveData<Comments> = MutableLiveData()
    val comment: LiveData<Comments>
        get() = _comment

    private val _recomment: MutableLiveData<Comments> = MutableLiveData()
    val recomment: LiveData<Comments>
        get() = _recomment

    private val _commentsList: MutableLiveData<List<Comments>> = MutableLiveData()
    val commentsList: LiveData<List<Comments>>
        get() = _commentsList

    private val _recommentsList: MutableLiveData<List<Comments>> = MutableLiveData()
    val recommentsList: MutableLiveData<List<Comments>>
        get() = _recommentsList

    private val _buttonSendComment: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSendComment: LiveData<Event<Unit>>
        get() = _buttonSendComment

    private val _buttonSendReComment: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSendReComment: LiveData<Event<Unit>>
        get() = _buttonSendReComment

    private val _buttonCommentLike: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonCommentLike: LiveData<Event<Unit>>
        get() = _buttonCommentLike

    private val _isLike: MutableLiveData<Boolean> = MutableLiveData()
    val isLike: LiveData<Boolean>
        get() = _isLike

    private val _isCommentLike: MutableLiveData<Boolean> = MutableLiveData()
    val isCommentLike: LiveData<Boolean>
        get() = _isCommentLike

    private val _isReCommentLike: MutableLiveData<Boolean> = MutableLiveData()
    val isReCommentLike: LiveData<Boolean>
        get() = _isReCommentLike

    private val _likeList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val likeList: LiveData<ArrayList<String>>
        get() = _likeList

    private val _commentsLikeList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val commentsLikeList: LiveData<ArrayList<String>>
        get() = _commentsLikeList

    private val _reCommentsLikeList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val reCommentsLikeList: LiveData<ArrayList<String>>
        get() = _reCommentsLikeList

    private val _buttonLike: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonLike: LiveData<Event<Unit>>
        get() = _buttonLike

    private val _snackBarMsg: MutableLiveData<PeopleViewModel.MessageSet> = MutableLiveData()
    val snackBarMsg: LiveData<PeopleViewModel.MessageSet>
        get() = _snackBarMsg

    private val _postCreate: MutableLiveData<Event<Unit>> = MutableLiveData()
    val postCreate: LiveData<Event<Unit>>
        get() = _postCreate

    private val _editTextPostComment: MutableLiveData<String> = MutableLiveData("")
    val editTextPostComment: MutableLiveData<String>
        get() = _editTextPostComment

    private val _toolbarBack: MutableLiveData<Event<Unit>> = MutableLiveData()
    val toolbarBack: LiveData<Event<Unit>>
        get() = _toolbarBack

    private val _postImageList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val postImageList: LiveData<List<String>>
        get() = _postImageList

    fun getAllPost() = viewModelScope.launch {
        getAllPostUseCase()
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _postList.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getUserInfo() = viewModelScope.launch {
        getUserInfoUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _writerComment.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }


    fun getComments() = viewModelScope.launch {
        getCommentsUseCase(_postKey.value ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _commentsList.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getReComments(recomment: Comments) = viewModelScope.launch {
        getReCommentsUseCase(recomment)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _recommentsList.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun checkPost() = viewModelScope.launch {

        checkPostUseCase(_postKey.value ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _post.value = it.data
                        _likeList.value = it.data?.likecount ?: arrayListOf()
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun checkComment(commentKey: String) = viewModelScope.launch {
        checkCommentUseCase(commentKey)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _comment.value = it.data
                        _commentsLikeList.value = it.data?.comments_likeCount ?: arrayListOf()
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value =
                        PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }

    }

    fun checkReComment(recomment : Comments) = viewModelScope.launch {
        _recomment.value = recomment
        checkReCommentUseCase(recomment)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _reCommentsLikeList.value = it.data?.comments_likeCount ?: arrayListOf()
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value =
                        PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }

    }


    fun checkLike(likeList: ArrayList<String>) {
        _isLike.value = likeList.contains(currentUid)
    }

    fun checkCommentLike(likeList: ArrayList<String>) {
        _isCommentLike.value = likeList.contains(currentUid)
    }

    fun updateLikeState() {
        when (_isLike.value ?: false) {
            true -> {
                removeLikeCount()
                _isLike.value = false
            }
            else -> {
                addlikeCount()
                _isLike.value = true
            }
        }
    }

    fun updateCommentLikeState() {
        when (_isCommentLike.value ?: false) {
            true -> {
                removeCommentLikeCount()
                _isCommentLike.value = false
            }
            else -> {
                addCommentlikeCount()
                _isCommentLike.value = true
            }
        }
    }

    fun updateReCommentLikeState() {
        when (_isReCommentLike.value ?: false) {
            true -> {
                removeReCommentLikeCount()
                _isReCommentLike.value = false
            }
            else -> {
                addReCommentlikeCount()
                _isReCommentLike.value = true
            }
        }
    }

    fun addlikeCount() = viewModelScope.launch {
        val likeList = _likeList.value ?: arrayListOf()

        likeList.add(currentUid)

        updateLikeCountUseCase(likeList, _postKey.value ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> hideProgress()
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun addCommentlikeCount() = viewModelScope.launch {
        val likeList = _commentsLikeList.value ?: arrayListOf()

        likeList.add(currentUid)
        if (commentKey.value != null) {
            updateCommentLikeUseCase(_commentKey.value ?: "", likeList)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun addReCommentlikeCount() = viewModelScope.launch {
        val likeList = _reCommentsLikeList.value ?: arrayListOf()

        likeList.add(currentUid)
        if (_recomment.value != null) {
            updateRecommentLikeUseCase(_recomment.value!!, likeList)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun removeLikeCount() = viewModelScope.launch {

        val likeList = _likeList.value ?: arrayListOf()
        likeList.remove(currentUid)
        updateLikeCountUseCase(likeList, _postKey.value ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> hideProgress()
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun removeCommentLikeCount() = viewModelScope.launch {

        val likeList = _commentsLikeList.value ?: arrayListOf()
        likeList.remove(currentUid)
        if (_commentKey.value != null) {

            updateCommentLikeUseCase(_commentKey.value ?: "", likeList)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun removeReCommentLikeCount() = viewModelScope.launch {

        val likeList = _reCommentsLikeList.value ?: arrayListOf()
        likeList.remove(currentUid)
        if (_recomment.value != null) {
            updateRecommentLikeUseCase(_recomment.value!!, likeList)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun writeComments(content: String) = viewModelScope.launch {
        val commentId = firestore.collection("comments").document().id

        if (writerComment.value != null) {
            val writer = writerComment.value!!
            val recomments = Comments(
                writer.uid,
                writer.usernm,
                writer.userphoto,
                content,
                System.currentTimeMillis().toString(),
                System.currentTimeMillis().toString(),
                arrayListOf(),
                postKey.value ?: "",
                commentId,
                0
            )
            setCommentsUseCase(recomments)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun writeReComments(content: String) = viewModelScope.launch {
        val commentId = _recomment.value?.comments_commentskey?:""
        Log.d("commentId", "${commentId}")
        if (writerComment.value != null) {
            val writer = writerComment.value!!
            val comments = Comments(
                writer.uid,
                writer.usernm,
                writer.userphoto,
                content,
                System.currentTimeMillis().toString(),
                System.currentTimeMillis().toString(),
                arrayListOf(),
                _recomment.value?.comments_postkey ?: "",
                commentId,
                1
            )
            setReCommentsUseCase(comments)
                .collect {
                    when (it) {
                        is Resource.Success -> hideProgress()
                        is Resource.Loading -> showProgress()
                        is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                        is Resource.Empty -> _snackBarMsg.value =
                            PeopleViewModel.MessageSet.EMPTY_QUERY
                    }
                }
        }
    }

    fun showPostCreate() {
        _postCreate.value = Event((Unit))
    }

    fun backPostDetail() {
        _toolbarBack.value = Event(Unit)
    }

    fun getPostList(postImageList: List<String>) {
        _postImageList.value = postImageList
    }

    fun sendComment(commentText: String) {
        _buttonSendComment.value = Event(Unit)
        writeComments(commentText)
    }

    fun sendReComment(recommentText: String) {
        _buttonSendReComment.value = Event(Unit)
        writeReComments(recommentText)
    }

    fun getPostKey(postKey: String) {
        _postKey.value = postKey
    }

    fun getCommentsKey(commentKey: String) {
        _commentKey.value = commentKey
    }

    fun getCommentLike(isCommentLike : Boolean) {
        _isCommentLike.value = isCommentLike
    }

    fun getCommentLkeList(commentLikeList : ArrayList<String>) {
        _commentsLikeList.value = commentLikeList
    }

    fun getReCommentList(recommentList : List<Comments>) {
        _recommentsList.value = recommentList
    }

    fun getReCommentLike(isReCommentLike : Boolean) {
        _isReCommentLike.value = isReCommentLike
    }

    fun getReCommentLkeList(recommentLikeList : ArrayList<String>) {
        _reCommentsLikeList.value = recommentLikeList
    }

}
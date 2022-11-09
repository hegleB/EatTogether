package com.qure.presenation.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel.Post
import com.qure.domain.model.User
import com.qure.domain.usecase.comment.*
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.usecase.post.*
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
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
    private val getCategoryPostUseCase: GetCategoryPostUseCase,
    private val getProfileCreatedPostsUseCase: GetProfileCreatedPostsUseCase,
    private val getProfileLikedPostsUseCase: GetProfileLikedPostsUseCase,
    private val getProfileCommentsCreatedPostsUseCase: GetProfileCommentsCreatedPostsUseCase,
    private val updateCommentsCountUseCase: UpdateCommentsCountUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : BaseViewModel() {

    val currentUid = firebaseAuth.currentUser?.uid ?: ""

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post>
        get() = _post

    private val _category: MutableLiveData<String> = MutableLiveData("카테고리 선택")
    val category: LiveData<String>
        get() = _category

    private val _categoryName: MutableLiveData<String> = MutableLiveData("")
    val categoryName: LiveData<String>
        get() = _categoryName

    private val _postList: MutableLiveData<List<Post>> = MutableLiveData()
    val postList: LiveData<List<Post>>
        get() = _postList

    private val _categoryPostList: MutableLiveData<List<Post>> = MutableLiveData()
    val categoryPostList: LiveData<List<Post>>
        get() = _categoryPostList

    private val _profileCreatedPost: MutableLiveData<List<Post>> = MutableLiveData()
    val profileCreatedPost: LiveData<List<Post>>
        get() = _profileCreatedPost

    private val _profileLikedPost: MutableLiveData<List<Post>> = MutableLiveData()
    val profileLikedPost: LiveData<List<Post>>
        get() = _profileLikedPost

    private val _profileCommentsPost: MutableLiveData<List<Post>> = MutableLiveData()
    val profileCommentsPost: LiveData<List<Post>>
        get() = _profileCommentsPost

    private val _writer: MutableLiveData<User> = MutableLiveData()
    val writer: LiveData<User>
        get() = _writer

    private val _profileUid: MutableLiveData<String> = MutableLiveData()
    val profileUid: LiveData<String>
        get() = _profileUid

    private val _buttonDeleteImage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonDeleteImage: LiveData<Event<Unit>>
        get() = _buttonDeleteImage

    val createPostTitle: MutableLiveData<String> = MutableLiveData()
    val createPostContent: MutableLiveData<String> = MutableLiveData()

    private val _createPostKey: MutableLiveData<String> = MutableLiveData()
    val createPostKey: MutableLiveData<String>
        get() = _createPostKey

    private val _updatedState: MutableLiveData<Resource<String, String>> = MutableLiveData()
    val updatedState: MutableLiveData<Resource<String, String>>
        get() = _updatedState

    private val _createPostImage: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val createPostImage: MutableLiveData<ArrayList<String>>
        get() = _createPostImage


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

    private val _buttonCategory: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonCategory: LiveData<Event<Unit>>
        get() = _buttonCategory

    private val _buttonUploadImage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonUploadImage: LiveData<Event<Unit>>
        get() = _buttonUploadImage

    private val _toolbarPostCreate: MutableLiveData<Event<Unit>> = MutableLiveData()
    val toolbarPostCreate: LiveData<Event<Unit>>
        get() = _toolbarPostCreate

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

    fun getCategoryPost() = viewModelScope.launch {

        getCategoryPostUseCase(categoryName.value ?: "")
            .collect {
                when(it) {
                    is Resource.Success -> {
                        _categoryPostList.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getProfileCreatedPosts() = viewModelScope.launch {
        getProfileCreatedPostsUseCase(profileUid.value ?: "")
            .collect {
                when(it) {
                    is Resource.Success -> {
                        _profileCreatedPost.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getProfileLikedPosts() = viewModelScope.launch {
        getProfileLikedPostsUseCase(profileUid.value ?: "")
            .collect {
                when(it) {
                    is Resource.Success -> {
                        _profileLikedPost.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getProfileCommentsCreatedPosts() = viewModelScope.launch {
        getProfileCommentsCreatedPostsUseCase(profileUid.value ?: "")
            .collect {
                when(it) {
                    is Resource.Success -> {
                        _profileCommentsPost.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = PeopleViewModel.MessageSet.EMPTY_QUERY
                }
            }
    }

    fun setPost() = viewModelScope.launch {
        _createPostKey.value = firestore.collection("posts").document().id
        val post = Post(
            currentUid,
            _writer.value?.usernm ?: "",
            createPostTitle.value ?: "",
            category.value ?: "",
            createPostContent.value ?: "",
            _writer.value?.userphoto ?: "",
            System.currentTimeMillis().toString(),
            _createPostKey.value ?: "",
            arrayListOf(),
            "0",
            arrayListOf()
        )

        setPostUseCase(post)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _snackBarMsg.value = PeopleViewModel.MessageSet.SUCCESS
                        createPost()
                    }
                    is Resource.Error -> _snackBarMsg.value = PeopleViewModel.MessageSet.ERROR
                }
            }
    }


    fun getUserInfo() = viewModelScope.launch {
        getUserInfoUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _writer.value = it.data
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

    fun updateCommentsCount(count : String) = viewModelScope.launch {
        updateCommentsCountUseCase(_postKey.value ?: "", count)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _snackBarMsg.value = PeopleViewModel.MessageSet.SUCCESS
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

    fun checkReComment(recomment: Comments) = viewModelScope.launch {
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

        if (writer.value != null) {
            val writer = writer.value!!
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
        val commentId = _recomment.value?.comments_commentskey ?: ""
        if (writer.value != null) {
            val writer = writer.value!!
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

    fun createPost() = viewModelScope.launch {
        val key = createPostKey.value ?: ""
        try {
            val createImages = createPostImage.value ?: arrayListOf()
            val imageList = arrayListOf<String>()
            if (createImages.isNotEmpty()) {
                _updatedState.value = Resource.Loading()
                for (i in createImages.indices) {
                    val riverRef: StorageReference =
                        firebaseStorage.getReference()
                            .child("post_image/" + key + "/" + i + ".jpg")
                    val uploadTask: UploadTask = riverRef.putFile(createImages.get(i).toUri())
                    uploadTask.addOnSuccessListener {
                        riverRef.downloadUrl.addOnSuccessListener { uri ->
                            imageList.add(uri.toString())
                            firestore.collection("posts").document(key).update("postImages", imageList)

                        }
                    }
                    uploadTask.addOnProgressListener {
                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                        if (progress == 100.0) {
                            _updatedState.value = Resource.Success("업로드 성공")
                            _createPostImage.value = arrayListOf()
                            createPostTitle.value = ""
                            _category.value = "카테고리 선택"
                            createPostContent.value = ""
                        }
                    }
                }

            } else {
                _updatedState.value = Resource.Success("")
                _createPostImage.value = arrayListOf()
                createPostTitle.value = ""
                _category.value = "카테고리 선택"
                createPostContent.value = ""
            }

        } catch (e: Exception) {
            _updatedState.value = Resource.Error(e.message)
        }
    }

    fun showPostCreate() {
        _postCreate.value = Event((Unit))
    }

    fun backPostDetail() {
        _toolbarBack.value = Event(Unit)
    }

    fun backPostCreat() {
        _toolbarBack.value = Event(Unit)
    }

    fun backPostCategory() {
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

    fun deletePostCreateImage(image: String) {
        val images = createPostImage.value ?: arrayListOf()
        images.remove(image)
        _createPostImage.value = images
    }

    fun getReCommentLike(isReCommentLike: Boolean) {
        _isReCommentLike.value = isReCommentLike
    }

    fun getReCommentLkeList(recommentLikeList: ArrayList<String>) {
        _reCommentsLikeList.value = recommentLikeList
    }

    fun getPostCreateImage(images: ArrayList<String>) {
        _createPostImage.value = images
    }

    fun buttonUploadImage() {
        _buttonUploadImage.value = Event(Unit)
        _createPostImage.value = arrayListOf()
    }

    fun getCategory(category: String) {
        _category.value = category
    }

    fun getCategoryName(category: String) {
        _categoryName.value = category
    }

    fun getProfileUid(uid: String) {
        _profileUid.value = uid
    }

    fun moveToPostCreateCategory() {
        _buttonCategory.value = Event(Unit)
    }

    fun setUpdatedState(resource: Resource<String, String>) {
        _updatedState.value = resource
    }
}
package com.qure.presenation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.qure.domain.model.PostModel
import com.qure.domain.model.PostModel.Post
import com.qure.domain.model.User
import com.qure.domain.repository.*
import com.qure.domain.usecase.CommentUseCase
import com.qure.domain.usecase.PostUseCase
import com.qure.domain.usecase.UserUseCase
import com.qure.domain.utils.*
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val commentUseCase: CommentUseCase,
    private val postUseCase: PostUseCase,
    private val userUseCase: UserUseCase,
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

    private val _isLike: MutableLiveData<Boolean> = MutableLiveData()
    val isLike: LiveData<Boolean>
        get() = _isLike

    private val _isCommentLike: MutableLiveData<Boolean> = MutableLiveData()

    private val _likeList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val likeList: LiveData<ArrayList<String>>
        get() = _likeList

    private val _commentsLikeList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val commentsLikeList: LiveData<ArrayList<String>>
        get() = _commentsLikeList

    private val _reCommentsLikeList: MutableLiveData<ArrayList<String>> = MutableLiveData()

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

    private val _postDetailImageList: MutableLiveData<List<PostModel.PostImage>> = MutableLiveData()
    val postDetailImageList: LiveData<List<PostModel.PostImage>>
        get() = _postDetailImageList

    var addComments by mutableStateOf<AddComments>(Resource.Success(false))
        private set

    var addReComments by mutableStateOf<AddReComments>(Resource.Success(false))
        private set

    var updateCommentsLike by mutableStateOf<UpdateCommentsLike>(Resource.Success(false))
        private set

    var updateCommentsCount by mutableStateOf<UpdateCommentsCount>(Resource.Success(false))
        private set

    var addPost by mutableStateOf<AddPost>(Resource.Success(false))
        private set

    var updateLike by mutableStateOf<UpdateLike>(Resource.Success(false))
        private set

    fun getAllPost() = viewModelScope.launch {
        postUseCase.getAllPost()
            .collect {
                when (it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _postList.value = it.data
                        hideProgress()
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun getCategoryPost() = viewModelScope.launch {
        postUseCase.getCategoryPost(categoryName.value ?: "")
            .collect {
                when (it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _categoryPostList.value = it.data
                        hideProgress()
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun getProfileCreatedPosts() = viewModelScope.launch {
        postUseCase.getProfileCreatedPosts(profileUid.value ?: "")
            .collect {
                when (it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _profileCreatedPost.value = it.data
                        hideProgress()
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun getProfileLikedPosts() = viewModelScope.launch {
        postUseCase.getProfileLikedPosts(profileUid.value ?: "").collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _profileLikedPost.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun getProfileCommentsCreatedPosts() = viewModelScope.launch {
        postUseCase.getProfileCommentsCreatedPosts(profileUid.value ?: "").collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _profileCommentsPost.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }


    fun getUserInfo() = viewModelScope.launch {
        userUseCase.getUser(currentUid).collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _writer.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }


    fun getComments() = viewModelScope.launch {
        commentUseCase.getComments(_postKey.value ?: "").collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _commentsList.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun updateCommentsCount(count: String) = viewModelScope.launch {
        updateCommentsCount = Resource.Loading()
        updateCommentsCount = commentUseCase.updateCommentsCount(_postKey.value ?: "", count)
    }

    fun getReComments(recomment: Comments) = viewModelScope.launch {
        commentUseCase.getReComments(recomment).collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _recommentsList.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun checkPost() = viewModelScope.launch {
        postUseCase.checkPost(_postKey.value ?: "").collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _post.value = it.data
                    _likeList.value = it.data?.likecount ?: arrayListOf()
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun checkComment(commentKey: String) = viewModelScope.launch {
        commentUseCase.checkComment(commentKey)
            .collect {
                when (it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _comment.value = it.data
                        _commentsLikeList.value = it.data?.comments_likeCount ?: arrayListOf()
                        hideProgress()
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun checkReComment(recomment: Comments) = viewModelScope.launch {
        _recomment.value = recomment
        commentUseCase.checkReComment(recomment)
            .collect {
                when (it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _reCommentsLikeList.value = it.data?.comments_likeCount ?: arrayListOf()
                        hideProgress()
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
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

    fun addlikeCount() = viewModelScope.launch {
        val likeList = _likeList.value ?: arrayListOf()
        likeList.add(currentUid)
        updateLike = Resource.Loading()
        updateLike = postUseCase.updateLike(likeList, _postKey.value ?: "")
    }

    fun addCommentlikeCount() = viewModelScope.launch {
        val likeList = _commentsLikeList.value ?: arrayListOf()
        likeList.add(currentUid)
        if (commentKey.value != null) {
            updateCommentsLike = Resource.Loading()
            updateCommentsLike = commentUseCase.updateCommentLike(_commentKey.value ?: "", likeList)
        }
    }

    fun removeLikeCount() = viewModelScope.launch {
        val likeList = _likeList.value ?: arrayListOf()
        likeList.remove(currentUid)
        updateLike = Resource.Loading()
        updateLike = postUseCase.updateLike(likeList, _postKey.value ?: "")
    }

    fun removeCommentLikeCount() = viewModelScope.launch {
        val likeList = _commentsLikeList.value ?: arrayListOf()
        likeList.remove(currentUid)
        if (_commentKey.value != null) {
            updateCommentsLike = Resource.Loading()
            updateCommentsLike = commentUseCase.updateCommentLike(_commentKey.value ?: "", likeList)
        }
    }

    fun writeComments(content: String) = viewModelScope.launch {
        val commentId = firestore.collection(COMMENTS_COLLECTION_PATH).document().id
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
            addComments = Resource.Loading()
            addComments = commentUseCase.setComments(recomments)
        }
    }

    fun writeReComments(content: String) = viewModelScope.launch {
        val commentId = _recomment.value?.comments_commentskey ?: ""
        val replyId = firestore.collection(COMMENTS_COLLECTION_PATH).document().id
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
                1,
                replyId
            )
            addReComments = Resource.Loading()
            addReComments = commentUseCase.setReComments(comments)
        }
    }

    fun setPost(key: String, postImages: ArrayList<String>) = viewModelScope.launch {
        _createPostKey.value = key

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
            postImages
        )
        addPost = Resource.Loading()
        addPost = postUseCase.setPost(post)
        initWritedPost()
    }

    fun initWritedPost() {
        createPostTitle.value = ""
        _category.value = "카테고리 선택"
        createPostContent.value = ""
        _createPostImage.value = arrayListOf()
    }

    fun getPostImages() = viewModelScope.launch {
        postUseCase.getPostImage(_postKey.value ?: "").collect {
            when (it) {
                is Resource.Success -> {
                    _postDetailImageList.value = it.data
                }
            }
        }
    }

    fun createPost() = viewModelScope.launch {
        val key = firestore.collection(POSTS_COLLECTION_PATH).document().id
        val createImages = createPostImage.value ?: arrayListOf()
        val imageList = arrayListOf<String>()
        if (createImages.isNotEmpty()) {
            showProgress()
            _updatedState.value = Resource.Loading()
            for (i in createImages.indices) {
                val riverRef: StorageReference =
                    firebaseStorage.getReference().child("post_image/" + key + "/" + i + ".jpg")
                val uploadTask: UploadTask = riverRef.putFile(createImages.get(i).toUri())
                uploadTask.addOnSuccessListener {

                    riverRef.downloadUrl.addOnSuccessListener { uri ->
                        firestore.collection(POSTS_COLLECTION_PATH).document(key).collection(
                            IMAGES_COLLECTION_GROUP)
                            .document().set(PostModel.PostImage(key, uri.toString()))
                        imageList.add(uri.toString())
                        firestore.collection(POSTS_COLLECTION_PATH).document(key).update(
                            POST_IMAGES_FIELD, imageList)
                    }
                }
                firestore.collection(POSTS_COLLECTION_PATH).document(key).set(post)
                uploadTask.addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    if (progress == 100.0) {
                        setPost(key, imageList)
                        hideProgress()
                        _updatedState.value = Resource.Success("성공")
                    }
                }
            }
        } else {
            setPost(key, imageList)
            _updatedState.value = Resource.Success("성공")
        }
    }

    private fun uploadImages(
        riverRef: StorageReference,
        createImages: java.util.ArrayList<String>,
        i: Int,
        key: String,
        imageList: ArrayList<String>
    ) {
        val uploadTask: UploadTask = riverRef.putFile(createImages.get(i).toUri())
        uploadTask.addOnSuccessListener {
            riverRef.downloadUrl.addOnSuccessListener { uri ->
                setPostImages(key, uri, imageList)
            }
        }
        uploadTask.addOnProgressListener {
            uploadProgress(it, key, imageList)
        }
    }

    private fun setPostImages(
        key: String,
        uri: Uri,
        imageList: ArrayList<String>
    ) {
        firestore.collection(POSTS_COLLECTION_PATH).document(key).collection(IMAGES_COLLECTION_GROUP)
            .document().set(PostModel.PostImage(key, uri.toString()))
        imageList.add(uri.toString())
        firestore.collection(POSTS_COLLECTION_PATH).document(key)
            .update(POST_IMAGES_FIELD, imageList)
    }

    private fun uploadProgress(
        it: UploadTask.TaskSnapshot,
        key: String,
        imageList: ArrayList<String>
    ) {
        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
        if (progress == 100.0) {
            setPost(key, imageList)
            _updatedState.value = Resource.Success("성공")
            hideProgress()
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
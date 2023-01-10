package com.qure.presenation.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.*
import com.qure.domain.usecase.people.GetAllUserUseCase
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.usecase.post.GetLikeCountUseCase
import com.qure.domain.usecase.post.GetPostCountUseCase
import com.qure.domain.usecase.profile.*
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getLikeCountUseCase: GetLikeCountUseCase,
    private val getPostCountUseCase: GetPostCountUseCase,
    private val getMeetingCountUseCase: GetMeetingCountUseCase,
    private val updateMeetingCountUseCase: UpdateMeetingCountUseCase,
    private val deleteBarcodeTimeUseCase: DeleteBarcodeTimeUseCase,
    private val deleteBarcodeUseCase: DeleteBarcodeUseCase,
    private val setBarcodeTimeUseCase: SetBarcodeTimeUseCase,
    private val setBarcodeUseCase: SetBarcodeUseCase,
    private val checkBarcodeTimeUseCase: CheckBarcodeTimeUseCase,
    private val getBarcodeTimeUseCase: GetBarcodeTimeUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val firebaseStorage: FirebaseStorage
) : BaseViewModel() {

    val currentUid = getCurrentUser()?.uid ?: ""

    private val _myProfileImage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val myProfileImage: LiveData<Event<Unit>>
        get() = _myProfileImage

    private val _otherProfileInfo: MutableLiveData<Event<Unit>> = MutableLiveData()
    val otherProfileInfo: LiveData<Event<Unit>>
        get() = _otherProfileInfo

    private val _chatRoom: MutableLiveData<Event<Unit>> = MutableLiveData()
    val chatRoom: LiveData<Event<Unit>>
        get() = _chatRoom

    private val _userList: MutableLiveData<List<User>> = MutableLiveData()
    val userList: LiveData<List<User>>
        get() = _userList

    private val _snackBarMsg: MutableLiveData<MessageSet> = MutableLiveData()
    val snackBarMsg: LiveData<MessageSet>
        get() = _snackBarMsg

    private val _myName: MutableLiveData<String> = MutableLiveData()
    val myName: LiveData<String>
        get() = _myName

    private val _myMsg: MutableLiveData<String> = MutableLiveData()
    val myMsg: LiveData<String>
        get() = _myMsg

    private val _myImage: MutableLiveData<String> = MutableLiveData()
    val myImage: LiveData<String>
        get() = _myImage

    private val _myToken: MutableLiveData<String> = MutableLiveData()
    val myToken: LiveData<String>
        get() = _myToken

    private val _myId: MutableLiveData<String> = MutableLiveData()
    val myId: LiveData<String>
        get() = _myId

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val _myUri: MutableLiveData<Uri> = MutableLiveData()
    val myUri: LiveData<Uri>
        get() = _myUri

    private val _postCount: MutableLiveData<String> = MutableLiveData()
    val postCount: LiveData<String>
        get() = _postCount

    private val _meetingCount: MutableLiveData<String> = MutableLiveData()
    val meetingCount: LiveData<String>
        get() = _meetingCount

    private val _likeCount: MutableLiveData<String> = MutableLiveData()
    val likeCount: LiveData<String>
        get() = _likeCount

    private val _profileClose: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileClose: LiveData<Event<Unit>>
        get() = _profileClose

    private val _profileEdit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileEdit: LiveData<Event<Unit>>
        get() = _profileEdit

    private val _profileSubmit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileSubmit: LiveData<Event<Unit>>
        get() = _profileSubmit

    private val _profileCancel: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileCancel: LiveData<Event<Unit>>
        get() = _profileCancel

    private val _profileQRCode: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileQRCode: LiveData<Event<Unit>>
        get() = _profileQRCode

    private val _profileQRScanner: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileQRScanner: LiveData<Event<Unit>>
        get() = _profileQRScanner

    private val _otherProfile: MutableLiveData<Boolean> = MutableLiveData()
    val otherProfile: LiveData<Boolean>
        get() = _otherProfile

    private val _cancelBarcode: MutableLiveData<Event<Unit>> = MutableLiveData()
    val cancelBarcode: LiveData<Event<Unit>>
        get() = _cancelBarcode

    private val _profileNameEdit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileNameEdit: LiveData<Event<Unit>>
        get() = _profileNameEdit

    private val _profileEditCancle: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileEditCancle: LiveData<Event<Unit>>
        get() = _profileEditCancle

    private val _profileEditSubmit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileEditSubmit: LiveData<Event<Unit>>
        get() = _profileEditSubmit

    private val _profileMessageEdit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileMessageEdit: LiveData<Event<Unit>>
        get() = _profileMessageEdit

    private val _profileImageEdit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val profileImageEdit: LiveData<Event<Unit>>
        get() = _profileImageEdit

    private val _recreateBarcode: MutableLiveData<Event<Unit>> = MutableLiveData()
    val recreateBarcode: LiveData<Event<Unit>>
        get() = _recreateBarcode

    private val _barcdoeCount: MutableLiveData<Long> = MutableLiveData()
    val barcdoeCount: LiveData<Long>
        get() = _barcdoeCount

    private val _createdBarcode: MutableLiveData<String> = MutableLiveData()
    val createdBarcode: LiveData<String>
        get() = _createdBarcode

    private val _checkBarcode: MutableLiveData<Boolean> = MutableLiveData()
    val checkBarcode: LiveData<Boolean>
        get() = _checkBarcode

    private val _barcodeTimeRemaining: MutableLiveData<Long> = MutableLiveData()
    val barcodeTimeRemaining: LiveData<Long>
        get() = _barcodeTimeRemaining

    private val _profileTag: MutableLiveData<String> = MutableLiveData()
    val profileTag: MutableLiveData<String>
        get() = _profileTag

    private val _profileEditText: MutableLiveData<String> = MutableLiveData()
    val profileEditText: MutableLiveData<String>
        get() = _profileEditText

    private val _updatedState: MutableLiveData<Resource<String, String>> = MutableLiveData()
    val updatedState: LiveData<Resource<String, String>>
        get() = _updatedState

    fun getCurrentUser() = getCurrentUserUseCase.getCurrentUser()

    fun getAllUser(user: User) = viewModelScope.launch {
        getAllUserUseCase(user)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _userList.value = it.data
                        hideProgress()
                    }
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getUserInfo(uid: String) = viewModelScope.launch {
        getUserInfoUseCase(uid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        val user = it.data
                        _myName.value = user?.usernm
                        _myMsg.value = user?.usermsg
                        _myImage.value = user?.userphoto
                        _myId.value = user?.userid
                        _myToken.value = user?.token
                        _user.value = user
                    }
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getPostCount(uid: String) = viewModelScope.launch {
        getPostCountUseCase(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _postCount.value = it.data.toString()
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun updateMeetingCount() = viewModelScope.launch {
        val count = meetingCount.value?.toInt()?.plus(1) ?: meetingCount.value!!.toInt()
        updateMeetingCountUseCase(currentUid, count)
            .collect {
                when (it) {
                    is Resource.Success -> _meetingCount.value = count.toString()
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getMeetingCount(uid: String) = viewModelScope.launch {
        getMeetingCountUseCase(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _meetingCount.value = it.data.toString()
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }


    fun getLikeCount(uid: String) = viewModelScope.launch {
        getLikeCountUseCase(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _likeCount.value = it.data.toString()
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun deleteBarcode() = viewModelScope.launch {
        deleteBarcodeUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> _snackBarMsg.value = MessageSet.SUCCESS
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun deleteBarcodeTime() = viewModelScope.launch {
        deleteBarcodeTimeUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> _snackBarMsg.value = MessageSet.SUCCESS
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun setBarcode(randomBarcode: String) = viewModelScope.launch {
        setBarcodeUseCase(currentUid, randomBarcode)
            .collect {
                when (it) {
                    is Resource.Success -> _snackBarMsg.value = MessageSet.SUCCESS
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun setBarcodeTime() = viewModelScope.launch {
        setBarcodeTimeUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> _snackBarMsg.value = MessageSet.SUCCESS
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun checkBarcodeTime() = viewModelScope.launch {
        checkBarcodeTimeUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> _checkBarcode.value = it.data
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }

    fun getBarcodeTime() = viewModelScope.launch {
        getBarcodeTimeUseCase(currentUid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        var data = it.data!!
                        var currentTime = System.currentTimeMillis()
                        var resultTime = data - currentTime
                        _barcodeTimeRemaining.value = resultTime
                    }
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }


    fun uploadImage() = viewModelScope.launch {


        val riverRef: StorageReference =
            firebaseStorage.getReference()
                .child("profile_image/" + currentUid + ".jpg")
        try {
            if (myUri.value != null) {
                _updatedState.value = Resource.Loading()
                val uploadTask: UploadTask = riverRef.putFile(myImage.value!!.toUri())
                uploadTask.addOnSuccessListener {
                    riverRef.downloadUrl.addOnSuccessListener {
                        _myImage.value = it.toString()
                        _updatedState.value = Resource.Success("업로드 성공")
                    }
                }.addOnFailureListener {
                    _updatedState.value = Resource.Success("")
                }
            } else {
                _updatedState.value = Resource.Success("")
            }

        } catch (e: Exception) {
            _updatedState.value = Resource.Error(e.message)
        }
    }

    fun chageProfile() = viewModelScope.launch {

        updateUserUseCase(currentUid, myName.value ?: "", myMsg.value ?: "", myImage.value ?: "")
            .collectLatest {
                when (it) {
                    is Resource.Success -> _snackBarMsg.value = MessageSet.SUCCESS
                    is Resource.Error -> _snackBarMsg.value = MessageSet.ERROR
                    is Resource.Empty -> _snackBarMsg.value = MessageSet.EMPTY_QUERY
                }
            }
    }


    fun moveMyProfile() {
        _myProfileImage.value = Event(Unit)
    }

    fun moveToProfile() {
        _otherProfileInfo.value = Event(Unit)
    }

    fun moveToChatRoom() {
        _chatRoom.value = Event(Unit)
    }

    fun closeProfile() {
        _profileClose.value = Event(Unit)
    }

    fun submitProfile() {
        _profileSubmit.value = Event(Unit)

    }

    fun editProfile() {
        _profileEdit.value = Event(Unit)
    }

    fun cancelProfile() {
        _myUri.value = myImage.value?.toUri()
        _profileCancel.value = Event(Unit)
    }

    fun showProfileQRScanner() {
        _profileQRScanner.value = Event(Unit)
    }

    fun showProfileQRCode() {
        _profileQRCode.value = Event(Unit)
    }

    fun checkCurrentUser(uid: String) {
        _otherProfile.value = if (currentUid.equals(uid)) true else false
    }

    fun countBarcodeTime(count: Long) {
        _barcdoeCount.value = count
    }

    fun createBarcod(text: String) {
        _createdBarcode.value = text
    }

    fun updateEditText() {

        when (profileTag.value) {
            "name" -> _myName.value = profileEditText.value
            "message" -> _myMsg.value = profileEditText.value
        }
    }

    fun changeImage(uri: Uri) {
        _myUri.value = uri
        _myImage.value = uri.toString()
    }

    fun profileTag(tag: String) {
        _profileTag.value = tag
    }

    fun profileEdit(text: String) {
        _profileEditText.value = text
    }

    fun recreateBarcode() {
        _recreateBarcode.value = Event(Unit)
    }

    fun cancelBarcode() {
        _cancelBarcode.value = Event(Unit)
    }

    fun editProfileName() {
        _profileNameEdit.value = Event(Unit)
    }

    fun editProfileMessage() {
        _profileMessageEdit.value = Event(Unit)
    }

    fun editProfileImage() {
        _profileImageEdit.value = Event(Unit)
    }

    fun cancelProfileEdit() {
        _profileEditCancle.value = Event(Unit)
    }

    fun submitProfileEdit() {
        _profileEditCancle.value = Event(Unit)
    }

    enum class MessageSet {
        LAST_PAGE,
        EMPTY_QUERY,
        NETWORK_NOT_CONNECTED,
        ERROR,
        SUCCESS,
        NO_RESULT,
        LOCAL_SUCCESS
    }
}
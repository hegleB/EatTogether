package com.qure.presenation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qure.domain.model.PostModel
import com.qure.domain.model.User
import com.qure.domain.repository.*
import com.qure.domain.usecase.BarcodeUseCase
import com.qure.domain.usecase.PostUseCase
import com.qure.domain.usecase.UploadUseCase
import com.qure.domain.usecase.UserUseCase
import com.qure.domain.utils.*
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val postUseCase: PostUseCase,
    private val barcodeUseCase: BarcodeUseCase,
    private val uploadUseCase: UploadUseCase,
) : BaseViewModel() {

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

    private val _myId: MutableLiveData<String> = MutableLiveData()

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

    private val _profileEditState: MutableLiveData<Boolean> = MutableLiveData(false)
    val profileEditState: LiveData<Boolean>
        get() = _profileEditState

    private val _profileQRState: MutableLiveData<Boolean> = MutableLiveData(true)
    val profileQRState: LiveData<Boolean>
        get() = _profileQRState

    var deleteBarcodeTime by mutableStateOf<DeleteBarcodeTime>(Resource.Success(false))
        private set

    var deleteBarcodeInfo by mutableStateOf<DeleteBarcodeInfo>(Resource.Success(false))
        private set

    var addBarcodeTime by mutableStateOf<AddBarcodeTime>(Resource.Success(false))
        private set

    var addBarcodeInfo by mutableStateOf<AddBarcodeInfo>(Resource.Success(false))
        private set

    var checkBarcodeTime by mutableStateOf<CheckBarcodeTime>(Resource.Success(false))
        private set

    var updateMeetingCount by mutableStateOf<UpdateMeetingCount>(Resource.Success(false))
        private set

    var updateUser by mutableStateOf<UpdateUser>(Resource.Success(false))
        private set

    fun getAllUser(user: User) = viewModelScope.launch {
        userUseCase.getAllUser()
            .collect {
                when (it) {
                    is Resource.Success -> _userList.value = getRemovedCurrentUser(it.data!!, user)
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    private fun getRemovedCurrentUser(users: List<User>, user: User): List<User> =
        users.filter { !it.isSameUid(user.uid) }

    fun getUserInfo(uid: String) = viewModelScope.launch {
        userUseCase.getUser(uid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        val user = it.data
                        _myName.value = user?.usernm
                        _myMsg.value = user?.usermsg
                        _myImage.value = user?.userphoto
                        _myId.value = user?.userid
                        _myToken.value = user?.token
                        _user.value = user!!
                    }
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun getPostCount(uid: String) = viewModelScope.launch {
        postUseCase.getPostCount(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _postCount.value = it.data.toString()
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun updateMeetingCount() = viewModelScope.launch {
        val count = meetingCount.value?.toInt()?.plus(1) ?: meetingCount.value!!.toInt()
        updateMeetingCount = Resource.Loading()
        updateMeetingCount = userUseCase.updateMeetingCount(currentUid.value ?: "", count)
    }

    fun getMeetingCount(uid: String) = viewModelScope.launch {
        userUseCase.getMeetingCount(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _meetingCount.value = it.data.toString()
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun getLikeCount(uid: String) = viewModelScope.launch {
        postUseCase.getLikeCount(uid)
            .collect {
                when (it) {
                    is Resource.Success -> _likeCount.value = it.data.toString()
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun deleteBarcode() = viewModelScope.launch {
        deleteBarcodeInfo = barcodeUseCase.deleteBarcodeInfo(currentUid.value ?: "")
    }

    fun deleteBarcodeTime() = viewModelScope.launch {
        deleteBarcodeTime = barcodeUseCase.deleteBarcodeTime(currentUid.value ?: "")
    }

    fun setBarcode(randomBarcode: String) = viewModelScope.launch {
        addBarcodeInfo = barcodeUseCase.setBarcodeInfo(currentUid.value ?: "", randomBarcode)
    }

    fun setBarcodeTime() = viewModelScope.launch {
        addBarcodeTime = barcodeUseCase.setBarcodeTime(currentUid.value ?: "")
    }

    fun checkBarcodeTime() = viewModelScope.launch {
        checkBarcodeTime = barcodeUseCase.checkBarcodeTime(currentUid.value ?: "")
    }

    fun getBarcodeTime() = viewModelScope.launch {
        barcodeUseCase.getBarcodeTime(currentUid.value ?: "").collect {
            when (it) {
                is Resource.Success -> _barcodeTimeRemaining.value = getCurrentBarcodeTime(it)
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    private fun getCurrentBarcodeTime(it: BarcodeTimeResource): Long {
        var data = it.data!!
        var currentTime = System.currentTimeMillis()
        var resultTime = data - currentTime
        return resultTime
    }

    fun uploadImage() = viewModelScope.launch {

        uploadUseCase.uploadImage(
            PROFILE_IMAGE_PATH,
            currentUid.value!!,
            -1,
            myImage.value!!.toUri()
        ).collect {
            when (it) {
                is Resource.Loading -> {
                    showProgress()
                    _updatedState.value = Resource.Loading()
                }
                is Resource.Success -> {
                    downloadImage(currentUid.value!!)
                    hideProgress()
                }
                is Resource.Error -> {
                    hideProgress()
                    _updatedState.value = Resource.Success("")
                }
            }
        }
    }

    private fun downloadImage(key: String) = viewModelScope.launch {
        uploadUseCase.downloadImage(PROFILE_IMAGE_PATH, key, -1).collect {
            when (it) {
                is Resource.Success -> {
                    _myImage.value = it.data.toString()
                    _updatedState.value = Resource.Success("")
                }
                is Resource.Error -> {
                    hideProgress()
                    _updatedState.value = Resource.Success("")
                }
            }
        }
    }
    fun chageProfile() = viewModelScope.launch {
        updateUser = userUseCase.updateUser(
            currentUid.value ?: "",
            myName.value ?: "",
            myMsg.value ?: "",
            myImage.value ?: "",
        )
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

    fun setClosedProfileState() {
        _profileEditState.value = false
        _profileQRState.value = true
    }

    fun setEditedProfileState() {
        _profileEditState.value = true
        _profileQRState.value = false
    }

    fun setQRAbledState() {
        _profileQRState.value = true
    }

    fun setQRDisabledState() {
        _profileQRState.value = false
    }
}

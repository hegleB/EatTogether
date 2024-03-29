package com.qure.presenation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.Setting
import com.qure.domain.model.User
import com.qure.domain.repository.AddMeetingCount
import com.qure.domain.repository.AddSetting
import com.qure.domain.usecase.AuthUseCase
import com.qure.domain.usecase.UserUseCase
import com.qure.domain.usecase.setting.SetSettingUseCase
import com.qure.domain.utils.ErrorMessage
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val setSettingUseCase: SetSettingUseCase,
    private val firebaseStorage: FirebaseStorage,
) : BaseViewModel() {

    private val _bottomsSheetLogin: MutableLiveData<Event<Unit>> = MutableLiveData()
    val bottomsSheetLogin: LiveData<Event<Unit>>
        get() = _bottomsSheetLogin

    private val _buttonGoogle: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonGoogle: LiveData<Event<Unit>>
        get() = _buttonGoogle

    private val _signInGoogleState: MutableLiveData<Resource<FirebaseUser, String>> =
        MutableLiveData()
    val signInGoogleState: LiveData<Resource<FirebaseUser, String>>
        get() = _signInGoogleState

    private val _signInFacebookState: MutableLiveData<Resource<FirebaseUser, String>> =
        MutableLiveData()
    val signInFacebookState: LiveData<Resource<FirebaseUser, String>>
        get() = _signInFacebookState

    private val _buttonFacebook: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonFacebook: LiveData<Event<Unit>>
        get() = _buttonFacebook

    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val currentUser: LiveData<FirebaseUser>
        get() = _currentUser

    private val _settingName: MutableLiveData<String> = MutableLiveData()
    val settingName: MutableLiveData<String>
        get() = _settingName

    private val _settingMessage: MutableLiveData<String> = MutableLiveData()
    val settingMessage: MutableLiveData<String>
        get() = _settingMessage

    private val _buttonUserImage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonUserImage: LiveData<Event<Unit>>
        get() = _buttonUserImage

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _settingImageUri: MutableLiveData<String> = MutableLiveData()

    private val _userMessageToken: MutableLiveData<String> = MutableLiveData()

    private val _settingState: MutableLiveData<Resource<User, String>> = MutableLiveData()
    val settingState: LiveData<Resource<User, String>>
        get() = _settingState

    private val _buttonSettingSubmit: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSettingSubmit: LiveData<Event<Unit>>
        get() = _buttonSettingSubmit

    private val _users: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>>
        get() = _users

    private val _isUser: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUser: LiveData<Boolean>
        get() = _isUser

    var addMeetingCount by mutableStateOf<AddMeetingCount>(Resource.Success(false))
        private set

    var addSetting by mutableStateOf<AddSetting>(Resource.Success(false))
        private set

    var setUser by mutableStateOf<AddSetting>(Resource.Success(false))
        private set

    fun accessGoogle(credential: AuthCredential) {
        showProgress()
        authUseCase.signWithGoogle(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                hideProgress()
                _signInGoogleState.value = Resource.Success(it.result?.user!!)
            } else {
                _signInGoogleState.value = Resource.Error(it.exception?.message.toString())
            }
        }
    }

    fun accessFacebook(token: AccessToken) = viewModelScope.launch {
        showProgress()
        authUseCase.signWithFacebook(token).addOnCompleteListener {
            if (it.isSuccessful) {
                hideProgress()
                _signInFacebookState.value = Resource.Success(it.result.user!!)
            } else {
                _signInFacebookState.value = Resource.Error(it.exception?.message.toString())
            }
        }
    }

    fun getAllUser() = viewModelScope.launch {
        userUseCase.getAllUser().collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    hideProgress()
                    _users.value = it.data!!
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun isJoin(user: FirebaseUser) {
        val isNotEmpty = _users.value?.filter { it.isSameUid(user.uid) }
            ?.isNotEmpty() ?: false
        _isUser.value = isNotEmpty
    }

    fun showBottomSheetLogin() {
        _bottomsSheetLogin.value = Event(Unit)
    }

    fun signInGoogle() {
        _buttonGoogle.value = Event(Unit)
    }

    fun signInFacebook() {
        _buttonFacebook.value = Event(Unit)
    }

    fun showImagePicker() {
        _buttonUserImage.value = Event(Unit)
    }

    fun getMessageToken() = viewModelScope.launch {
        userUseCase.getUserMessageToken().addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result.toString()
                _userMessageToken.value = token
            }
        }
    }

    fun getCurrentUser(user: FirebaseUser) {
        _currentUser.value = user
    }

    fun getImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun storageProfile() {
        showProgress()
        val riverRef: StorageReference =
            firebaseStorage.getReference()
                .child("profile_image/" + currentUser.value?.uid.toString() + ".jpg")
        try {
            if (imageUri.value != null) {
                val uploadTask: UploadTask = riverRef.putFile(imageUri.value!!)
                uploadTask.addOnSuccessListener {
                    riverRef.downloadUrl.addOnSuccessListener {
                        _settingImageUri.value = it.toString()
                        _settingState.value = Resource.Success(User())
                        hideProgress()
                    }
                }
            } else {
                _settingState.value = Resource.Success(User())
            }
        } catch (e: Exception) {
            _settingState.value = Resource.Error(e.message)
        }
    }

    fun setSetting(now: Long) = viewModelScope.launch {
        addSetting = Resource.Loading()
        addSetting = setSettingUseCase(currentUser.value?.uid ?: "", Setting(true, true, true, now))
    }

    fun setUserProfile(): User = User(
        currentUser.value?.email ?: "",
        currentUser.value?.uid ?: "",
        settingName.value ?: "",
        _userMessageToken.value ?: "",
        _settingImageUri.value ?: "",
        settingMessage.value ?: ""
    )

    fun setUser() = viewModelScope.launch {
        val user = User(
            _currentUser.value?.uid ?: ""
        )
        setUser = Resource.Loading()
        setUser = userUseCase.setUser(currentUser.value?.uid ?: "", setUserProfile())
    }

    fun setMeeting() = viewModelScope.launch {
        addMeetingCount = Resource.Loading()
        addMeetingCount = userUseCase.setMeetingCount(currentUser.value?.uid ?: "", 0)
    }

    fun setFireStoreUser(now: Long) {
        setSetting(now)
        setUser()
        setMeeting()
    }

    fun moveToHome() {
        _buttonSettingSubmit.value = Event(Unit)
    }
}

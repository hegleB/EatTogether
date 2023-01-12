package com.qure.presenation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.Setting
import com.qure.domain.model.User
import com.qure.domain.repository.AddMeetingCount
import com.qure.domain.usecase.auth.SignInWithFacebookUseCase
import com.qure.domain.usecase.auth.SignWithGoogleUseCase
import com.qure.domain.usecase.people.GetAllUserUseCase
import com.qure.domain.usecase.people.GetMessageTokenUseCase
import com.qure.domain.usecase.profile.GetCurrentUserUseCase
import com.qure.domain.usecase.profile.SetMeetingCountUseCase
import com.qure.domain.usecase.profile.SetUserUseCase
import com.qure.domain.usecase.setting.SetSettingUseCase
import com.qure.domain.utils.ErrorMessage
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signWithGoogleUseCase: SignWithGoogleUseCase,
    private val signWithFacebookUserCase: SignInWithFacebookUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMessageTokenUseCase: GetMessageTokenUseCase,
    private val setUserUseCase: SetUserUseCase,
    private val setSettingUseCase: SetSettingUseCase,
    private val setMeetingCountUseCase: SetMeetingCountUseCase,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {

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
    val settingImageUri: LiveData<String>
        get() = _settingImageUri

    private val _userMessageToken: MutableLiveData<String> = MutableLiveData()
    val userMessageToken: LiveData<String>
        get() = _userMessageToken

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

    fun accessGoogle(credential: AuthCredential) {
        _signInGoogleState.value = Resource.Loading()
        signWithGoogleUseCase.signWithGoogle(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                _signInGoogleState.value = Resource.Success(it.result?.user!!)
            } else {
                _signInGoogleState.value = Resource.Error(it.exception?.message.toString())
            }
        }
    }

    fun accessFacebook(token: AccessToken) {
        viewModelScope.launch {
            _signInFacebookState.value = Resource.Loading()
            signWithFacebookUserCase.signWithFacebook(token).addOnCompleteListener {
                if (it.isSuccessful) {
                    _signInFacebookState.value = Resource.Success(it.result.user!!)
                } else {
                    _signInFacebookState.value = Resource.Error(it.exception?.message.toString())
                }
            }

        }
    }

    fun getAllUser() = viewModelScope.launch {
        getAllUserUseCase().collect {
            when (it) {
                is Resource.Success -> _users.value = it.data
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
        getMessageTokenUseCase.getMessageToken().addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result.toString()
                _userMessageToken.value = token
            }
        }
    }

    fun getCurrentUser() {
        _currentUser.value = getCurrentUserUseCase.getCurrentUser()
    }

    fun setUserProfile(): User {

        return User(
            currentUser.value?.email ?: "",
            currentUser.value?.uid ?: "",
            settingName.value ?: "",
            userMessageToken.value ?: "",
            settingImageUri.value ?: "",
            settingMessage.value ?: ""
        )
    }


    fun getImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun storageProfile() {

        _settingState.value = Resource.Loading()

        val riverRef: StorageReference =
            firebaseStorage.getReference()
                .child("profile_image/" + currentUser.value?.uid.toString() + ".jpg")
        try {
            if (imageUri.value != null) {
                val uploadTask: UploadTask = riverRef.putFile(imageUri.value!!)
                uploadTask.addOnSuccessListener {
                    riverRef.downloadUrl.addOnSuccessListener {
                        _settingImageUri.value = it.toString()
                        _settingState.value = Resource.Success(setUserProfile())
                    }
                }
            } else {
                _settingState.value = Resource.Success(setUserProfile())
            }
        } catch (e: Exception) {
            _settingState.value = Resource.Error(e.message)
        }
    }

    fun setSetting(now: Long) = viewModelScope.launch {
        setSettingUseCase(currentUser.value?.uid ?: "", Setting(true, true, true, now))
            .collectLatest {
                when (it) {
                    is Resource.Success -> println()
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun setUser() = viewModelScope.launch {
        setUserUseCase(currentUser.value?.uid ?: "", setUserProfile())
            .collectLatest {
                when (it) {
                    is Resource.Success -> println()
                    is Resource.Error -> ErrorMessage.print(it.message ?: "")
                }
            }
    }

    fun setMeeting() = viewModelScope.launch {
        addMeetingCount = Resource.Loading()
        addMeetingCount = setMeetingCountUseCase(currentUser.value?.uid ?: "", 0)
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
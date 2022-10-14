package com.qure.presenation.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.BarcodeScan
import com.qure.domain.model.Setting
import com.qure.domain.model.User
import com.qure.domain.usecase.*
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signWithGoogleUseCase: SignWithGoogleUseCase,
    private val signWithFacebookUserCase: SignInWithFacebookUseCase,
    private val isJoinUseCase: IsJoinUserCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMessageTokenUseCase: GetMessageTokenUseCase,
    private val firebaseStorage: FirebaseStorage,
    private val setFireStoreUserUseCase: SetFireStoreUserUseCase,
    private val setFireStoreSettingUseCase: SetFireStoreSettingUseCase,
    private val setFireStoreMeetingUseCase: SetFireStoreMeetingUseCase

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

    fun accessGoogle(account: GoogleSignInAccount) {

        _signInGoogleState.value = Resource.Loading()
        signWithGoogleUseCase.signWithGoogle(account).addOnCompleteListener {
            if (it.isSuccessful) {

                _signInGoogleState.value =
                    Resource.Success(getCurrentUserUseCase.getCurrentUser()!!)
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

    fun isJoin(user: FirebaseUser): Boolean? {
        return isJoinUseCase.isJoin(user)
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
            }
        } catch (e: Exception) {
            _settingState.value = Resource.Error(e.message)
        }
    }

    fun setFireStoreUser() =
        viewModelScope.launch {
            setFireStoreUserUseCase.setFireStoreUser().set(setUserProfile(), SetOptions.merge())
        }

    fun setFireStoreSetting(now : Long) =
        viewModelScope.launch {
            setFireStoreSettingUseCase.setFireStoreSetting().set(Setting(true,true,true, now))
        }

    fun setFireStoreMeeting() =
        viewModelScope.launch {
            setFireStoreMeetingUseCase.setFireStoreMeeting().set(BarcodeScan(0))
        }

    fun moveToHome() {
        _buttonSettingSubmit.value = Event(Unit)
    }
}
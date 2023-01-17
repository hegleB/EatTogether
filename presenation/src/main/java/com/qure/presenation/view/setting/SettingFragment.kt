package com.qure.presenation.view.setting

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.User
import com.qure.domain.utils.*
import com.qure.presenation.R
import com.qure.presenation.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: SettingViewModel by viewModels()
    private var loginInfo: Preference? = null
    private var LogOutPreference: Preference? = null
    private var messagePreference: SwitchPreference? = null
    private var vibrationPreference: CheckBoxPreference? = null
    private var soundPreference: CheckBoxPreference? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        loginInfo = findPreference(SETTING_LOGIN_INFO)
        LogOutPreference = findPreference(SETTING_LOGOUT)
        messagePreference = findPreference(SETTING_MESSAGE)
        vibrationPreference = findPreference(SETTING_VIBRATE)
        soundPreference = findPreference(SETTING_SOUND)
        val uid = firebaseAuth.currentUser?.uid ?: ""

        setInitVibrationAndSoundPreference()
        setCurrentLoginInfo(uid)
        setMessageClickListener(uid)
        setVibrationClickListener(uid)
        setSoundClickListener(uid)
        setLogoutClickListener()
    }

    private fun setInitVibrationAndSoundPreference() {
        vibrationPreference!!.isEnabled = messagePreference!!.isChecked
        soundPreference!!.isEnabled = messagePreference!!.isChecked
    }

    private fun setCurrentLoginInfo(uid: String) {
        firestore.collection(USERS_COLLECTION_PATH).document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = task.result.toObject(User::class.java)
                    loginInfo?.summary = email?.userid
                }
            }
    }

    private fun setMessageClickListener(uid: String) {
        messagePreference?.setOnPreferenceChangeListener(object :
            Preference.OnPreferenceChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                val now = System.currentTimeMillis()
                setMessagePreference(uid, now)
                return true
            }
        })
    }

    private fun setMessagePreference(uid: String, now: Long) {
        vibrationPreference?.isEnabled = messagePreference!!.isChecked.not()
        soundPreference?.isEnabled = messagePreference!!.isChecked.not()
        updateMessageNotification(uid, now)
    }

    private fun updateMessageNotification(uid: String, now: Long) {
        with(firestore.collection(SETTING_COLLECTION_PATH).document(uid)) {
            update(MESSAGE_FIELD, messagePreference?.isChecked?.not())
            update(NOTIFICATION_TIME_FIELD, now)
        }
    }

    private fun setVibrationClickListener(uid: String) {
        vibrationPreference?.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                firestore.collection(SETTING_COLLECTION_PATH)
                    .document(uid)
                    .update(VIBRATION_FIELD, vibrationPreference?.isChecked)
                return true
            }
        })
    }

    private fun setSoundClickListener(uid: String) {
        soundPreference?.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                firestore.collection(SETTING_COLLECTION_PATH)
                    .document(uid)
                    .update(SOUND_FIELD, soundPreference?.isChecked)
                return true
            }
        })
    }

    private fun setLogoutClickListener() {
        LogOutPreference?.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                viewModel.clickLogout()
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
                return true
            }
        })
    }

    companion object {
        const val SETTING_LOGIN_INFO = "setting_login_info"
        const val SETTING_LOGOUT = "setting_logout"
        const val SETTING_MESSAGE = "setting_message"
        const val SETTING_VIBRATE = "setting_vibrate"
        const val SETTING_SOUND = "setting_sound"
    }
}
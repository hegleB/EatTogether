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
    private val viewModel : SettingViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val loginInfo : Preference? = findPreference("setting_login_info")
        val LogOutPreference: Preference? = findPreference("setting_logout")
        val messagePreference: SwitchPreference? = findPreference("setting_message")
        val vibrationPreference : CheckBoxPreference? = findPreference("setting_vibrate")
        val soundPreference : CheckBoxPreference? = findPreference("setting_sound")
        val uid = firebaseAuth.currentUser?.uid ?: ""

        setCurrentLoginInfo(uid, loginInfo)
        setMessageClickListener(messagePreference, uid, vibrationPreference, soundPreference)
        setVibrationClickListener(vibrationPreference, uid)
        setSoundClickListener(soundPreference, uid)
        setLogoutClickListener(LogOutPreference)
    }

    private fun setCurrentLoginInfo(uid: String, loginInfo: Preference?) {
        firestore.collection(USERS_COLLECTION_PATH).document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val email = task.result.toObject(User::class.java)
                loginInfo!!.summary = email!!.userid
            }
        }
    }

    private fun setMessageClickListener(
        messagePreference: SwitchPreference?,
        uid: String,
        vibrationPreference: CheckBoxPreference?,
        soundPreference: CheckBoxPreference?
    ) {
        messagePreference!!.setOnPreferenceChangeListener(object :
            Preference.OnPreferenceChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                val now = System.currentTimeMillis()
                setMessagePreference(
                    messagePreference,
                    uid,
                    now,
                    vibrationPreference,
                    soundPreference
                )
                return true
            }
        })
    }

    private fun setMessagePreference(
        messagePreference: SwitchPreference,
        uid: String,
        now: Long,
        vibrationPreference: CheckBoxPreference?,
        soundPreference: CheckBoxPreference?
    ) {
        if (messagePreference.isChecked) {
            updateMessageNotification(uid, now)
            vibrationPreference!!.isEnabled = false
            soundPreference!!.isEnabled = false
        } else {
            vibrationPreference!!.isEnabled = true
            soundPreference!!.isEnabled = true
        }
    }

    private fun updateMessageNotification(uid: String, now: Long) {
        firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(MESSAGE_FIELD, false)
        firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(NOTIFICATION_TIME_FIELD, now)
    }

    private fun setVibrationClickListener(
        vibrationPreference: CheckBoxPreference?,
        uid: String
    ) {
        vibrationPreference!!.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                setVibrationPrefernce(vibrationPreference, uid)
                return true
            }
        })
    }

    private fun setVibrationPrefernce(
        vibrationPreference: CheckBoxPreference?,
        uid: String
    ) {
        if (vibrationPreference!!.isChecked) {
            firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(VIBRATION_FIELD, true)

        } else {
            firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(VIBRATION_FIELD, false)

        }
    }

    private fun setSoundClickListener(
        soundPreference: CheckBoxPreference?,
        uid: String
    ) {
        soundPreference!!.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                setSoundPrefernce(soundPreference, uid)
                return true
            }
        })
    }

    private fun setSoundPrefernce(
        soundPreference: CheckBoxPreference?,
        uid: String
    ) {
        if (soundPreference!!.isChecked) {
            firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(SOUND_FIELD, true)
        } else {
            firestore.collection(SETTING_COLLECTION_PATH).document(uid).update(SOUND_FIELD, false)
        }
    }

    private fun setLogoutClickListener(LogOutPreference: Preference?) {
        LogOutPreference!!.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                viewModel.clickLogout()
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
                return true
            }
        })
    }
}
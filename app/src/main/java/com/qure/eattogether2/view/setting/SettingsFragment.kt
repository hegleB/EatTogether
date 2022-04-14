package com.qure.eattogether2.view.setting

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.qure.eattogether2.R
import com.qure.eattogether2.data.User
import com.qure.eattogether2.view.MainActivity
import com.qure.eattogether2.view.chat.MyFirebaseMessagingService
import com.qure.eattogether2.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

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
        val uid = firebaseAuth.currentUser!!.uid


        firestore.collection("users").document(uid).get().addOnCompleteListener{
            task->
            if(task.isSuccessful){
                val email = task.result.toObject(User::class.java)
                loginInfo!!.summary = email!!.userid
            }
        }

        if(messagePreference!!.isChecked){
            vibrationPreference!!.isEnabled = true
            soundPreference!!.isEnabled = true
        } else {
            vibrationPreference!!.isEnabled = false
            soundPreference!!.isEnabled = false
        }


        messagePreference!!.setOnPreferenceChangeListener(object :
            Preference.OnPreferenceChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {

                val now = System.currentTimeMillis()
                if (messagePreference.isChecked) {
                    firestore.collection("setting").document(uid).update("message",false)
                    firestore.collection("setting").document(uid).update("notification_time",now)
                    vibrationPreference!!.isEnabled = false
                    soundPreference!!.isEnabled = false


                } else {
                    firestore.collection("setting").document(uid).update("message",true)
                    firestore.collection("setting").document(uid).update("notification_time",now)
                    vibrationPreference!!.isEnabled = true
                    soundPreference!!.isEnabled = true


                }


                return true
            }

        })

        vibrationPreference!!.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener{
            override fun onPreferenceClick(preference: Preference?): Boolean {
                if (vibrationPreference!!.isChecked) {
                    firestore.collection("setting").document(uid).update("vibration",true)

                } else {
                    firestore.collection("setting").document(uid).update("vibration",false)

                }
                return true
            }

        })

        soundPreference!!.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener{
            override fun onPreferenceClick(preference: Preference?): Boolean {
                if (soundPreference!!.isChecked) {
                    firestore.collection("setting").document(uid).update("sound",true)

                } else {
                    firestore.collection("setting").document(uid).update("sound",false)

                }
                return true
            }

        })





        LogOutPreference!!.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {

                viewModel.logout()
                activity?.let {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                return true
            }

        })
    }
}
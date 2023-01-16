package com.qure.presenation.view.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseFragment
import com.qure.domain.model.User
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentPeopleBinding
import com.qure.presenation.databinding.FragmentProfileSettingBinding
import com.qure.presenation.view.MainActivity
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
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.loginFragment)
                return false
            }
        })

    }
}
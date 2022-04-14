package com.qure.eattogether2.view.people

import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.databinding.DataBindingUtil
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.ActivityHomeBinding
import com.qure.eattogether2.databinding.ActivityProfileBinding
import com.qure.eattogether2.view.post.DetailPostFragmentDirections
import com.qure.eattogether2.view.post.PostFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_profile_fragment)



        val post = intent.getParcelableExtra<Post>("post")
        val user = intent.getParcelableExtra<User>("user")

        post!!.run {
            val directions = PostFragmentDirections.actionPostContainerFragmentToDetailPostFragment(
                title,
                timestamp,
                userimage,
                content,
                category,
                likecount.toTypedArray(),
                arrayOf(),
                writer,
                key,
                "profile",
                user!!
            )

            navController.navigate(directions)
        }




    }
}
package com.qure.eattogether2.view.people

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentProfileBinding
    private val args by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.apply {
            val currentUid = firebaseAuth.currentUser!!.uid
            val chatroomId = firestore.collection("chatrooms").document().id
            val usersList = arrayListOf<String>(args.peopleOtherPersonUid, currentUid)
            firestore.collection("users").get().addOnSuccessListener { snapshot ->
                var currentUserImage = ""
                var otherPersonImage = ""
                var myProfile : User = User()
                var otherProfile : User = User()
                for (i in snapshot) {
                    val user= i.toObject(User::class.java)

                    if (user.uid.equals(currentUid)) {
                        currentUserImage = user.userphoto
                        myProfile = user
                    } else if (user.uid.equals(args.peopleOtherPersonUid)) {
                        otherPersonImage = user.userphoto
                        otherProfile = user
                    }
                }
                var chatroom = ChatRoom(
                    false,
                    chatroomId,
                    "",
                    mutableMapOf(
                        currentUid to currentUserImage,
                        args.peopleOtherPersonUid to otherPersonImage
                    ),
                    "",
                    "",
                    usersList.size,
                    mutableMapOf(currentUid to 0, args.peopleOtherPersonUid to 0),
                    usersList
                )

                profileChat.setOnClickListener {

                    firestore.collection("chatrooms").whereArrayContains("users", currentUid)
                        .get()
                        .addOnSuccessListener { snapshot ->

                            val chatroomData = snapshot.toObjects(ChatRoom::class.java)

                            if (chatroomData.isEmpty()) {

                                setChatRoom(chatroom)
                                moveToMessageFragment(chatroom)

                            } else {


                                for (i in chatroomData) {
                                    if (i.users.contains(args.peopleOtherPersonUid) && i.users.size <= 2) {
                                        moveToMessageFragment(i)
                                        break
                                    } else {
                                        setChatRoom(chatroom)
                                        moveToMessageFragment(chatroom)
                                        break
                                    }
                                }


                            }


                        }
                }
                profileInformation.setOnClickListener {
                    moveToProfile(otherProfile)
                }

            }

        }
    }

    fun moveToMessageFragment(chatroom: ChatRoom) {
        val directions =
            ProfileFragmentDirections.actionProfileFragmentToMessageFragment(
                chatroom, args.peopleOtherPersonUid, false
            )

        findNavController().navigate(directions)

    }

    fun moveToProfile(user : User) {

        val intent = Intent(requireActivity(),DetailProfileActivity::class.java)
        intent.putExtra("editData", "")
        intent.putExtra("user",user) //데이터 넣기
        startActivity(intent)
        requireActivity().finish()


    }


    fun setChatRoom(chatroomData: ChatRoom) {
        firestore.collection("chatrooms").document(chatroomData.roomId)
            .set(chatroomData)


    }

}
package com.qure.eattogether2.view.chat

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.components.Component.builder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ChatAdapter
import com.qure.eattogether2.adapter.ChatUserAdapter
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentMessageBinding
import com.qure.eattogether2.repository.ChatRepository
import com.qure.eattogether2.repository.PeopleRepository
import com.qure.eattogether2.viewmodel.ChatViewModel
import com.qure.eattogether2.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap



@AndroidEntryPoint
class MessageFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var peopleRepository: PeopleRepository

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private val useradapter = ChatUserAdapter()

    private val viewModel by viewModels<ChatViewModel>()
    private val userViewModel by viewModels<PeopleViewModel>()
    var imm: InputMethodManager? = null
    private lateinit var binding: FragmentMessageBinding
    private var allUsers = MutableLiveData<MutableList<User>>()
    private lateinit var navBar: BottomNavigationView
    private val args by navArgs<MessageFragmentArgs>()
    private var allChatroomUsers = MutableLiveData<MutableList<User>>()
    private lateinit var callback: OnBackPressedCallback

    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false)

        val chatroomData = args.chatroom
        val currentUid = firebaseAuth.currentUser!!.uid

        pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()



        imm =
            requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        binding.apply {
            chatroom = chatroomData

            messageReclclerview.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

                    hideKeyboard()

                    return true
                }

            })

        }

        touchMessageInput()
        setMesage(chatroomData, currentUid)
        setmessageToolbar()
        findChatRoom(chatroomData)
        InputMessage()
        clickSendMessage(chatroomData, currentUid)
        setHasOptionsMenu(true)
        onBackpressed()
        setUserAdapter()
        getAllChatRoomUser()
        moveToChatAllUserActivity()

        binding.messageImage.setOnClickListener {


            requestPermission(chatroomData, currentUid)

        }


        return binding.root
    }

    fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.messageEditText.windowToken, 0)
    }


    private fun requestPermission(chatroomData: ChatRoom, currentUid: String) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagesPicker(chatroomData, currentUid)
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(
                    requireContext(),
                    "Permission Denied\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun setMesage(chatroomData: ChatRoom, currentUid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            chatRepository.getChatMessage(chatroomData.roomId).collectLatest {

                val adapter = ChatAdapter(requireContext(), currentUid, it, args.chatroom.userCount)


                viewModel.messageCount.postValue(it.size)

                binding.apply {

                    messageReclclerview.adapter = adapter

                }


                binding.messageReclclerview.scrollToPosition(adapter.itemCount - 1)

            }

        }
    }

    fun InputMessage() {
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length == 0) {
                    binding.messageSend.visibility = View.INVISIBLE
                } else {
                    binding.messageSend.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    fun touchMessageInput() {
        binding.messageEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                return false
            }

        })
    }

    fun setmessageToolbar() {
        binding.apply {
            messageToolbar.inflateMenu(R.menu.chat_navigation)
            messageToolbar.setNavigationIcon(R.drawable.ic_back)
            messageToolbar.setNavigationOnClickListener {
                if (args.notification == false) {
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(R.id.chatFragment)
                    navBar = requireActivity().findViewById(R.id.bottom_navigation)
                    navBar.selectedItemId = R.id.chatFragment
                }

            }

            messageToolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {

                        R.id.menu_add_user -> {
                            val addUserFragment: AddUserFragment = AddUserFragment()
                            val result = chatroom!!.roomId
                            val bundle = Bundle()
                            bundle.putString("roomId", result)

                            addUserFragment.arguments = bundle

                            if (chatDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
                                chatDrawerlayout.closeDrawer(Gravity.RIGHT)

                            } else {


                                chatDrawerlayout.openDrawer(Gravity.RIGHT)
                            }
                            return true
                        }


                        else -> {
                            return true
                        }

                    }
                }

            })
        }
    }

    fun findChatRoom(chatroomData: ChatRoom) {

        val currentUid = firebaseAuth.currentUser!!.uid


        viewModel.messageCount.observe(viewLifecycleOwner, {
            readMessage(currentUid, chatroomData)


        })


    }

    fun clickSendMessage(chatroomData: ChatRoom, currentUid: String) {
        binding.messageSend.setOnClickListener {
            val inputText = binding.messageEditText.text.toString()
            sendMessage(chatroomData, currentUid, inputText)

        }
    }

    fun readMessage(currentUid: String, chatroomData: ChatRoom) {


        firestore.collectionGroup("chat").whereEqualTo("roomId", chatroomData.roomId)
            .orderBy("timestamp", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {

                    for (i in snapshot.documents) {
                        val s = i.toObject(ChatComment::class.java)
                        if (!s!!.readUsers.containsKey(currentUid)) {
                            var data = s.readUsers
                            data.put(currentUid, true)
                            i.reference.update("readUsers", data)

                            firestore.collection("chatrooms").document(chatroomData.roomId)
                                .get().addOnSuccessListener { snapshot ->

                                    val chat = snapshot.toObject(ChatRoom::class.java)

                                    val ss = chat!!.unreadCount
                                    ss.put(currentUid, 0)
                                    firestore.collection("chatrooms").document(chatroomData.roomId)
                                        .update(
                                            "unreadCount",
                                            ss
                                        )
                                }


                        }
                    }
                }
            }
    }


    fun sendMessage(
        chatroomData: ChatRoom,
        currentUid: String,
        inputText: String,

        ) {

        val currentTime = Date().time

        firestore.collection("users").document(currentUid).get().addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)
            var userImage = user!!.userphoto
            var usernm = user!!.usernm
            val chatComment = ChatComment(
                chatroomData.roomId,
                userImage,
                currentUid,
                usernm,
                inputText,
                "1",
                currentTime.toString(),
                mutableMapOf(currentUid to true)

            )

            getToken(currentUid, user!!.userphoto, inputText, chatroomData.roomId, usernm)
            firestore.collection("chatrooms").document(chatroomData.roomId).get()
                .addOnSuccessListener { snapshot ->
                    var bundle: Bundle = requireArguments()


                    val chat = snapshot.toObject(ChatRoom::class.java)

                    val myMessageCount = chat!!.unreadCount.get(currentUid)
                    var value = chat!!.unreadCount

                    if (bundle != null && args.destinationUid.equals("")) {

                        var destinationId = bundle.get("destinationId").toString()
                        value =
                            chatCount(chat, value, myMessageCount!!, currentUid, destinationId!!)

                    } else {
                        value = chatCount(
                            chat,
                            value,
                            myMessageCount!!,
                            currentUid,
                            args.destinationUid
                        )
                    }

                    val c = ChatRoom(
                        true,
                        chat!!.roomId,
                        "",
                        chat.photo,
                        inputText,
                        currentTime.toString(),
                        chat!!.users.size,
                        value,
                        chat!!.users
                    )
                    firestore.collection("chatrooms").document(chatroomData.roomId)
                        .set(c)

                    firestore.collection("chat").document(currentTime.toString()).set(chatComment)
                    binding.messageEditText.setText("")

                }


        }
    }

    fun chatCount(
        chat: ChatRoom,
        value: MutableMap<String, Int>,
        myMessageCount: Int,
        currentUid: String,
        destinationUid: String
    ): MutableMap<String, Int> {


        val destinationmessageCount = chat!!.unreadCount.get(destinationUid)

        if (chat!!.users.size <= 2) {
            value.put(currentUid, myMessageCount!!)
            value.put(
                destinationUid, destinationmessageCount!!.plus(
                    1
                )
            )
        } else {

            for (i in chat!!.users) {
                if (i.equals(currentUid)) {
                    value.put(currentUid, myMessageCount!!)
                } else {
                    val other = chat!!.unreadCount.get(i)

                    value.put(i, other!!.plus(1))
                }
            }

        }
        return value

    }


    fun sendImage(
        chatroomData: ChatRoom,
        currentUid: String,
        image: String
    ) {

        val currentTime = Date().time

        firestore.collection("users").document(currentUid).get().addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)
            var userImage = user!!.userphoto
            var usernm = user!!.usernm
            val chatComment = ChatComment(
                chatroomData.roomId,
                userImage,
                currentUid,
                usernm,
                image,
                "2",
                currentTime.toString(),
                mutableMapOf(currentUid to true)

            )
            getToken(currentUid, user!!.userphoto, "사진을 보냈습니다.", chatroomData.roomId, usernm)

            firestore.collection("chatrooms").document(chatroomData.roomId).get()
                .addOnSuccessListener { snapshot ->

                    val chat = snapshot.toObject(ChatRoom::class.java)
                    val destinationmessageCount = chat!!.unreadCount.get(args.destinationUid)
                    val myMessageCount = chat!!.unreadCount.get(currentUid)
                    val value = chat!!.unreadCount

                    if (chat!!.users.size <= 2) {
                        value.put(currentUid, myMessageCount!!)
                        value.put(
                            args.destinationUid, destinationmessageCount!!.plus(
                                1
                            )
                        )
                    } else {

                        for (i in chat!!.users) {
                            if (i.equals(currentUid)) {
                                value.put(currentUid, myMessageCount!!)
                            } else {
                                val other = chat!!.unreadCount.get(i)

                                value.put(i, other!!.plus(1))
                            }
                        }

                    }
                    val c = ChatRoom(
                        true,
                        chat!!.roomId,
                        "",
                        chat.photo,
                        image,
                        currentTime.toString(),
                        chat!!.users.size,
                        value,
                        chat!!.users
                    )
                    firestore.collection("chatrooms").document(chatroomData.roomId)
                        .set(c)

                    firestore.collection("chat").document(currentTime.toString()).set(chatComment)

                }


        }


    }


    fun moveToChatAllUserActivity() {

        binding.addUserButton.setOnClickListener {
            binding.chatDrawerlayout.visibility = View.VISIBLE
            binding.chatDrawerlayout.closeDrawer(Gravity.RIGHT)
            val intent = Intent(requireContext(), ChatAllUserActivity::class.java)
            intent.putExtra("roomId", args.chatroom.roomId)
            startActivity(intent)
        }

    }


    fun setUserAdapter() {
        binding.addUserRecyclerview.adapter = useradapter


    }

    fun getAllChatRoomUser() {

        val userList = mutableListOf<User>()

        firestore.collection("chatrooms").document(args.chatroom.roomId).get()
            .addOnSuccessListener { snapshot ->
                val chatroom = snapshot.toObject(ChatRoom::class.java)

                firestore.collection("users").get().addOnSuccessListener { user ->
                    for (i in chatroom!!.users) {


                        val users = user.toObjects(User::class.java)

                        for (j in users) {
                            if (j.uid.equals(i)) {
                                userList.add(j)
                            }
                        }
                    }
                    allChatroomUsers.postValue(userList)

                    CoroutineScope(Dispatchers.Main).launch {
                        userViewModel.getUsers(allChatroomUsers).collectLatest {
                            useradapter.submitData(it)
                        }
                    }
                }
            }
    }

    private fun openImagesPicker(chatroomData: ChatRoom, currentUid: String) {

        TedBottomPicker.with(requireActivity())
            .setPeekHeight(1600)
            .showGalleryTile(false)
            .setPreviewMaxCount(1000)
            .setSelectMaxCount(1)
            .setSelectMaxCountErrorText("1개만 선택이 가능합니다.")
            .showTitle(false)
            .setTitleBackgroundResId(R.color.light_red)
            .setGalleryTileBackgroundResId(R.color.white)
            .setCompleteButtonText("전송")
            .setEmptySelectionText("사진 선택")
            .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>?) {


                    if (uriList!!.size > 0) {

                        val riverRef: StorageReference =
                            firebaseStorage.getReference()
                                .child("chat_image/" + chatroomData.roomId + "/" + chatroomData.lastDate + ".jpg")


                        val uploadTask: UploadTask = riverRef.putFile(uriList.get(0))

                        uploadTask.addOnSuccessListener {

                            riverRef.downloadUrl.addOnSuccessListener { uri ->

                                sendImage(chatroomData, currentUid, uri.toString())
                            }
                        }
                    }

                }
            })
    }


    private fun getToken(
        currentUid: String,
        currentImage: String,
        message: String,
        chatId: String,
        currentnm: String
    ) {



        firestore.collection("chatrooms").document(args.chatroom.roomId).get()
            .addOnSuccessListener { snapshot ->
                val chatroom = snapshot.toObject(ChatRoom::class.java)

                firestore.collection("users").get().addOnSuccessListener { user ->
                    for (i in chatroom!!.users) {

                        if (i.equals(currentUid)) {
                            continue
                        }
                        val users = user.toObjects(User::class.java)

                        for (j in users) {

                            var title = ""
                            if (j.uid.equals(i)) {

                                title += j.usernm
                                val to = JSONObject()
                                val data = JSONObject()
                                val now = System.currentTimeMillis()

                                data.put("hisId", currentUid)
                                data.put("hisImage", currentImage)
                                data.put("message", message)
                                data.put("chatId", chatId)
                                data.put("title", currentnm)
                                data.put("time", now)

                                to.put("to", j.token)
                                to.put("data", data)

                                sendNotification(to, Request.Method.POST)

                            }


                        }

                    }


                }


            }
    }


    private fun sendNotification(to: JSONObject, method: Int) {


        val request: JsonObjectRequest = object : JsonObjectRequest(
            method,
            "https://fcm.googleapis.com/fcm/send",
            to,
            Response.Listener { response: JSONObject ->
                Log.d("TAG", "onResponse : " + response)
            },
            Response.ErrorListener {
                Log.d("TAG", "onError : " + it)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {

                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] =
                    "key=" + "AAAAtGgkK28:APA91bGakBBK1zPClVRQvOQd2WcSsz77icRoJvlldzq0_jXYrx8rnl--N8sW80vbn7w6PerKi-OArYuLV5bbFfVP94ZjTRGwtFX2_WsRwMzYTuMjbx6JvcddzuuG4EecR7pNicD0x4Tr"
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }


        val requestQueue = Volley.newRequestQueue(requireContext())
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)


    }

    fun onBackpressed() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (args.notification == false) {
                    findNavController().popBackStack()


                } else {
                    findNavController().navigate(R.id.chatFragment)
                    navBar = requireActivity().findViewById(R.id.bottom_navigation)
                    navBar.selectedItemId = R.id.chatFragment
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE


    }
}
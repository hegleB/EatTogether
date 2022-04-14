package com.qure.eattogether2.view.chat

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.qure.eattogether2.R
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.Setting
import com.qure.eattogether2.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException
import java.util.*
import javax.inject.Inject


const val channelId1 = "my_channel1"
const val channelId2 = "my_channel2"


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var prefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]
            val chatId = map["chatId"]

            prefs = this.getSharedPreferences("prefs", MODE_PRIVATE)
            prefsEditor = prefs!!.edit()


            val uid = firebaseAuth.currentUser!!.uid



            firebaseFirestore.collection("setting").document(uid)
                .addSnapshotListener { snapshot, e ->

                    val data = snapshot!!.toObject<Setting>()

                    prefsEditor!!.putBoolean("notification", data!!.message)
                    prefsEditor!!.putBoolean("sound", data!!.sound)
                    prefsEditor!!.putBoolean("vibration", data!!.vibration)
                    prefsEditor!!.apply()

                }

            firebaseFirestore.collection("chatrooms").whereArrayContains("users", uid)
                .addSnapshotListener { snapshot, e ->


                    var chatCount = snapshot!!.toObjects(ChatRoom::class.java)


                    var count = 0
                    for (i in chatCount) {
                        count += i.unreadCount.get(uid)!!
                    }
                    prefsEditor!!.putInt("messageCount", count)
                    prefsEditor!!.apply()



                }


            if (prefs!!.getBoolean("notification", true) == true) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    createOreoNotification(
                        title!!,
                        message!!,
                        hisId!!,
                        hisImage!!,
                        chatId!!,
                        prefs!!.getBoolean("notification", true),
                        prefs!!.getBoolean("sound", true),
                        prefs!!.getBoolean("vibration", true),
                        prefs!!.getInt("messageCount", 0)


                    )

                } else {
                    createNormalNotification(
                        title!!,
                        message!!,
                        hisId!!,
                        hisImage!!,
                        chatId!!,
                        prefs!!.getBoolean("notification", true)
                    )
                }

            } else {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    createOreoNotification(
                        title!!,
                        message!!,
                        hisId!!,
                        hisImage!!,
                        chatId!!,
                        prefs!!.getBoolean("notification", true),
                        prefs!!.getBoolean("sound", true),
                        prefs!!.getBoolean("vibration", true),
                        prefs!!.getInt("messageCount", 0)

                    )

                } else {
                    createNormalNotification(
                        title!!,
                        message!!,
                        hisId!!,
                        hisImage!!,
                        chatId!!,
                        prefs!!.getBoolean("notification", true)
                    )
                }
            }


        }
    }


    override fun onNewToken(token: String) {
        Log.d("mytag", "Refreshed token: $token")
        super.onNewToken(token)
    }

    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        chatId: String,
        isNotify: Boolean
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, MessageFragment::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("chatId", chatId)


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Glide.with(applicationContext).asBitmap().load(hisImage)
            .into(object : SimpleTarget<Bitmap?>() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    val builder1 = NotificationCompat.Builder(applicationContext, channelId1)
                    val builder2 = NotificationCompat.Builder(applicationContext, channelId2)
                    builder1.setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.img_logo)
                        .setLargeIcon(resource)
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setColor(ResourcesCompat.getColor(resources, R.color.black, null))
                        .setContentIntent(pendingIntent)


                    builder2.setContentTitle(title)
                        .setAutoCancel(true)
                        .setColor(ResourcesCompat.getColor(resources, R.color.black, null))
                        .setContentIntent(pendingIntent)
                        .setSound(uri)

                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


                    manager.notify(Random().nextInt(85), builder1.build())
                    manager.notify(Random().nextInt(86), builder1.build())

                }
            })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        chatId: String,
        isNotify: Boolean,
        isSound: Boolean,
        isVibrate: Boolean,
        count : Int
    ) {


        val channel1 = NotificationChannel(
            channelId1,
            "새 메시지 알림",
            NotificationManager.IMPORTANCE_LOW
        )

        val channel2 = NotificationChannel(
            channelId2,
            "알림을 받지 않는 메시지",
            NotificationManager.IMPORTANCE_MIN
        )

        channel1.setShowBadge(true)
        channel1.enableLights(true)
        channel1.vibrationPattern = LongArray(1) { 0 }
        channel1.enableVibration(true)
        channel1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel1)
        manager.createNotificationChannel(channel2)

        val intent = Intent(this, MessageFragment::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("chatId", chatId)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        firebaseFirestore.collection("chatrooms").document(chatId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val chatroom = it.result.toObject<ChatRoom>()
                var bundle =
                    bundleOf(
                        "chatroom" to chatroom,
                        "destinationId" to hisId,
                        "notification" to true
                    )


                val pendingIntent1 = NavDeepLinkBuilder(applicationContext)
                    .setComponentName(HomeActivity::class.java)
                    .setGraph(R.navigation.nav_home)
                    .setArguments(bundle)
                    .setDestination(R.id.messageFragment3)
                    .createPendingIntent()
                if (!hisImage.equals("")) {
                    Glide.with(applicationContext).asBitmap().load(hisImage)
                        .into(object : SimpleTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                if (isNotify == true) {
                                    notificationBuilder1(
                                        channelId1,
                                        title,
                                        message,
                                        resource,
                                        pendingIntent1,
                                        manager,
                                        isNotify,
                                        isSound,
                                        isVibrate,
                                        count
                                    )
                                } else {
                                    notificationBuilder2(
                                        channelId2,
                                        title,
                                        message,
                                        resource,
                                        pendingIntent1,
                                        isNotify,
                                        manager,
                                        count
                                    )
                                }
                            }
                        })


                } else {
                    Glide.with(applicationContext).asBitmap().load(R.drawable.ic_user)
                        .into(object : SimpleTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                if (isNotify) {
                                    notificationBuilder1(
                                        channelId1,
                                        title,
                                        message,
                                        resource,
                                        pendingIntent1,
                                        manager,
                                        isNotify,
                                        isSound,
                                        isVibrate,
                                        count
                                    )
                                } else {
                                    notificationBuilder2(
                                        channelId2,
                                        title,
                                        message,
                                        resource,
                                        pendingIntent1,
                                        isNotify,
                                        manager,
                                        count
                                    )
                                }
                            }
                        })
                }
            }


        }
    }


    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationBuilder1(
        channelId: String,
        title: String,
        message: String,
        resource: Bitmap,
        pendingIntent1: PendingIntent,
        manager: NotificationManager,
        isNotify: Boolean,
        isSound: Boolean,
        isVibrate: Boolean,
        count : Int
    ) {
        val notification =
            Notification.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.img_logo)
                .setLargeIcon(resource)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setBadgeIconType(1)
                .setNumber(count)
                .setColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.black,
                        null
                    )
                )
                .setContentIntent(pendingIntent1)
                .build()

        val now = System.currentTimeMillis()
        manager.notify(100, notification)

        if (isSound) {
            PushCallDisplay()
            PushCallSound()
        }

        if (isVibrate) {
            PushCallDisplay()
            PushCallVibrator()
        }


    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationBuilder2(
        channelId: String,
        title: String,
        message: String,
        resource: Bitmap,
        pendingIntent1: PendingIntent,
        isNotify: Boolean,
        manager: NotificationManager,
        count : Int
    ) {
        val notification =
            Notification.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.img_logo)
                .setLargeIcon(resource)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setAutoCancel(true)
                .setBadgeIconType(1)
                .setNumber(count)
                .setColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.black,
                        null
                    )
                )
                .setContentIntent(pendingIntent1)
                .build()

        val now = System.currentTimeMillis()
        manager.notify(101, notification)


    }

    @SuppressLint("InvalidWakeLockTag")
    fun PushCallDisplay() {
        val tag = "wake"
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val wakelock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                tag
            )
            wakelock.acquire()
            wakelock.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //======== [모바일 진동 강제 발생 메소드] ========
    fun PushCallVibrator() {

        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //======== [모바일 알림음 강제 발생 메소드] ========
    fun PushCallSound() {

        try {
            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(applicationContext, defaultSoundUri)
            ringtone.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.capstone_design.a1209_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//유저의 토큰 정보 가져와서
//Firebas서버로 메세지 보내라고 명령하고
//Firebase 서버에서 앱으로 메세지 보내주고
//앱에서는 메세지를 받아서
//앱에서는 알림을 띄워줌
class FirebaseService : FirebaseMessagingService() {
    val TAG = "firebaseService"
    private lateinit var auth: FirebaseAuth
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        Log.e(TAG, message.notification?.title.toString())
//        Log.e(TAG, message.notification?.body.toString())
//
//        val title=message.notification?.title.toString()
//        val body=message.notification?.body.toString()

        val title = message.data["title"].toString()
        val body = message.data["content"].toString()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE
        val formatted = current.format(formatter)
        val receiver_uid = message.data["receiver_uid"].toString()
        val chatroom_title = message.data["chatroom_title"].toString()

        createNotificationChannel()
        sendNotification(title, body)
        if (body.contains("카테고리")) {
            newNotification(body, formatted)
        } else if (body.contains("인원")) {
            fullUserNotification(receiver_uid, body, formatted, chatroom_title)
        } else if (body.contains("입장")) {
            enterNotification(receiver_uid, body, formatted, chatroom_title)
        } else if (body.contains("송금")) {
            payNotification(receiver_uid, body, formatted, chatroom_title)
        } else if (body.contains("주문서")) {
            receiptNotification(receiver_uid, body, formatted, chatroom_title)
        }
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(title: String, body: String) {
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.main_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(123, builder.build())
        }
    }

    //firebase-users-notilist에 올리기(새글알림)
    private fun newNotification(body: String, date: String) {
        var category = ""
        if (body.contains("일식")) {
            category = "japan"
        }
        if (body.contains("한식")) {
            category = "korean"
        }
        if (body.contains("양식")) {
            category = "asian"
        }
        if (body.contains("분식")) {
            category = "bun"
        }
        if (body.contains("치킨")) {
            category = "chicken"
        }
        if (body.contains("피자")) {
            category = "pizza"
        }
        if (body.contains("패스트")) {
            category = "fastfood"
        }
        if (body.contains("도시락")) {
            category = "bento"
        }
        if (body.contains("중식")) {
            category = "chi"
        }
        if (body.contains("카페")) {
            category = "cafe"
        }
        val newNoti = notiData(category, body, date)
        auth = Firebase.auth
        FBRef.usersRef.child(auth.currentUser?.uid.toString()).child("newNoti").push()
            .setValue(newNoti)


    }

    //firebase-users-notilist에 올리기(정원 다 참 알림)
    private fun fullUserNotification(
        uid: String,
        body: String,
        date: String,
        chatroom_title: String
    ) {

        val newNoti = notiData("full", body, date, chatroom_title)
        auth = Firebase.auth
        FBRef.usersRef.child(uid).child("newNoti").push()
            .setValue(newNoti)


    }

    //firebase-users-notilist에 올리기(정원 다 참 알림)
    private fun enterNotification(uid: String, body: String, date: String, chatroom_title: String) {

        val newNoti = notiData("enter", body, date, chatroom_title)
        auth = Firebase.auth
        FBRef.usersRef.child(uid).child("newNoti").push()
            .setValue(newNoti)


    }

    //firebase-users-notilist에 올리기(송금 알림)
    private fun payNotification(uid: String, body: String, date: String, chatroom_title: String) {

        val newNoti = notiData("pay", body, date, chatroom_title)
        auth = Firebase.auth
        FBRef.usersRef.child(uid).child("newNoti").push()
            .setValue(newNoti)


    }

    //firebase-users-notilist에 올리기(주문서 알림)
    private fun receiptNotification(uid: String, body: String, date: String, chatroom_title: String) {

        val newNoti = notiData("receipt", body, date, chatroom_title)
        auth = Firebase.auth
        FBRef.usersRef.child(uid).child("newNoti").push()
            .setValue(newNoti)


    }
}
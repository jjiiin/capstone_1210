package com.capstone_design.a1209_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.capstone_design.a1209_app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//유저의 토큰 정보 가져와서
//Firebas서버로 메세지 보내라고 명령하고
//Firebase 서버에서 앱으로 메세지 보내주고
//앱에서는 메세지를 받아서
//앱에서는 알림을 띄워줌
class FirebaseService : FirebaseMessagingService() {
    val TAG="firebaseService"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        Log.e(TAG, message.notification?.title.toString())
//        Log.e(TAG, message.notification?.body.toString())
//
//        val title=message.notification?.title.toString()
//        val body=message.notification?.body.toString()

        val title=message.data["title"].toString()
        val body=message.data["content"].toString()
        createNotificationChannel()
        sendNotification(title, body)
    }


    private fun createNotificationChannel(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name="name"
            val descriptionText="description"
            val importance= NotificationManager.IMPORTANCE_DEFAULT
            val channel=NotificationChannel("Test_Channel",name,importance).apply {
                description=descriptionText
            }

            val notificationManager:NotificationManager=
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun  sendNotification(title:String,body:String){
        var builder=NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)){
            notify(123,builder.build())
        }
    }

}
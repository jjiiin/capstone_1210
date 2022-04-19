package com.capstone_design.a1209_app.fcm

data class NotiModel(
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val receiver_uid: String = "",
    val chatroom_title: String = ""
)
package com.capstone_design.a1209_app.fcm

import java.util.*

data class NotiModel(
    val title: String = "",
    val content: String = "",
    val date: Date = Date(),
    val receiver_uid: String = "",
    val chatroom_title: String = "",
    val chatroom_key: String = ""
)
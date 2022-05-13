package com.capstone_design.a1209_app.dataModels

import java.util.*

data class NotiData(
    val category: String = "",
    val content: String = "",
    val date: Date = Date(),
    val chatroom_title: String = "",
    val chatroom_key: String = ""
)

package com.capstone_design.a1209_app.dataModels

import java.util.*

data class ChatData(
    val nickname: String = "",
    val msg: String = "",
    val email: String = "",
    val uid: String = "",
    val time: Date = Date()
)
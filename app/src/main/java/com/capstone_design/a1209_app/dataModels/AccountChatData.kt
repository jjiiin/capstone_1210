package com.capstone_design.a1209_app.dataModels

import java.util.*

data class AccountChatData(
    val nickname: String = "",
    val msg: String = "",
    val email: String = "",
    val uid: String = "",
    val time: Long = 0,
    val bankName: String = "",
    val receiverName: String = "",
    val accountNum: String = ""
)
package com.capstone_design.a1209_app.dataModels

import java.io.Serializable

data class ReceiptData(
    val menu: String = "",
    val price: Int = 0,
    val option: String = "",
    val nickname: String = "",
    val uid: String = "",
    val check_paid: Boolean = false
)
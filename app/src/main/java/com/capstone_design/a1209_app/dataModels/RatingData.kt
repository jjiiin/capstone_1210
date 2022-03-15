package com.capstone_design.a1209_app.dataModels

import java.util.*

data class RatingData(
    val rating: Float = 0.0F,
    val content: String = "",
    val writer: String = "",
    val saved_time: Date = Date(),
    var display_time:String = ""
)
package com.capstone_design.a1209_app

import android.os.Parcel
import android.os.Parcelable


data class dataModel(
    val title: String = "",
    //val image
    val person: String = "",
    val time: String = "",
    val fee: String = "",
    val place: String = "",
    val link: String = "",
    val mention: String = "",
    //글쓴이 정보 추가
    val writer: String = "",
    //채팅방 키 추가
    val chatroomkey: String = ""
)
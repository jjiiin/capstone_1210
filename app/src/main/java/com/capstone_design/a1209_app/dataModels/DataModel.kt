package com.capstone_design.a1209_app.dataModels

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

data class dataModel (
    val title: String="",
    val category:String="",
    val image:String="",
    val person : String="",
    val time: String="",
    //즉각주문
    val quick:String="",
    val fee: String="",
    val place:String="",
    val placeAddress: String="",
    val placeDetail:String="",
    val link: String="",
    val mention: String="",
    val lat:String="",
    val lng:String="",
    //글쓴이 정보 추가
    val writer: String = "",
    //채팅방 키 추가
    val chatroomkey: String = ""
)
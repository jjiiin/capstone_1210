package com.capstone_design.a1209_app.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        private val database = Firebase.database

        val boardRef=database.getReference("contents_gyeong")
        val usersRef = database.getReference("users")
        val chatRoomsRef = database.getReference("chatRooms")
        val userRoomsRef = database.getReference("userRooms")
    }



}
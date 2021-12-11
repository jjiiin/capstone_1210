package com.capstone_design.a1209_app.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        private val database = Firebase.database

        val boardRef=database.getReference("contents")

    }



}
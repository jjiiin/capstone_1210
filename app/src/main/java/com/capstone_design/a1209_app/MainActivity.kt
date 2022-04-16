package com.capstone_design.a1209_app

import android.content.pm.PackageManager


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encode
import android.util.Log
import com.capstone_design.a1209_app.databinding.ActivityMainBinding
import com.capstone_design.a1209_app.fragment.ChatFragment
import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.a1209_app.fragment.MyFragment
import com.capstone_design.a1209_app.fragment.NoteFragment

import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {
    private  var mbinding : ActivityMainBinding?=null
    private val binding get()=mbinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getAppKeyHash()

        mbinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bn_main=binding.bnNav
        bn_main.itemIconTintList = null


        bn_main.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.home-> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val homeFragment = HomeFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                    }
                    R.id.chat -> {
                        val chatFragment = ChatFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.fl_container, chatFragment).commit()
                    }
                    R.id.note -> {
                        val noteFragment = NoteFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.fl_container, noteFragment).commit()
                    }
                    R.id.my -> {
                        val myFragment = MyFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.fl_container, myFragment).commit()
                    }
                }
                true
            }
            selectedItemId = R.id.home
        }
    }
//    fun getAppKeyHash() {
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for(i in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(i.toByteArray())
//
//                val something = String(Base64.encode(md.digest(),0)!!)
//                Log.e("Debug key", something)
//            }
//        } catch(e: Exception) {
//            Log.e("Not found", e.toString())
//        }
//    }

    }

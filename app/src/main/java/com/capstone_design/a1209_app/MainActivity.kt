package com.capstone_design.a1209_app

import android.content.pm.PackageManager


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encode
import android.util.Log
import androidx.fragment.app.Fragment
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.ActivityMainBinding
import com.capstone_design.a1209_app.fragment.ChatFragment
import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.a1209_app.fragment.MyFragment
import com.capstone_design.a1209_app.fragment.NoteFragment
import java.io.Serializable

import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {
    private  var mbinding : ActivityMainBinding?=null
    private val binding get()=mbinding!!
    private var page=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getAppKeyHash()

        mbinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bn_main=binding.bnNav
        bn_main.itemIconTintList = null

        page=intent.getStringExtra("page").toString()
        if(page=="my"){
            setFragment(MyFragment())
        }

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
    //fragment 간 데이터 전달
    fun setDataAtFragment(fragment:Fragment, items:MutableList<dataModel> ){
        val bundle=Bundle()
        bundle.putSerializable("list",items as Serializable)

        fragment.arguments=bundle
        setFragment(fragment)
    }

    fun setFragment(fragment: Fragment){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment)
        transaction.commit()
    }
}

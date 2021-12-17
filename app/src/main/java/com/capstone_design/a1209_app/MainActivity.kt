package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone_design.a1209_app.databinding.ActivityMainBinding
import com.capstone_design.a1209_app.fragment.ChatFragment
import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.a1209_app.fragment.MyFragment
import com.capstone_design.a1209_app.fragment.NoteFragment

class MainActivity : AppCompatActivity() {
    private  var mbinding : ActivityMainBinding?=null
    private val binding get()=mbinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

    }

package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.databinding.ActivityAddressSearchBinding
import com.capstone_design.a1209_app.databinding.ActivityBoardWirteBinding

class AddressSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_address_search)

        //도로명 주소api로 주소 찾기
        binding.searchBtn1.setOnClickListener {

        }
        //GPS로 현재 위치로 주소 설정하기
        binding.mapSearch.setOnClickListener {
            val intent = Intent(this, MylocSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }


    }

}
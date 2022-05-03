package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone_design.a1209_app.dataModels.AccountData
import com.capstone_design.a1209_app.databinding.ActivityMypageAccountBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class Mypage_Account_Activity : AppCompatActivity() {

    lateinit var binding: ActivityMypageAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_account)

        //계좌 목록 불러오기
        getAccount()

        binding.backbtn.setOnClickListener { onBackPressed() }

    }

    fun getAccount() {
        FBRef.usersRef.child(Auth.current_uid).child("account").get()
            .addOnSuccessListener {
                val data = it.getValue(AccountData::class.java)!!
                binding.tvBankName.text = data.bankName
                binding.tvReceiverName.text = data.receiverName
                binding.tvAccountNum.text = data.accountNum
            }
            .addOnFailureListener { }
    }
}
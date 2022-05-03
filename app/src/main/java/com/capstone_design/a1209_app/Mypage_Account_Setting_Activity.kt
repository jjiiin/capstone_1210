package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.dataModels.AccountData
import com.capstone_design.a1209_app.databinding.ActivityMypageAccountSettingBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef

class Mypage_Account_Setting_Activity : AppCompatActivity() {

    lateinit var binding: ActivityMypageAccountSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_account_setting)
        binding.backbtn.setOnClickListener { onBackPressed() }
        binding.btnDone.setOnClickListener {

            val bankName: String = binding.tvBankName.text.toString()
            val receiverName: String = binding.tvReceiverName.text.toString()
            val accountNum: String = binding.tvAccountNum.text.toString()

            val accountData = AccountData(bankName, receiverName, accountNum)
            FBRef.usersRef.child(Auth.current_uid).child("account").setValue(accountData)

            binding.tvBankName.setText("")
            binding.tvReceiverName.setText("")
            binding.tvAccountNum.setText("")

            val intent = Intent(this, Mypage_Account_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
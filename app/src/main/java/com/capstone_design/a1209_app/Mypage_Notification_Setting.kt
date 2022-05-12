package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.databinding.ActivityMypageNotificationSettingBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.common.util.DataUtils

class Mypage_Notification_Setting : AppCompatActivity() {
    lateinit var binding: ActivityMypageNotificationSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage_notification_setting)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_notification_setting)

        setSwitch()

        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        binding.switchEnter.setOnCheckedChangeListener(object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                FBRef.usersRef.child(Auth.current_uid).child("switch_enterNoti").setValue(isChecked)
            }

        })

        binding.switchKeyword.setOnCheckedChangeListener(object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                FBRef.usersRef.child(Auth.current_uid).child("switch_kwNoti").setValue(isChecked)
            }

        })

    }

    fun setSwitch(){
        FBRef.usersRef.child(Auth.current_uid).child("switch_enterNoti").get().addOnSuccessListener {
            if(it.getValue() == null){
                binding.switchEnter.isChecked = true
            }else{
                binding.switchEnter.isChecked = it.getValue() as Boolean
            }
        }
        FBRef.usersRef.child(Auth.current_uid).child("switch_kwNoti").get().addOnSuccessListener {
            Log.d("뭘까", it.toString())
            if(it.getValue() == null){
                binding.switchKeyword.isChecked = true
            }else{
                binding.switchKeyword.isChecked = it.getValue() as Boolean
            }

        }
    }
}
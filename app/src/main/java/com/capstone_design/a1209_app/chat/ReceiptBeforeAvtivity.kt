package com.capstone_design.a1209_app.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.databinding.ActivityReceiptBeforeBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef

class ReceiptBeforeAvtivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBeforeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_before)

        binding = setContentView(this, R.layout.activity_receipt_before)

        val intent = getIntent()
        val nickname = intent.getStringExtra("닉네임")
        val chatroomkey = intent.getStringExtra("채팅방키")

        binding.nicknameTv.setText(nickname)

        binding.doneBtn.setOnClickListener {
            val menu = binding.menuEdit.text.toString()
            val price = binding.priceEdit.text.toString()
            val option = binding.optionEdit.text.toString()
            val data = ReceiptData(menu, price, option, nickname!!)
            FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts").child(Auth.current_uid).setValue(data)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

}
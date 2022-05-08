package com.capstone_design.a1209_app.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.ActivityChangeDeliveryFeeBinding
import com.capstone_design.a1209_app.utils.FBRef

class ChangeDeliveryFeeActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangeDeliveryFeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_delivery_fee)

        val chatroomkey = intent.getStringExtra("chatroomkey")

        //현재 1/n 가격 출력
        binding.tvIndividualDeliveryFee.text = ReceiptDoneActivity.indiv_delivery_fee.toString()

        //변경한 1/n 가격 저장
        binding.btnDone.setOnClickListener {
            val data = binding.etDeliveryFee.text.toString().toInt()
            FBRef.chatRoomsRef.child(chatroomkey!!)
                .child("receipts")
                .child("individual_fee").setValue(
                    data
                )
            ReceiptDoneActivity.indiv_delivery_fee = data
            ReceiptDoneActivity.isFeeChange = true
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        //뒤로가기
        binding.backbtn.setOnClickListener {
            onBackPressed()
        }
    }
}
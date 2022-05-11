package com.capstone_design.a1209_app.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil.setContentView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.databinding.ActivityReceiptBeforeBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReceiptBeforeAvtivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBeforeBinding
    var userNum = 0
    var host_token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_before)

        binding = setContentView(this, R.layout.activity_receipt_before)

        val intent = getIntent()
        val current_nickname = intent.getStringExtra("닉네임")
        val chatroomkey = intent.getStringExtra("채팅방키")
        val hostUid = intent.getStringExtra("hostUid")
        val roomTitle = intent.getStringExtra("roomTitle")

        binding.nicknameTv.setText(current_nickname)

        CoroutineScope(Dispatchers.IO).launch {
            getHostToken(hostUid!!)
        }
        Log.d("토큰", host_token)

        binding.doneBtn.setOnClickListener {
            val menu = binding.menuEdit.text.toString()
            val price = binding.priceEdit.text.toString().toInt()
            val option = binding.optionEdit.text.toString()
            val uid = Auth.current_uid
            val data = ReceiptData(menu, price, option, current_nickname!!, uid)

            FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts").child(Auth.current_uid)
                .setValue(data).addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        getUserNum(chatroomkey)
                        getIsAllDone(chatroomkey, hostUid!!, roomTitle!!)
                    }
                }
            val intent =
                Intent(this, ReceiptDoneActivity::class.java)
            intent.putExtra("채팅방키", chatroomkey)
            intent.putExtra("닉네임", current_nickname)
            intent.putExtra("hostUid", hostUid)
            intent.putExtra("roomTitle", roomTitle)
            startActivity(intent)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    //현재 참여자 수 가져오기
    suspend fun getUserNum(chatroomkey: String) = suspendCoroutine<Int> { continuation ->
        FBRef.chatRoomsRef.child(chatroomkey!!).child("users").get().addOnSuccessListener {
            for (data in it.children) {
                userNum++
            }
            continuation.resume(userNum)
        }
    }

    //모두 영수증 작성했는지 테스트
    fun getIsAllDone(chatroomkey: String, hostUid: String, roomTitle: String) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .get().addOnSuccessListener {
                var receiptNum = 0
                for (data in it.children) {
                    if (data.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                        receiptNum++
                    }
                }
                //모두 영수증 작성 완료했으면
                if (userNum == receiptNum) {
                    //방장에게 영수증 알림 보내기
                    val notiData_receipt = NotiModel(
                        "Saveat - 알림",
                        "모든 참여자가 주문서를 작성했어요",
                        Calendar.getInstance().time,
                        hostUid,
                        roomTitle
                    )
                    val pushModel_receipt =
                        PushNotification(notiData_receipt, "${host_token}")
                    //testPush(pushModel_receipt)
                }
            }
    }

    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("pushNoti", notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }

    suspend fun getHostToken(hostUid: String) = suspendCoroutine<String> { continuation ->
        FBRef.usersRef.child(hostUid).child("token").get().addOnSuccessListener {
            host_token = it.getValue<String>().toString()
            Log.d("토큰", host_token)
            continuation.resume(host_token)
        }
    }
}
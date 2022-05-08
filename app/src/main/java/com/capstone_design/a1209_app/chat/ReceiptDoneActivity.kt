package com.capstone_design.a1209_app.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.databinding.ActivityReceiptBeforeBinding
import com.capstone_design.a1209_app.databinding.ActivityReceiptDoneBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates

class ReceiptDoneActivity : AppCompatActivity() {


    companion object {
        //var indiv_Fee: Int = 0
        var isFeeChange: Boolean = false
        private lateinit var binding: ActivityReceiptDoneBinding

        var indiv_delivery_fee: Int by Delegates.observable(0) { props, old, new ->
            binding.tvIndividualDeliveryFee.text = indiv_delivery_fee.toString()
        }
        lateinit var rvAdapter: Receipt_RVAdapter
        var delivery_fee: Int = 0
    }

    //리사이클러뷰에 들어갈 아이템 추가
    val items = mutableListOf<ReceiptData>()
    var fee_string: String = ""
    var boardKey: String = ""
    var userNum: Int = 0
    var price_sum: Int = 0
    var isAllPaid = false
    var host_token = ""
    var hostUid = ""
    var roomTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_done)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_done)

        val intent = getIntent()
        val chatroomkey = intent.getStringExtra("채팅방키").toString()
        hostUid = intent.getStringExtra("hostUid").toString()
        roomTitle = intent.getStringExtra("roomTitle").toString()

        //리사이클러뷰 어댑터 연결
        rvAdapter = Receipt_RVAdapter(items, this, chatroomkey, hostUid, roomTitle)
        val rv = findViewById<RecyclerView>(R.id.receipt_rv_view)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼
        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        //파이어베이스의 비동기 방식 -> 동기 방식
        CoroutineScope(Dispatchers.IO).launch {
            getHostToken(hostUid)
            getBoardKey(chatroomkey)
            getDeliveryFee(chatroomkey)
            if (isFeeChange == false) {
                CalculateIndividualFee(chatroomkey)
            } else {
                getIndividualFee(chatroomkey)
            }
            getUserReceipt(chatroomkey)
        }

        //배달비 수정 버튼 누를 시
        binding.imageChangeDeliveryFee.setOnClickListener {
            val intent = Intent(this, ChangeDeliveryFeeActivity::class.java).putExtra(
                "chatroomkey",
                chatroomkey
            )
            startActivity(intent)
        }

        //모두 송금했는지 확인
        //checkAllPaid(chatroomkey, hostUid, roomTitle)
    }

    fun getUserReceipt(chatroomkey: String) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addValueEventListener(object : ValueEventListener {
                var receiptNum = 0
                override fun onDataChange(snapshot: DataSnapshot) {
                    items.clear()
                    price_sum = delivery_fee
                    for (data in snapshot.children) {
                        if (data.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                            val data = data.getValue(ReceiptData::class.java)
                            price_sum += data!!.price
                            if (data.uid.equals(Auth.current_uid)) {
                                //내껄 맨위에 오게
                                items.add(0, data)
                            } else {
                                items.add(data!!)
                            }
                            //items에 변화가 생기면 반영
                            rvAdapter.notifyDataSetChanged()
                            receiptNum++
                        }

                    }
                    binding.tvFoodFee.text = (price_sum - delivery_fee).toString()
                    binding.tvPriceSum.text = price_sum.toString()

                    //만약 사용자가 주문서 다 작성했으면 방장에게 알람
                    /*   if(userNum == receiptNum){
                           val notiData_receipt = NotiModel(
                               "Saveat - 알림",
                               "모든 참여자가 주문서를 작성했어요",
                               "임시",
                               hostUid,
                               roomTitle
                           )
                           val pushModel_receipt = PushNotification(notiData_receipt, "${Receipt_RVAdapter.host_token}")
                           testPush(pushModel_receipt)
                       }*/
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    //파이어베이스의 비동기 방식 -> 동기 방식
    suspend fun getBoardKey(chatroomkey: String) =
        suspendCoroutine<String> { continuation ->
            FBRef.chatRoomsRef.child(chatroomkey!!).child("boardKey")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        boardKey = snapshot.getValue<String>().toString()
                        continuation.resume(boardKey)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

    //파이어베이스의 비동기 방식 -> 동기 방식
    suspend fun getDeliveryFee(chatroomkey: String) =
        suspendCoroutine<Int> { continuation ->
            FBRef.board.child(boardKey).child("fee")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        fee_string = snapshot.getValue<String>().toString()
                        //문자열에서 숫자만 추출하기
                        delivery_fee = fee_string.replace("[^\\d]".toRegex(), "").toInt()
                        price_sum += delivery_fee
                        binding.tvDeliveryFee.text = delivery_fee.toString()
                        continuation.resume(delivery_fee)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

    suspend fun CalculateIndividualFee(chatroomkey: String) =
        suspendCoroutine<Int> { continuation ->
            FBRef.chatRoomsRef.child(chatroomkey!!).child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            userNum++
                        }
                        if (delivery_fee == 0) {
                            indiv_delivery_fee = 0
                        } else {
                            indiv_delivery_fee = delivery_fee / userNum
                        }
                        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
                            .child("individual_fee").setValue(
                                indiv_delivery_fee
                            )
                        binding.tvIndividualDeliveryFee.text = indiv_delivery_fee.toString()
                        continuation.resume(indiv_delivery_fee)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

    suspend fun getIndividualFee(chatroomkey: String) = suspendCoroutine<Int> { continuation ->
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts").child("individual_fee")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    indiv_delivery_fee = snapshot.getValue().toString().toInt()
                    continuation.resume(indiv_delivery_fee)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    //방장 알람 token 가져오기
    suspend fun getHostToken(hostUid: String) = suspendCoroutine<String> { it ->
        FBRef.usersRef.child(hostUid).child("token")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Receipt_RVAdapter.host_token = snapshot.getValue().toString()
                    it.resume(Receipt_RVAdapter.host_token)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    /* private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
         Log.d("pushNoti", notification.toString())
         RetrofitInstance.api.postNotification(notification)
     }*/

}
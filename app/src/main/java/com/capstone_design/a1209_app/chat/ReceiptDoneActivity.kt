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
    }

    //리사이클러뷰에 들어갈 아이템 추가
    val items = mutableListOf<ReceiptData>()
    var fee_string: String = ""
    var delivery_fee: Int = 0
    var boardKey: String = ""
    var userNum: Int = 0
    var price_sum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_done)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_done)

        val intent = getIntent()
        val chatroomkey = intent.getStringExtra("채팅방키").toString()

        //리사이클러뷰 어댑터 연결
        rvAdapter = Receipt_RVAdapter(items, this, chatroomkey)
        val rv = findViewById<RecyclerView>(R.id.receipt_rv_view)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //파이어베이스의 비동기 방식 -> 동기 방식
        CoroutineScope(Dispatchers.IO).launch {
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
    }

    /*fun getUserReceipt(chatroomkey: String) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("데이터-Added", snapshot.toString())
                    items.clear()
                    if (snapshot.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                        val data = snapshot.getValue(ReceiptData::class.java)
                        price_sum += data!!.price
                        items.add(data!!)
                        //items에 변화가 생기면 반영
                        rvAdapter.notifyDataSetChanged()
                    }
                    binding.tvPriceSum.text = price_sum.toString()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("데이터-Changed", snapshot.toString())
                    items.clear()
                    if (snapshot.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                        val data = snapshot.getValue(ReceiptData::class.java)
                        price_sum += data!!.price
                        items.add(data!!)
                        //items에 변화가 생기면 반영
                        rvAdapter.notifyDataSetChanged()
                    }
                    binding.tvPriceSum.text = price_sum.toString()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("데이터-Removed", snapshot.toString())
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("데이터-Moved", snapshot.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }*/

    fun getUserReceipt(chatroomkey: String) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    items.clear()
                    price_sum = delivery_fee
                    for (data in snapshot.children) {
                        if (data.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                            val data = data.getValue(ReceiptData::class.java)
                            price_sum += data!!.price
                            items.add(data!!)
                            //items에 변화가 생기면 반영
                            rvAdapter.notifyDataSetChanged()
                        }

                    }

                    binding.tvPriceSum.text = price_sum.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
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
                        indiv_delivery_fee = delivery_fee / userNum
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
}
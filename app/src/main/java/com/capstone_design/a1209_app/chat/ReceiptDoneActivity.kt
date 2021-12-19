package com.capstone_design.a1209_app.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ReceiptDoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_done)

        val intent = getIntent()
        val chatroomkey = intent.getStringExtra("채팅방키")

        //리사이클러뷰에 들어갈 아이템 추가
        val items = mutableListOf<ReceiptData>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.receipt_rv_view)
        val rvAdapter = Receipt_RVAdapter(items, this)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //저장된 영수증 불러옴
        getUserReceipt(chatroomkey!!, items, rvAdapter)
    }

    fun getUserReceipt(chatroomkey: String, items: MutableList<ReceiptData>, rvAdapter: Receipt_RVAdapter) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    //if (snapshot.key.toString() == Auth.current_uid) {}
                    val data = snapshot.getValue(ReceiptData::class.java)
                    items.add(data!!)
                    //items에 변화가 생기면 반영
                    rvAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
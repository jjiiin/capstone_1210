package com.capstone_design.a1209_app.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class Receipt_RVAdapter(
    val items: MutableList<ReceiptData>,
    val context: ReceiptDoneActivity,
    val chatroomKey: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    val MINE = 0
    val NOT_MINE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //뷰홀더를 생성(레이아웃 생성)하는 코드 작성
        return when (viewType) {
            MINE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receipt_done_rv_item, parent, false)
                MINE_ViewHolder(view)
            }
            NOT_MINE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receipt_done_rv_item, parent, false)
                NOT_MINE_ViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receipt_done_rv_item, parent, false)
                NOT_MINE_ViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //뷰홀더가 재활용될때 실행되는 메소드 작성
        if (holder is NOT_MINE_ViewHolder) {
            holder.bindItems(items[position])
        } else if (holder is MINE_ViewHolder) {
            holder.bindItems(items[position])
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        auth = Firebase.auth
        val current_uid = auth.currentUser!!.uid.toString()
        val chatData = items.get(position)
        /*if(chatData.uid.equals(current_uid)){
            //내 채팅인 경우 0
            return MINE
        }else{
            //다른 사람 채팅인 경우 1
            return NOT_MINE
        }*/
        return NOT_MINE
    }

    inner class NOT_MINE_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ReceiptData) {

            val nickname = itemView.findViewById<TextView>(R.id.nickname_tv)
            nickname.text = item.nickname

            val menu = itemView.findViewById<TextView>(R.id.menu_tv)
            menu.text = item.menu

            val price = itemView.findViewById<TextView>(R.id.price_tv)
            price.text = (item.price + ReceiptDoneActivity.indiv_delivery_fee).toString()

            val option = itemView.findViewById<TextView>(R.id.option_tv)
            option.text = item.option

            val checkBox_pay = itemView.findViewById<CheckBox>(R.id.checkbox_pay)
            checkBox_pay.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                        .child("check_paid").setValue(true)
                } else {
                    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                        .child("check_paid").setValue(false)
                }
            }

            val change_order_btn = itemView.findViewById<LinearLayout>(R.id.layout_change_order)
            change_order_btn.setOnClickListener {
                val intent = Intent(context, ReceiptBeforeAvtivity::class.java)
                intent.putExtra("채팅방키", chatroomKey)
                intent.putExtra("닉네임", item.nickname)
                context.startActivity(intent)
                // context.finish()
            }
            getIsPaid(chatroomKey, item, itemView)
        }
    }

    inner class MINE_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ReceiptData) {

            val menu = itemView.findViewById<TextView>(R.id.menu_tv)
            menu.text = item.menu

            val price = itemView.findViewById<TextView>(R.id.price_tv)
            price.text = (item.price + ReceiptDoneActivity.indiv_delivery_fee).toString()

            val option = itemView.findViewById<TextView>(R.id.option_tv)
            option.text = item.option

            val checkBox_pay = itemView.findViewById<CheckBox>(R.id.checkbox_pay)
            checkBox_pay.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                        .child("check_paid").setValue(true)
                } else {
                    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                        .child("check_paid").setValue(false)
                }
            }

            val change_order_btn = itemView.findViewById<LinearLayout>(R.id.layout_change_order)
            change_order_btn.setOnClickListener {
                val intent = Intent(context, ReceiptBeforeAvtivity::class.java)
                context.startActivity(intent)
                context.finish()
            }
        }
    }
}

fun getIsPaid(chatroomKey: String, item: ReceiptData, itemView: View) {
    val checkBox_pay = itemView.findViewById<CheckBox>(R.id.checkbox_pay)
    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
        .child("check_paid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                checkBox_pay.isChecked = snapshot.getValue() as Boolean
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
}
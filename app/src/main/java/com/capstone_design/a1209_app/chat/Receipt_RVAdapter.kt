package com.capstone_design.a1209_app.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.chat.Receipt_RVAdapter.Companion.isAllPaid
import com.capstone_design.a1209_app.dataModels.ReceiptData
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.protobuf.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Receipt_RVAdapter(
    val items: MutableList<ReceiptData>,
    val context: ReceiptDoneActivity,
    val chatroomKey: String,
    val hostUid: String,
    val roomTitle: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    val MINE = 0
    val NOT_MINE = 1

    companion object {
        var isAllPaid = false
        var host_token = ""
    }

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
        val recieptData = items.get(position)
        if (recieptData.uid.equals(Auth.current_uid)) {
            //내 채팅인 경우 0
            return MINE
        } else {
            //다른 사람 채팅인 경우 1
            return NOT_MINE
        }
        return NOT_MINE
    }

    inner class NOT_MINE_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ReceiptData) {
            getImage(itemView, item)
            val nickname = itemView.findViewById<TextView>(R.id.nickname_tv)
            nickname.text = item.nickname

            val menu = itemView.findViewById<TextView>(R.id.menu_tv)
            menu.text = item.menu

            val price = itemView.findViewById<TextView>(R.id.price_tv)
            price.text = (item.price + ReceiptDoneActivity.indiv_delivery_fee).toString()

            val option = itemView.findViewById<TextView>(R.id.option_tv)
            option.text = item.option

            itemView.findViewById<FrameLayout>(R.id.bubble).visibility = View.GONE
            itemView.findViewById<LinearLayout>(R.id.detail).visibility = View.GONE
            itemView.findViewById<LinearLayout>(R.id.layout_change_order).visibility = View.GONE

            //방장의 주문서에는 송금완료 버튼 안보이게
            if (item.uid == hostUid) {
                if(item.uid != Auth.current_uid){
                    itemView.findViewById<TextView>(R.id.nickname_tv).background = null
                }
                nickname.text = nickname.text.toString() + "(방장)"
                itemView.findViewById<CheckBox>(R.id.checkbox_pay).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.tv_paid).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.tv_host_paid).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.host_icon).visibility = View.VISIBLE
            } else {
                if(item.uid != Auth.current_uid){
                    itemView.findViewById<TextView>(R.id.nickname_tv).background = null
                }
                //입금완료 체크박스
                val checkBox_pay = itemView.findViewById<CheckBox>(R.id.checkbox_pay)
                //다른사람 송금완료 버튼은 안눌리게
                checkBox_pay.isClickable = false
                //송금완료 버튼 상태 가져옴
                getIsPaid(chatroomKey, item, itemView)
            }


        }
    }

    inner class MINE_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ReceiptData) {
            getImage(itemView, item)
            val nickname = itemView.findViewById<TextView>(R.id.nickname_tv)
            nickname.text = item.nickname

            val menu = itemView.findViewById<TextView>(R.id.menu_tv)
            menu.text = item.menu

            val price = itemView.findViewById<TextView>(R.id.price_tv)
            price.text = (item.price + ReceiptDoneActivity.indiv_delivery_fee).toString()

            val option = itemView.findViewById<TextView>(R.id.option_tv)
            option.text = item.option

            //새로 반영된 xml
            val foodPrice = itemView.findViewById<TextView>(R.id.tv_foodPrice)
            foodPrice.text = item.price.toString()

            val deliveryPrice = itemView.findViewById<TextView>(R.id.tv_deliveryPrice)
            deliveryPrice.text = ReceiptDoneActivity.indiv_delivery_fee.toString()

            //말풍선 안에 들어갈 텍스트
            val savePrice = itemView.findViewById<TextView>(R.id.tv_savePrice)
            savePrice.text =
                (ReceiptDoneActivity.delivery_fee - ReceiptDoneActivity.indiv_delivery_fee).toString()

            //방장의 주문서에는 송금완료 버튼 안보이게
            if (item.uid == hostUid) {
                if(item.uid != Auth.current_uid){
                    itemView.findViewById<TextView>(R.id.nickname_tv).background = null
                }
                nickname.text = nickname.text.toString() + "(방장)"
                itemView.findViewById<CheckBox>(R.id.checkbox_pay).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.tv_paid).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.tv_host_paid).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.host_icon).visibility = View.VISIBLE
            } else {
                if(item.uid != Auth.current_uid){
                    itemView.findViewById<TextView>(R.id.nickname_tv).background = null
                }
                //입금완료 체크박스
                val checkBox_pay = itemView.findViewById<CheckBox>(R.id.checkbox_pay)
                checkBox_pay.setOnClickListener {
                    if (checkBox_pay.isChecked) {
                        FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                            .child("check_paid").setValue(true)
                        //모두 송금했는지 확인
                        checkAllPaid(chatroomKey, hostUid, roomTitle)
                    } else {
                        FBRef.chatRoomsRef.child(chatroomKey).child("receipts").child(item.uid)
                            .child("check_paid").setValue(false)
                    }
                }

                //송금완료 버튼 상태 가져옴
                getIsPaid(chatroomKey, item, itemView)
            }


            val change_order_btn = itemView.findViewById<LinearLayout>(R.id.layout_change_order)
            change_order_btn.setOnClickListener {
                val intent = Intent(context, ReceiptBeforeAvtivity::class.java)
                intent.putExtra("채팅방키", chatroomKey)
                intent.putExtra("닉네임", item.nickname)
                context.startActivity(intent)
                // context.finish()
            }
        }
    }

    fun getImage(itemView: View, item: ReceiptData) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + item.uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(context).load(it).into(itemView.findViewById(R.id.profile_img))
            }.addOnFailureListener {
                itemView.findViewById<ImageView>(R.id.profile_img)
                    .setImageResource(R.drawable.profile_cat)
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
            }

        })
}

fun checkAllPaid(chatroomKey: String, hostUid: String, roomTitle: String) {
    FBRef.chatRoomsRef.child(chatroomKey).child("receipts").get().addOnSuccessListener {
        for (data in it.children) {
            if (data.getValue()!!.javaClass.toString() == "class java.util.HashMap") {
                val data = data.getValue<ReceiptData>()
                isAllPaid = data!!.check_paid
                if (isAllPaid == false) {
                    break
                }
            }
        }
        //모두 송금했으면 방장에게 알림 보내기
        if (isAllPaid) {
            //방장에게 송금알림 보내기
            val notiData_paid = NotiModel(
                "Saveat - 알림",
                "모든 참여자가 송금을 완료했어요",
                Calendar.getInstance().time,
                hostUid,
                roomTitle
            )
            val pushModel_pay = PushNotification(notiData_paid, "${Receipt_RVAdapter.host_token}")
            testPush(pushModel_pay)
        }
    }.addOnFailureListener {
        Log.d("실패", "실패")
    }

}

private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    Log.d("pushNoti", notification.toString())
    RetrofitInstance.api.postNotification(notification)
}

package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatList_RVAdapter(
    val items: MutableList<ChatRoomData>,
    val context: Context,
    val chatroomkeyList: MutableList<String>
) :
    RecyclerView.Adapter<ChatList_RVAdapter.ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isCheckBtn_Show = false

    companion object {
        val checked_chatRoomKey_List = mutableListOf<String>()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position], chatroomkeyList[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ChatRoomData, chatroomkey: String) {
            val title = itemView.findViewById<TextView>(R.id.chat_list_rv_title_textView)
            title.setText(item.title)
            val checkBtn = itemView.findViewById<CheckBox>(R.id.checkBtn)

            //휴지통 버튼 눌렸으면 체크버튼 보이게
            if (isCheckBtn_Show == false) {
                checkBtn.isChecked = false
                checkBtn.visibility = View.GONE
                checked_chatRoomKey_List.clear()
                //휴지통 버튼 안눌렀을때만 채팅방 눌렀을때 채팅방에 들어갈수있도록함
                itemView.setOnClickListener {
                    val intent =
                        Intent(context, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                }
            } else {
                //휴지통 버튼 눌렀을때는 클릭해도 채팅방으로 넘어가는것을 막음
                itemView.setOnClickListener {
                    checkBtn.isChecked = !checkBtn.isChecked
                    Log.d("리스트", checked_chatRoomKey_List.toString())
                }
                checkBtn.visibility = View.VISIBLE
                checkBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        //체크된 채팅방키를 list에 저장
                        checked_chatRoomKey_List.add(chatroomkey)
                    } else {
                        //체크해제되면 해당 채팅방키를 체크 list에서 삭제
                        checked_chatRoomKey_List.remove(chatroomkey)
                    }

                    Log.d("체크됨", checked_chatRoomKey_List.toString())
                }
            }
            getRecentMessage(chatroomkey, itemView)
            getUserNum(chatroomkey, itemView)
        }


    }

    fun updateCheckBox(show: Boolean) {
        isCheckBtn_Show = show
    }


    fun getRecentMessage(chatroomkey: String, itemView: View) {
        val query = FBRef.chatRoomsRef.child(chatroomkey).child("messages").limitToLast(1)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val chatData = data.getValue<ChatData>()
                    //최근 메시지 띄우기
                    itemView.findViewById<TextView>(R.id.tv_msg).text = chatData!!.msg
                    //저장된 시간의 오전 오후 정보 추출
                    val ampmCheck = SimpleDateFormat("aa")
                    val ampm = ampmCheck.format(chatData.time)
                    //저장된 시간을 "hh:mm" 형식으로 표시
                    val dateFormat = SimpleDateFormat("hh:mm")
                    val time = dateFormat.format(chatData.time)
                    val rv_msgtime = itemView.findViewById<TextView>(R.id.tv_time)
                    if (ampm.toString() == "AM") {
                        rv_msgtime.text = "오전 " + time.toString()
                    } else {
                        rv_msgtime.text = "오후 " + time.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun getUserNum(chatroomkey: String, itemView: View) {
        FBRef.chatRoomsRef.child(chatroomkey).child("users")
            .addValueEventListener(object : ValueEventListener {
                var num = 0
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        num++
                    }
                    itemView.findViewById<TextView>(R.id.tv_userNum).text = num.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}

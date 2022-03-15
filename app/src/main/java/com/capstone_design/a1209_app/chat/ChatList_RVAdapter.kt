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
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ChatList_RVAdapter(
    val items: MutableList<ChatRoomData>,
    val context: Context,
    val chatroomkeyList: MutableList<String>
) :
    RecyclerView.Adapter<ChatList_RVAdapter.ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var ck = 0

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
            if (ck == 0) {
                checkBtn.visibility = View.GONE
            } else {
                checkBtn.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                val intent =
                    Intent(context, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
            }
        }


    }

    fun updateCheckBox(n: Int) {
        ck = n
    }

    fun exit(chatroomkey: String) {
        //chatRooms에서 사용자 삭제
        FBRef.chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
            .removeValue()

        //UserRooms에서 채팅방 키 삭제(결과: 해당 유저의 화면에서 안보이게됨)
        FBRef.userRoomsRef.child(Auth.current_uid).child(chatroomkey!!).removeValue()
    }
}

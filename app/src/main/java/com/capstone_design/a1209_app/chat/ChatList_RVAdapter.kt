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
            if (ck == 0) {
                checkBtn.visibility = View.GONE
                checked_chatRoomKey_List.clear()
            } else {
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
}

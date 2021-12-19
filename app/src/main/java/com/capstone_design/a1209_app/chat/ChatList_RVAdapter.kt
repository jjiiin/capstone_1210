package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ChatList_RVAdapter (
    val items: MutableList<ChatRoomData>,
    val context: Context,
    val chatroomkeys: MutableList<String>,
    val isExitBtnClick: Int
):
    RecyclerView.Adapter<ChatList_RVAdapter.ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position], chatroomkeys[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ChatRoomData, chatroomkey: String) {
            val title = itemView.findViewById<TextView>(R.id.chat_list_rv_title_textView)
            title.setText(item.title)
            //Log.d("선택", isExitBtnClick.toString())
           /* if (isExitBtnClick == 0) {
                itemView.findViewById<RadioButton>(R.id.radioBtn).visibility = View.VISIBLE
            } else {
                itemView.findViewById<RadioButton>(R.id.radioBtn).visibility = View.GONE
            }*/
            itemView.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }
}
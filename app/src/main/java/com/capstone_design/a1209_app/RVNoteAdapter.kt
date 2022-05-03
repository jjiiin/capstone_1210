package com.capstone_design.a1209_app

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.chat.ChatList_RVAdapter
import com.capstone_design.a1209_app.chat.ChatRoomActivity
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.fcm.NotiModel

class RVNoteAdapter(val items: MutableList<notiData>, val context: Context, val keys:MutableList<String>) :
    RecyclerView.Adapter<RVNoteAdapter.ViewHolder>() {
    private var isCheckBtn_Show = false
    companion object {
        val checked_noti_List = mutableListOf<String>()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listview_noti, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.content.text = item.content
        holder.date.text = item.date
        val key = keys[position]
        //image
        when (item.category) {
            "asian" -> holder.img.setImageResource(R.drawable.asian)
            "bun" -> holder.img.setImageResource(R.drawable.bun)
            "bento" -> holder.img.setImageResource(R.drawable.bento)
            "chicken" -> holder.img.setImageResource(R.drawable.chicken)
            "pizza" -> holder.img.setImageResource(R.drawable.pizza)
            "fastfood" -> holder.img.setImageResource(R.drawable.fastfood)
            "japan" -> holder.img.setImageResource(R.drawable.japan)
            "korean" -> holder.img.setImageResource(R.drawable.korean)
            "cafe" -> holder.img.setImageResource(R.drawable.cafe)
            "chi" -> holder.img.setImageResource(R.drawable.china)
            "full" -> holder.img.setImageResource(R.drawable.notification_chatroom_full)
            "enter" -> holder.img.setImageResource(R.drawable.notification_enter)
            "pay" -> holder.img.setImageResource(R.drawable.notification_paid)
            "receipt" -> holder.img.setImageResource(R.drawable.notification_receipt)
            "evaluation" -> {
                holder.img.setImageResource(R.drawable.notification_evaluation)

            }
        }
        if (item.chatroom_title != "") {
            holder.chatroom_title.text = item.chatroom_title
        }
        holder.bind(item, key)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateCheckBox(show: Boolean) {
        isCheckBtn_Show = show
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.notiContent)
        val date: TextView = itemView.findViewById(R.id.notiDate)
        val img: ImageView = itemView.findViewById(R.id.item_image)
        val chatroom_title = itemView.findViewById<TextView>(R.id.noti_chatroom_title)

        fun bind(item: notiData, key:String) {
            if (item.category == "evaluation") {
                //신뢰도 알림 클릭하면 신뢰도 평가 화면으로 이동
                itemView.setOnClickListener {
                    val intent = Intent(context, Push_Evaluation_Activity::class.java)
                    intent.putExtra("chatroomkey", item.chatroom_key)
                    context.startActivity(intent)
                }
            }
            //휴지통 버튼 눌렸으면 체크버튼 보이게
            if (isCheckBtn_Show == false) {
                itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked = false
                itemView.findViewById<CheckBox>(R.id.checkBtn).visibility = View.GONE
                checked_noti_List.clear()
                //휴지통 버튼 안눌렀을때는 알림 클릭하면 신뢰도 화면 이동
                itemView.setOnClickListener {
                    if(item.category == "evaluation"){
                        val intent = Intent(context, Push_Evaluation_Activity::class.java)
                        intent.putExtra("chatroomkey", item.chatroom_key)
                        context.startActivity(intent)
                    }

                }
            } else {
                //휴지통 버튼 눌렀을때는 알림 클릭하면 체크버튼 클릭됨
                itemView.setOnClickListener {
                    itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked = !itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked
                }
                itemView.findViewById<CheckBox>(R.id.checkBtn).visibility = View.VISIBLE
                itemView.findViewById<CheckBox>(R.id.checkBtn).setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        //체크된 알림의 키를 list에 저장
                        checked_noti_List.add(key)
                    } else {
                        //체크해제되면 해당 채팅방키를 체크 list에서 삭제
                        checked_noti_List.remove(key)
                    }

                    Log.d("체크됨", checked_noti_List.toString())
                }
            }
        }
    }
}
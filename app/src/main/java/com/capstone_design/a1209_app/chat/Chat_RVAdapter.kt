package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.Evaluation_Display_Activity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.AccountChatData
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.utils.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class Chat_RVAdapter(val items: MutableList<Any>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    val RIGHT_TALK = 0
    val LEFT_TALK = 1
    val RIGHT_ACCOUNT_TALK = 2
    val LEFT_ACCOUNT_TALK = 3
    val ENTER = 4
    val NOTICE = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //뷰홀더를 생성(레이아웃 생성)하는 코드 작성
        return when (viewType) {
            RIGHT_TALK -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.chat_rv_item_right, parent, false)
                RightViewHolder(view)
            }
            LEFT_TALK -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.chat_rv_item_left, parent, false)
                LeftViewHolder(view)
            }
            RIGHT_ACCOUNT_TALK -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_rv_item_account_right, parent, false)
                RightAccountiewHolder(view)
            }
            LEFT_ACCOUNT_TALK -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_rv_item_account_left, parent, false)
                LeftAccountViewHolder(view)
            }
            ENTER -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_rv_item_enter_message, parent, false)
                EnterViewHolder(view)
            }
            NOTICE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_rv_item_notice, parent, false)
                NoticeViewHolder(view)
            }
            else -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.chat_rv_item_left, parent, false)
                LeftViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //뷰홀더가 재활용될때 실행되는 메소드 작성
        if (holder is LeftViewHolder) {
            holder.bindItems(items[position])
        } else if (holder is RightViewHolder) {
            holder.bindItems(items[position])
        } else if (holder is LeftAccountViewHolder) {
            holder.bindItems(items[position])
        } else if (holder is RightAccountiewHolder) {
            holder.bindItems(items[position])
        } else if (holder is EnterViewHolder) {
            holder.bindItems(items[position])
        } else if (holder is NoticeViewHolder) {
            holder.bindItems(items[position])
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        auth = Firebase.auth
        val current_uid = auth.currentUser!!.uid
        //items 배열에는 ChatData와 AccountChatData가 들어있기 때문에 타입 체크 해줌
        if (items.get(position).javaClass.name.toString() == "com.capstone_design.a1209_app.dataModels.AccountChatData") {
            val data = items.get(position) as AccountChatData
            if (data.uid.equals(current_uid)) {
                return RIGHT_ACCOUNT_TALK
            } else {
                return LEFT_ACCOUNT_TALK
            }
        } else if (items.get(position).javaClass.name.toString() == "com.capstone_design.a1209_app.dataModels.ChatData") {
            val data = items.get(position) as ChatData
            if (data.uid.equals(current_uid)) {
                if (data.msg == "enter") {
                    return ENTER
                } else if (data.uid == "notice") {
                    return NOTICE
                } else {
                    //내 채팅인 경우 0
                    return RIGHT_TALK
                }
            } else {
                if (data.msg == "enter") {
                    return ENTER
                } else if (data.uid == "notice") {
                    return NOTICE
                } else {
                    //다른 사람 채팅인 경우 1
                    return LEFT_TALK
                }
            }
        } else return RIGHT_TALK
    }

    inner class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {
            val item = item as ChatData
            getImage(itemView, item.uid)
            val rv_nickname = itemView.findViewById<TextView>(R.id.rv_nickname_textView)
            rv_nickname.text = item.nickname
            val rv_msg = itemView.findViewById<TextView>(R.id.rv_msg_textView)
            rv_msg.text = item.msg

            //저장된 시간의 오전 오후 정보 추출
            val ampmCheck = SimpleDateFormat("aa")
            val ampm = ampmCheck.format(item.time)
            //저장된 시간을 "hh:mm" 형식으로 표시
            val dateFormat = SimpleDateFormat("hh:mm")
            val time = dateFormat.format(item.time)
            val rv_msgtime = itemView.findViewById<TextView>(R.id.rv_msg_time)
            if (ampm.toString() == "AM") {
                rv_msgtime.text = "오전 " + time.toString()
            } else {
                rv_msgtime.text = "오후 " + time.toString()
            }

            itemView.findViewById<ImageView>(R.id.rv_profile_btn).setOnClickListener {
                val intent =
                    Intent(context, Evaluation_Display_Activity::class.java).putExtra(
                        "uid",
                        item.uid
                    )
                        .putExtra("nickname", item.nickname)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }

            //val rv_profile_btn = itemView.findViewById<Button>(R.id.rv_profile_btn)
            /* rv_profile_btn.setOnClickListener {
                 Intent(context, ProfileActivity::class.java).apply {
                     putExtra("이메일", item.email)
                     putExtra("닉네임", item.nickname)
                 }.run { context.startActivity(this) }
             }*/

        }
    }

    inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {

            val item = item as ChatData
            val rv_msg = itemView.findViewById<TextView>(R.id.rv_msg_textView)
            rv_msg.text = item.msg

            //저장된 시간의 오전 오후 정보 추출
            val ampmCheck = SimpleDateFormat("aa")
            val ampm = ampmCheck.format(item.time)
            //저장된 시간을 "hh:mm" 형식으로 표시
            val dateFormat = SimpleDateFormat("hh:mm")
            val time = dateFormat.format(item.time)
            val rv_msgtime = itemView.findViewById<TextView>(R.id.rv_msg_time)
            if (ampm.toString() == "AM") {
                rv_msgtime.text = "오전 " + time.toString()
            } else {
                rv_msgtime.text = "오후 " + time.toString()
            }
        }
    }

    inner class RightAccountiewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {
            val item = item as AccountChatData
            //저장된 시간의 오전 오후 정보 추출
            val ampmCheck = SimpleDateFormat("aa")
            val ampm = ampmCheck.format(item.time)
            //저장된 시간을 "hh:mm" 형식으로 표시
            val dateFormat = SimpleDateFormat("hh:mm")
            val time = dateFormat.format(item.time)
            val rv_msgtime = itemView.findViewById<TextView>(R.id.rv_msg_time)

            itemView.findViewById<TextView>(R.id.tv_bank_name).text = item.bankName
            itemView.findViewById<TextView>(R.id.tv_receiver_name).text = item.receiverName
            itemView.findViewById<TextView>(R.id.tv_account_num).text = item.accountNum

            if (ampm.toString() == "AM") {
                rv_msgtime.text = "오전 " + time.toString()
            } else {
                rv_msgtime.text = "오후 " + time.toString()
            }
        }
    }

    inner class LeftAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {
            val item = item as AccountChatData
            getImage(itemView, item.uid)
            val rv_nickname = itemView.findViewById<TextView>(R.id.rv_nickname_textView)
            rv_nickname.text = item.nickname
            //저장된 시간의 오전 오후 정보 추출
            val ampmCheck = SimpleDateFormat("aa")
            val ampm = ampmCheck.format(item.time)
            //저장된 시간을 "hh:mm" 형식으로 표시
            val dateFormat = SimpleDateFormat("hh:mm")
            val time = dateFormat.format(item.time)
            val rv_msgtime = itemView.findViewById<TextView>(R.id.rv_msg_time)

            itemView.findViewById<TextView>(R.id.tv_bank_name).text = item.bankName
            itemView.findViewById<TextView>(R.id.tv_receiver_name).text = item.receiverName
            itemView.findViewById<TextView>(R.id.tv_account_num).text = item.accountNum

            if (ampm.toString() == "AM") {
                rv_msgtime.text = "오전 " + time.toString()
            } else {
                rv_msgtime.text = "오후 " + time.toString()
            }
            itemView.findViewById<ImageView>(R.id.rv_profile_btn).setOnClickListener {
                val intent =
                    Intent(context, Evaluation_Display_Activity::class.java).putExtra(
                        "uid",
                        item.uid
                    )
                        .putExtra("nickname", item.nickname)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    inner class EnterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {
            val item = item as ChatData
            val rv_nickname = itemView.findViewById<TextView>(R.id.tv_nickname)
            rv_nickname.text = item.nickname

        }
    }

    inner class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Any) {
            val item = item as ChatData
            val tv_notice = itemView.findViewById<TextView>(R.id.tv_notice)
            tv_notice.text = item.msg
        }
    }

    fun getImage(itemView: View, uid: String) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(context).load(it).into(itemView.findViewById(R.id.rv_profile_btn))
            }.addOnFailureListener {
                itemView.findViewById<ImageView>(R.id.rv_profile_btn)
                    .setImageResource(R.drawable.profile_cat)
            }
    }
}
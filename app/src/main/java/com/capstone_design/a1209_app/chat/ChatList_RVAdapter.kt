package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.Push_Evaluation_Activity
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    var isCheckBtn_Show = false

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
                    /* val intent = Intent(context, Push_Evaluation_Activity::class.java).putExtra("chatroomkey", chatroomkey)
                     context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))*/
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
                    val currentTime = Calendar.getInstance().time
                    val rv_msgtime = itemView.findViewById<TextView>(R.id.tv_time)
                    if ((currentTime.year - chatData.time.year) != 0) {
                        rv_msgtime.text =
                            (currentTime.year - chatData.time.year).toString() + "년 전"
                    } else if ((currentTime.month - chatData.time.month) != 0) {
                        rv_msgtime.text =
                            (currentTime.month - chatData.time.month).toString() + "달 전"
                    } else if ((currentTime.date - chatData.time.date) != 0) {
                        rv_msgtime.text =
                            (currentTime.date - chatData.time.date).toString() + "일 전"
                    } else if ((currentTime.hours - chatData.time.hours) != 0) {
                        rv_msgtime.text =
                            (currentTime.hours - chatData.time.hours).toString() + "시간 전"
                    } else if ((currentTime.minutes - chatData.time.minutes) != 0) {
                        rv_msgtime.text =
                            (currentTime.minutes - chatData.time.minutes).toString() + "분 전"
                    } else {
                        rv_msgtime.text =
                            (currentTime.seconds - chatData.time.seconds).toString() + "초 전"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun getUserNum(chatroomkey: String, itemView: View) {
        FBRef.chatRoomsRef.child(chatroomkey).child("users")
            .addValueEventListener(object : ValueEventListener {
                var num = 0
                var uids = mutableListOf<String>()
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        num++
                        uids.add(data.key.toString())
                    }
                    itemView.findViewById<TextView>(R.id.tv_userNum).text = num.toString()
                    //참가자 수에 따라서 채팅방 썸네일 다르게
                    val storage: FirebaseStorage = FirebaseStorage.getInstance()
                    val storageRef: StorageReference = storage.getReference()
                    when (num) {
                        1 -> {
                            itemView.findViewById<LinearLayout>(R.id.layout1).visibility =
                                View.VISIBLE
                            itemView.findViewById<LinearLayout>(R.id.layout2).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout3).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout4).visibility =
                                View.GONE
                            storageRef.child("profile_img/" + uids[0] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile1_layout1))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile1_layout1)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                        }
                        2 -> {
                            itemView.findViewById<LinearLayout>(R.id.layout2).visibility =
                                View.VISIBLE
                            itemView.findViewById<LinearLayout>(R.id.layout1).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout3).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout4).visibility =
                                View.GONE
                            storageRef.child("profile_img/" + uids[0] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile1_layout2))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile1_layout2)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[1] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile2_layout2))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile2_layout2)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                        }
                        3 -> {
                            itemView.findViewById<LinearLayout>(R.id.layout3).visibility =
                                View.VISIBLE
                            itemView.findViewById<LinearLayout>(R.id.layout1).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout2).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout4).visibility =
                                View.GONE
                            storageRef.child("profile_img/" + uids[0] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile1_layout3))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile1_layout3)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[1] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile2_layout3))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile2_layout3)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[2] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile3_layout3))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile3_layout3)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                        }
                        4 -> {
                            itemView.findViewById<LinearLayout>(R.id.layout4).visibility =
                                View.VISIBLE
                            itemView.findViewById<LinearLayout>(R.id.layout1).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout2).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout3).visibility =
                                View.GONE
                            storageRef.child("profile_img/" + uids[0] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile1_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile1_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[1] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile2_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile2_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[2] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile3_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile3_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[3] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile4_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile4_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                        }
                        else -> {
                            itemView.findViewById<LinearLayout>(R.id.layout4).visibility =
                                View.VISIBLE
                            itemView.findViewById<LinearLayout>(R.id.layout1).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout2).visibility =
                                View.GONE
                            itemView.findViewById<LinearLayout>(R.id.layout3).visibility =
                                View.GONE
                            storageRef.child("profile_img/" + uids[0] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile1_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile1_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[1] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile2_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile2_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[2] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile3_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile3_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                            storageRef.child("profile_img/" + uids[3] + ".jpg").getDownloadUrl()
                                .addOnSuccessListener {
                                    Glide.with(context).load(it)
                                        .into(itemView.findViewById(R.id.profile4_layout4))
                                }.addOnFailureListener {
                                    itemView.findViewById<ImageView>(R.id.profile4_layout4)
                                        .setImageResource(R.drawable.profile_cat)
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
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

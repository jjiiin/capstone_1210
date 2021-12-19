package com.capstone_design.a1209_app.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.chatRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.userRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.usersRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue

class ChatRoomActivity : AppCompatActivity() {

    //채팅에 닉네임 정보 저장하기 위한 변수
    lateinit var current_nickname: String
    var isReceiptDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatroom_include_drawer)

        var isPlusBtnClick = 0

        //채팅방 키 값 받아옴
        val intent = getIntent()
        val chatroomkey = intent.getStringExtra("채팅방키");

        //val return_intent = Intent(this, ChatFragment::class.java)

        //리사이클러뷰에 들어갈 아이템 추가
        val chats = mutableListOf<ChatData>()
        val roomusers = mutableListOf<String>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        val rvAdapter = Chat_RVAdapter(chats, this)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //채팅방 참여자 리사이클러뷰(드로어메뉴)
        val roomUser_rv = findViewById<RecyclerView>(R.id.rv_room_user_list)
        val roomuserRvadapter = RoomUser_RVAdapter(roomusers, this)
        roomUser_rv.adapter = roomuserRvadapter
        roomUser_rv.layoutManager = LinearLayoutManager(this)

        //채팅방 제목 가져오기
        getRoomTitle(chatroomkey!!)

        //채팅 불러오기
        getMessages(chatroomkey!!, chats, rvAdapter)

        //채팅방 참여자 업데이트 감지
        getUsers(chatroomkey!!, roomusers, roomuserRvadapter)

        //사용자가 영수증 작성했는지 확인
        getUserReceipt(chatroomkey!!)

        //버튼을 눌러 메뉴를 오픈할 수도 있고, 왼쪽에서 오른쪽으로 스왑해 오픈할 수 있습니다.
        //DrawerLayout의 id에 직접 openDrawer()메소드를 사용할 수 있습니다.
        findViewById<Button>(R.id.menu_btn).setOnClickListener {
            findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.END)
        }


        //채팅방 나가기
        findViewById<ImageView>(R.id.btn_exit_room).setOnClickListener {

            //chatRooms에서 사용자 삭제
            chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
                .removeValue()
            //UserRooms에서 채팅방 키 삭제하여 해당 유저의 화면에서 안보이게함
            userRoomsRef.child(Auth.current_uid).child(chatroomkey!!).removeValue()

            //startActivity(return_intent)
        }

        //영수증 버튼 누를때
        findViewById<Button>(R.id.receipt_btn).setOnClickListener {
            if (isReceiptDone == true) {
                val intent =
                    Intent(this, ReceiptDoneActivity::class.java)
                intent.putExtra("채팅방키", chatroomkey)
                startActivity(intent)
            } else {
                val intent =
                    Intent(this, ReceiptBeforeAvtivity::class.java)
                intent.putExtra("닉네임", current_nickname)
                intent.putExtra("채팅방키", chatroomkey)
                startActivity(intent)
            }

        }
        //플러스 버튼 눌렀을 때
        findViewById<ImageView>(R.id.plus_btn).setOnClickListener {
            if(isPlusBtnClick == 0){
                findViewById<LinearLayout>(R.id.plus_layout).visibility = View.VISIBLE
                isPlusBtnClick = 1
            } else{
                findViewById<LinearLayout>(R.id.plus_layout).visibility = View.GONE
                isPlusBtnClick = 0
            }

        }

        //내 계좌 버튼 눌렀을 때
        findViewById<LinearLayout>(R.id.account_layout).setOnClickListener {
            val current_time = System.currentTimeMillis()
            val chatData =
                ChatData(
                    current_nickname,
                    "account",
                    Auth.current_email!!,
                    Auth.current_uid,
                    current_time
                )
            chatRoomsRef.child(chatroomkey!!).child("messages").push().setValue(chatData)
        }

        //채팅 보낼시 이벤트
        val sendBtn = findViewById<Button>(R.id.btn_chat_send)
        sendBtn.setOnClickListener {
            val message = findViewById<EditText>(R.id.editText_chat_msg)
            val current_time = System.currentTimeMillis()
            //Log.d("시간",date.toString())
            val chatData =
                ChatData(
                    current_nickname,
                    message.text.toString(),
                    Auth.current_email!!,
                    Auth.current_uid,
                    current_time
                )
            //myRef.push().setValue(chatData)
            chatRoomsRef.child(chatroomkey!!).child("messages").push().setValue(chatData)
            message.setText("")


            //키보드 내리기
            val manager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //채팅 불러오기
    fun getMessages(
        chatroomkey: String,
        chats: MutableList<ChatData>,
        rvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        //채팅 보낼때 감지
        chatRoomsRef.child(chatroomkey!!).child("messages").addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Log.d("데이터", "추가됨")
                    val saved_email = snapshot.getValue<ChatData>()!!.email
                    val saved_uid = snapshot.getValue<ChatData>()!!.uid
                    //내 닉네임은 "나", 상대방 닉네임은 이메일
                    if (saved_uid == Auth.current_uid) {
                        chats.add(
                            ChatData(
                                "나",
                                snapshot.getValue<ChatData>()!!.msg,
                                saved_email, saved_uid, snapshot.getValue<ChatData>()!!.time
                            )
                        )
                        //Log.d("데이터", items.toString())
                    } else {
                        chats.add(
                            ChatData(
                                saved_email,
                                snapshot.getValue<ChatData>()!!.msg,
                                saved_email, saved_uid, snapshot.getValue<ChatData>()!!.time
                            )
                        )
                    }
                    //items에 변화가 생기면 반영
                    rvAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("삭제", "삭제됨")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    //채팅방 참여자 업데이트 감지
    fun getUsers(
        chatroomkey: String,
        roomusers: MutableList<String>,
        roomuserRvadapter: RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>
    ) {
        chatRoomsRef.child(chatroomkey!!).child("users")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    //Log.d("추가됨", snapshot.key.toString())

                    //참여자의 회원정보 가져오기
                    val userQuery =
                        usersRef.orderByKey().equalTo(snapshot.key.toString())
                    userQuery.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            if (snapshot.key == Auth.current_uid) {
                                current_nickname = snapshot.getValue<UserData>()!!.nickname
                                //Log.d("ㅎㅎ", current_nickname)
                            }
                            val nickname = snapshot.getValue<UserData>()!!.nickname
                            roomusers.add(nickname)
                            Log.d("추가됨_닉네임", nickname)
                            roomuserRvadapter.notifyDataSetChanged()
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    //roomusers.add("사용자이름")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("업데이트됨", snapshot.toString())
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //채팅방에 유저 아무도 없으면 파이어베이스에서 채팅방 데이터 삭제
                    chatRoomsRef.child(chatroomkey!!).child("users").get().addOnSuccessListener {
                        if (it.value == null) {
                            chatRoomsRef.child(chatroomkey!!).removeValue()
                            Log.d("ㅎㅎ", it.value.toString())
                        }
                    }
                    finish()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("업데이트됨", snapshot.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("업데이트됨", error.toString())
                }

            })
    }

    //채팅방 제목 가져오기
    fun getRoomTitle(chatroomkey: String) {
        //데이터 한번만 가져올때는 get()함수 사용
        chatRoomsRef.child(chatroomkey!!).get().addOnSuccessListener {
            //Log.d("뭘까", it.child("title").value.toString())
            val title = it.child("title").value.toString()
            findViewById<TextView>(R.id.tv_chatroom_title).setText(title)
        }

    }

    fun getUserReceipt(chatroomkey: String) {
        FBRef.chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key.toString() == Auth.current_uid) {
                        isReceiptDone = true
                    }
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
                    isReceiptDone = false
                }

            })
    }
}
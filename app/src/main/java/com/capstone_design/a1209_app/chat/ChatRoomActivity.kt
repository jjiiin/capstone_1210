package com.capstone_design.a1209_app.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatRoomActivity : AppCompatActivity() {

    //채팅에 닉네임 정보 저장하기 위한 변수
    lateinit var current_nickname: String
    private var isReceiptDone = false

    //채팅방에 있는 유저들의 아이디 저장된 리스트
    val roomusersIdList = mutableListOf<String>()
    val roomusersList = mutableListOf<UserData>()

    //user 레퍼런스에서 데이터 읽어올때 그 user의 키가 저장된 리스트(RoomUser_RVAdapter로 데이터 넘길때 user의 uid 값도 같이 넘겨주기위함)
    val usersIdList = mutableListOf<String>()

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

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        val rvAdapter = Chat_RVAdapter(chats, this)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //채팅방 참여자 리사이클러뷰(드로어메뉴)
        val roomUser_rv = findViewById<RecyclerView>(R.id.rv_room_user_list)
        val roomuserRvadapter = RoomUser_RVAdapter(roomusersList, this, usersIdList)
        roomUser_rv.adapter = roomuserRvadapter
        roomUser_rv.layoutManager = LinearLayoutManager(this)

        //채팅방 제목 가져오기
        getRoomTitle(chatroomkey!!)

        //채팅 불러오기
        getMessages(chatroomkey!!, chats, rvAdapter)

        //채팅방 참여자 업데이트 감지
        chatRoomsRef.child(chatroomkey!!).child("users")
            .addValueEventListener(
                getRoomUserEventListener(
                    chatroomkey,
                    roomuserRvadapter
                )
            )

        //사용자가 영수증 작성했는지 확인
        getUserReceipt(chatroomkey!!)

        //버튼을 눌러 메뉴를 오픈할 수도 있고, 왼쪽에서 오른쪽으로 스왑해 오픈할 수 있습니다.
        //DrawerLayout의 id에 직접 openDrawer()메소드를 사용할 수 있습니다.
        findViewById<Button>(R.id.menu_btn).setOnClickListener {
            findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.END)
        }


        //채팅방 나가기
        findViewById<ConstraintLayout>(R.id.exit_room_layout).setOnClickListener {

            //chatRooms에서 사용자 삭제
            chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
                .removeValue()

            //UserRooms에서 채팅방 키 삭제(결과: 해당 유저의 화면에서 안보이게됨)
            userRoomsRef.child(Auth.current_uid).child(chatroomkey!!).removeValue()

            //startActivity(return_intent)
        }

        //영수증 버튼 누를때
        findViewById<Button>(R.id.receipt_btn).setOnClickListener {
            if (isReceiptDone == true) {
                val intent =
                    Intent(this, ReceiptDoneActivity::class.java)
                intent.putExtra("채팅방키", chatroomkey)
                intent.putExtra("닉네임", current_nickname)
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
            if (isPlusBtnClick == 0) {
                findViewById<LinearLayout>(R.id.plus_layout).visibility = View.VISIBLE
                isPlusBtnClick = 1
            } else {
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
                    val saved_nickname = snapshot.getValue<ChatData>()!!.nickname
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
                                saved_nickname,
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
        chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key.toString() == Auth.current_uid) {
                        isReceiptDone = true
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key.toString() == Auth.current_uid) {
                        isReceiptDone = true
                    }
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

    //1. 채팅방에 누가 있는지 불러온다.(list에 저장)
    //2. user 레퍼런스 for문 돌면서 만약 채팅방 참여자 list에 해당 user 있으면 닉네임 등등 저장한다.

    fun getRoomUserEventListener(
        chatroomkey: String,
        roomuserRvadapter: RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>
    ): ValueEventListener {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomusersIdList.clear()
                if (snapshot.value == null) {
                    chatRoomsRef.child(chatroomkey!!).removeValue()
                    finish()
                } else {
                    for (data in snapshot.children) {
                        roomusersIdList.add(data.key.toString())
                    }
                    usersRef.addValueEventListener(getUserInfoListener(roomuserRvadapter))
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        return eventListener
    }



    fun getUserInfoListener(roomuserRvadapter: RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>): ValueEventListener {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomusersList.clear()
                for (data in snapshot.children) {
                    if (roomusersIdList.contains(data.key)) {
                        val user = data.getValue(UserData::class.java)
                        roomusersList.add(user!!)
                        usersIdList.add(data.key!!)
                        if (Auth.current_uid == data.key) {
                            current_nickname = user.nickname
                        }
                    }

                }
                roomuserRvadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        return eventListener
    }
}
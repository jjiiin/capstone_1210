package com.capstone_design.a1209_app.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.Mypage_Account_Activity
import com.capstone_design.a1209_app.Push_Evaluation_Activity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.AccountChatData
import com.capstone_design.a1209_app.dataModels.AccountData
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.board
import com.capstone_design.a1209_app.utils.FBRef.Companion.chatRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.userRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.usersRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRoomActivity : AppCompatActivity() {

    //채팅에 닉네임 정보 저장하기 위한 변수
    lateinit var current_nickname: String
    private var isReceiptDone = false

    //채팅방에 있는 유저들의 아이디 저장된 리스트
    val roomusersIdList = mutableListOf<String>()
    val roomusersList = mutableListOf<UserData>()

    //리사이클러뷰에 들어갈 아이템 추가
    val chats = mutableListOf<Any>()

    //user 레퍼런스에서 데이터 읽어올때 그 user의 키가 저장된 리스트(RoomUser_RVAdapter로 데이터 넘길때 user의 uid 값도 같이 넘겨주기위함)
    val usersIdList = mutableListOf<String>()
    var boardKey: String = ""
    var isPlusBtnClick = 0
    var userNum = 0
    var num_maximumNum = 0
    var is_down_arrow_clicked = 0
    var roomTitle = ""
    var chatroomkey = ""
    var isClosed = false
    lateinit var rv:RecyclerView

    //프로필 사진 요청 코드
    private val DEFAULT_GALLERY_REQUEST_CODE = 0

    companion object{
        var hostUid = ""
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatroom_include_drawer)


        //채팅방 키 값 받아옴
        val intent = getIntent()
        chatroomkey = intent.getStringExtra("채팅방키").toString()

        //val return_intent = Intent(this, ChatFragment::class.java)


        //리사이클러뷰 어댑터 연결
        rv = findViewById<RecyclerView>(R.id.recycler_view)
        val rvAdapter = Chat_RVAdapter(chats, this, chatroomkey)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)
        //자동 스크롤(아이템 10개 가정)
        //rv.smoothScrollToPosition(10)

        val roomUser_rv = findViewById<RecyclerView>(R.id.rv_room_user_list)
        val roomuserRvadapter = RoomUser_RVAdapter(roomusersList, this, usersIdList)
        roomUser_rv.adapter = roomuserRvadapter
        roomUser_rv.layoutManager = LinearLayoutManager(this)

        //채팅방 제목 가져오기
        getRoomTitle(chatroomkey!!)

        //채팅 불러오기
        getMessages(chatroomkey!!, chats, rvAdapter)

        //방장 uid 가져오기
        getHostUid(chatroomkey)

        //채팅방 참여자 업데이트 감지
        getRoomUser(chatroomkey, roomuserRvadapter)

        //사용자가 영수증 작성했는지 확인
        getReceiptDone(chatroomkey!!)

        //마감된 채팅방일때 작업
        isClosed(chatroomkey, this)

        //게시글 key 가져오기
        getBoardKey(chatroomkey)


        //버튼을 눌러 메뉴를 오픈할 수도 있고, 왼쪽에서 오른쪽으로 스왑해 오픈할 수 있습니다.
        //DrawerLayout의 id에 직접 openDrawer()메소드를 사용할 수 있습니다.
        findViewById<Button>(R.id.menu_btn).setOnClickListener {
            findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.END)
        }

        findViewById<ImageView>(R.id.backbtn).setOnClickListener {
            onBackPressed()
        }

        //채팅방 나가기
        findViewById<ConstraintLayout>(R.id.exit_room_layout).setOnClickListener {
            //삭제 경고 팝업띄우기
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.findViewById<TextView>(R.id.tv_content).text =
                "삭제된 채팅방은 내용을 복구할 수 없어요.\n선택한 채팅방에서 나가시겠어요?"
            dialog.show()

            val okButton = dialog.findViewById<Button>(R.id.btn_yes)

            okButton.setOnClickListener {
                //chatRooms에서 사용자 삭제
                chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
                    .removeValue()

                //UserRooms에서 채팅방 키 삭제(결과: 해당 유저의 화면에서 안보이게됨)
                userRoomsRef.child(Auth.current_uid).child(chatroomkey!!).removeValue()
                //주문서 삭제
                chatRoomsRef.child(chatroomkey!!).child("receipts").child(Auth.current_uid)
                    .removeValue()
                //팝업창 없앰
                dialog.dismiss()
                finish()
            }

            val noButton = dialog.findViewById<Button>(R.id.btn_no)

            noButton.setOnClickListener {
                //팝업창 없앰
                dialog.dismiss()
            }


            //startActivity(return_intent)
        }

        //영수증 버튼 누를때
        findViewById<Button>(R.id.receipt_btn).setOnClickListener {
            if (isReceiptDone == true) {
                val intent =
                    Intent(this, ReceiptDoneActivity::class.java)
                intent.putExtra("채팅방키", chatroomkey)
                intent.putExtra("닉네임", current_nickname)
                intent.putExtra("hostUid", hostUid)
                intent.putExtra("roomTitle", roomTitle)
                startActivity(intent)
            } else {
                val intent =
                    Intent(this, ReceiptBeforeAvtivity::class.java)
                intent.putExtra("닉네임", current_nickname)
                intent.putExtra("채팅방키", chatroomkey)
                intent.putExtra("hostUid", hostUid)
                intent.putExtra("roomTitle", roomTitle)
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

        //갤러리 버튼 눌렀을 때
        findViewById<LinearLayout>(R.id.gallery_layout).setOnClickListener {
            //갤러리에서 사진 가져오기
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/")
            startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
        }

        //내 계좌 버튼 눌렀을 때(계좌 메시지, 공지 보내기)
        findViewById<LinearLayout>(R.id.account_layout).setOnClickListener {
            usersRef.child(Auth.current_uid).child("account").get().addOnSuccessListener {
                if (it.getValue() == null) {    //등록된 계좌 없으면
                    //계좌 등록 팝업띄우기
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)    //둥근 테두리 적용되려면 써줘야됨(setContentView 전에 호출해야됨)
                    dialog.setContentView(R.layout.custom_dialog)
                    dialog.window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))     //둥근 테두리 적용되려면 써줘야됨
                    dialog.setCanceledOnTouchOutside(true)      //외부영역 클릭시 팝업창 없앰
                    dialog.setCancelable(true)      //back 버튼 클릭시 팝업창 없앰
                    dialog.findViewById<TextView>(R.id.tv_content).text =
                        "등록된 계좌가 없어요.\n마이페이지에서 계좌등록을 하시겠어요?"
                    dialog.show()

                    val okButton = dialog.findViewById<Button>(R.id.btn_yes)
                    okButton.setOnClickListener {
                        //마이페이지 계좌설정 화면 이동
                        val intent = Intent(this, Mypage_Account_Activity::class.java)
                        startActivity(intent)
                        //팝업창 없앰
                        dialog.dismiss()
                    }
                    val noButton = dialog.findViewById<Button>(R.id.btn_no)
                    noButton.setOnClickListener {
                        //팝업창 없앰
                        dialog.dismiss()
                    }
                } else {    //등록된 계좌 있으면
                    val data = it.getValue(AccountData::class.java)
                    val time = Calendar.getInstance().time
                    //val current_time = System.currentTimeMillis()
                    val chatData1 =
                        AccountChatData(
                            current_nickname,
                            "(account)",
                            Auth.current_email!!,
                            Auth.current_uid,
                            time,
                            data!!.bankName,
                            data.receiverName,
                            data.accountNum
                        )
                    val chatData2 = ChatData(
                        "notice",
                        "[공지] 송금 후, 주문서에서\n" +
                                "'송금완료 버튼'을 눌러주세요 :)",
                        "notice",
                        "notice",
                        time
                    )
                    chatRoomsRef.child(chatroomkey!!).child("messages").push().setValue(chatData1)
                    chatRoomsRef.child(chatroomkey!!).child("messages").push().setValue(chatData2)

                    //실행 delay 시키기
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        push_evaluation(chatroomkey)
                    }, 3000)

                }

            }

        }

        //모집인원 누르면 마감버튼 나오게
        findViewById<LinearLayout>(R.id.layout_usernum).setOnClickListener {
            if (is_down_arrow_clicked == 0) {
                findViewById<ImageView>(R.id.img_chatroom_close).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.layout_chatroom_close).visibility = View.VISIBLE
                is_down_arrow_clicked = 1
            } else {
                findViewById<ImageView>(R.id.img_chatroom_close).visibility = View.GONE
                findViewById<LinearLayout>(R.id.layout_chatroom_close).visibility = View.GONE
                is_down_arrow_clicked = 0
            }
        }

        //마감버튼 눌렀을때
        findViewById<LinearLayout>(R.id.layout_chatroom_close).setOnClickListener {
            if (isClosed) { //마감 상태이면 마감 해체하기
                chatRoomsRef.child(chatroomkey).child("isClosed").setValue(false)
            } else if (!isClosed) { //마감 상태 아니면 마감 처리하기
                chatRoomsRef.child(chatroomkey).child("isClosed").setValue(true)
            }

        }

        //채팅 보낼시 이벤트
        val sendBtn = findViewById<Button>(R.id.btn_chat_send)
        sendBtn.setOnClickListener {
            val message = findViewById<EditText>(R.id.editText_chat_msg)
            val time = Calendar.getInstance().time
            //val current_time = System.currentTimeMillis()
            //Log.d("시간",date.toString())
            val chatData =
                ChatData(
                    current_nickname,
                    message.text.toString(),
                    Auth.current_email!!,
                    Auth.current_uid,
                    time
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
        chats: MutableList<Any>,
        rvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        chatRoomsRef.child(chatroomkey!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chats.clear()
                    for (snapshot in snapshot.children) {
                        // Log.d("데이터", "추가됨")
                        val data = snapshot.getValue(ChatData::class.java)
                        if (data!!.msg == "(account)") {
                            //계좌 메시지라면
                            val data = snapshot.getValue(AccountChatData::class.java)
                            chats.add(data!!)
                        } else {
                            //일반 채팅 메시지라면
                            val saved_email = data.email
                            val saved_nickname = data.nickname
                            val saved_uid = data.uid
                            val saved_msg = data.msg
                            //내 닉네임은 "나", 상대방 닉네임은 이메일
                            if (saved_uid == Auth.current_uid && saved_msg != "enter") {
                                chats.add(
                                    ChatData(
                                        "나",
                                        snapshot.getValue<ChatData>()!!.msg,
                                        saved_email, saved_uid, snapshot.getValue<ChatData>()!!.time
                                    )
                                )
                            } else {
                                chats.add(
                                    ChatData(
                                        saved_nickname,
                                        snapshot.getValue<ChatData>()!!.msg,
                                        saved_email, saved_uid, snapshot.getValue<ChatData>()!!.time
                                    )
                                )
                            }
                        }

                    }
                    //자동 스크롤
                    rv.smoothScrollToPosition(chats.size-1)

                    //items에 변화가 생기면 반영
                    rvAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    //채팅방 제목 가져오기
    fun getRoomTitle(chatroomkey: String) {
        //데이터 한번만 가져올때는 get()함수 사용
        chatRoomsRef.child(chatroomkey!!).get().addOnSuccessListener {
            roomTitle = it.child("title").value.toString()
            findViewById<TextView>(R.id.tv_chatroom_title).setText(roomTitle)
        }

    }

    fun getReceiptDone(chatroomkey: String) {
        chatRoomsRef.child(chatroomkey!!).child("receipts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snapshot in snapshot.children) {
                        if (snapshot.key.toString() == Auth.current_uid) {
                            isReceiptDone = true
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isReceiptDone = false
                }

            })
    }

    fun getRoomUser(
        chatroomkey: String,
        roomuserRvadapter: RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>
    ) {
        chatRoomsRef.child(chatroomkey!!).child("users")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        roomusersIdList.clear()
                        userNum = 0
                        //채팅방에 사용자 아무도 없으면 해당 채팅방 삭제하는 코드
                        /*  if (snapshot.value == null) {
                              chatRoomsRef.child(chatroomkey!!).removeValue()
                              finish()
                          } else {*/
                        for (data in snapshot.children) {
                            roomusersIdList.add(data.key.toString())
                            userNum++
                        }
                        findViewById<TextView>(R.id.tv_usernum).setText(userNum.toString())
                        getUserInfo(roomuserRvadapter)
                        /*}*/

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )
    }

    fun getUserInfo(roomuserRvadapter: RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>) {
        usersRef.addValueEventListener(object : ValueEventListener {
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
            }
        })
    }

    fun getBoardKey(chatroomkey: String) {
        chatRoomsRef.child(chatroomkey!!).child("boardKey").get().addOnSuccessListener {
            boardKey = it.getValue().toString()
            getMaximumUserNum(boardKey, chatroomkey)
        }
    }

    fun getMaximumUserNum(boardKey: String, chatroomkey: String) {
        board.child(boardKey).child("person").get().addOnSuccessListener {
            val string_maximumNum = it.getValue<String>()
            if (string_maximumNum != null) {
                num_maximumNum = string_maximumNum!!.toInt()
                findViewById<TextView>(R.id.tv_maximumNum).setText(num_maximumNum.toString())
                //정원 다 차면
                if (userNum == num_maximumNum) {
                    chatRoomsRef.child(chatroomkey).child("isClosed").setValue(true)
                } else {
                    //chatRoomsRef.child(chatroomkey).child("isClosed").setValue(false)
                }
            }

        }
    }

    //모집 끝난 글이면 처리하는 코드
    fun isClosed(chatroomkey: String, context: Context) {
        chatRoomsRef.child(chatroomkey).child("isClosed")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot != null) {
                        if (snapshot.value == true) {
                            findViewById<LinearLayout>(R.id.layout_usernum).background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.chatuser_rectangle_gray
                                )
                            findViewById<TextView>(R.id.tv_usernum).setTextColor(Color.parseColor("#C4C4C4"))
                            findViewById<TextView>(R.id.tv_slash).setTextColor(Color.parseColor("#C4C4C4"))
                            findViewById<TextView>(R.id.tv_maximumNum).setTextColor(
                                Color.parseColor(
                                    "#C4C4C4"
                                )
                            )
                            findViewById<ImageView>(R.id.img_down_arrow).imageTintList =
                                ColorStateList.valueOf(Color.parseColor("#C4C4C4"))
                            findViewById<ImageView>(R.id.img_chatroom_close).setImageResource(R.drawable.chatroom_close_btn_gray)
                            isClosed = true
                        } else if (snapshot.value == false) {
                            findViewById<LinearLayout>(R.id.layout_usernum).background =
                                ContextCompat.getDrawable(context, R.drawable.chatuser_rectangle)
                            findViewById<TextView>(R.id.tv_usernum).setTextColor(Color.parseColor("#FD5401"))
                            findViewById<TextView>(R.id.tv_slash).setTextColor(Color.parseColor("#FD5401"))
                            findViewById<TextView>(R.id.tv_maximumNum).setTextColor(
                                Color.parseColor(
                                    "#FD5401"
                                )
                            )
                            findViewById<ImageView>(R.id.img_down_arrow).imageTintList =
                                ColorStateList.valueOf(Color.parseColor("#FD5401"))
                            findViewById<ImageView>(R.id.img_chatroom_close).setImageResource(R.drawable.chatroom_close_btn)
                            isClosed = false
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }


            })
    }

    //방장 uid 가져오기
    fun getHostUid(chatroomkey: String) {
        chatRoomsRef.child(chatroomkey).child("host")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    hostUid = snapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun push_evaluation(chatroomkey: String) {
        usersRef.get().addOnSuccessListener {
            for (data in it.children) {
                if (roomusersIdList.contains(data.key)) {
                    for (_data in data.children) {
                        if (_data.key.toString() == "token") {
                            val token: String = _data.getValue().toString()
                            //참여자들에게 알림 보내기
                            val notiData_paid = NotiModel(
                                "Saveat - 알림",
                                "채팅방원들의 신뢰도를 평가해주세요!",
                                Calendar.getInstance().time,
                                data.key.toString(),
                                roomTitle,
                                chatroomkey
                            )
                            val pushModel_pay = PushNotification(notiData_paid, token)
                            testPush(pushModel_pay)
                        }
                    }
                }

            }
        }
    }

    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("pushNoti", notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            DEFAULT_GALLERY_REQUEST_CODE -> {
                data ?: return
                //갤러리에서 고른 사진의 uri
                val photo_uri = data.data as Uri
                val split = photo_uri.toString().split("/")
                val size = split.size
                val id = split[size-1]
                //sub_uri = sub_uri.substring(sub_uri.length - 10, sub_uri.length)
                val time = Calendar.getInstance().time
                //val current_time = System.currentTimeMillis()
                //Log.d("시간",date.toString())
                val chatData =
                    ChatData(
                        current_nickname,
                        "(photo)" + id,
                        Auth.current_email!!,
                        Auth.current_uid,
                        time
                    )
                //myRef.push().setValue(chatData)
                chatRoomsRef.child(chatroomkey!!).child("messages").push().setValue(chatData)
                //파이어베이스에 이미지 저장
                //Storage 객체 만들고 참조
                val fileName: String = id + ".jpg"
                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.getReference()
                val uploadTask: UploadTask =
                    storageRef.child("chat_img/${chatroomkey}/" + fileName).putFile(photo_uri!!)
                //새로운 프로필 이미지 저장
                uploadTask.addOnFailureListener { }
                    .addOnSuccessListener {
                    }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

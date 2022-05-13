package com.capstone_design.a1209_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.chat.ChatRoomActivity
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.ActivityDetailBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    lateinit var title: String
    lateinit var hostUID: String
    lateinit var chatroomkey: String
    private var lat: String? = null
    private var lng: String? = null
    private var address: String? = null
    private var linkurl: String? = null
    lateinit var current_nickname: String
    lateinit var data: dataModel
    var user_num: Int = 0
    var host_token: String = ""
    var host_nickname: String = ""
    var isMember = 0
    var usersTokens = mutableListOf<String>()
    var usersUid = mutableListOf<String>()
    var enterSwitch: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        val key = intent.getStringExtra("key")
        Log.d("key", key.toString())

        getBoardData(key.toString())
        getEnterSwitch()
        //방장 uid 가져오기
        //getHostUid(key.toString())

        //지도로 주소 찍기
        binding.detailPlace.setOnClickListener {
            //지도로 만남장소 위치 보여주기
            val intent = Intent(this, DetailPlaceActivity::class.java)
                .putExtra("lat", lat)
                .putExtra("lng", lng)
                .putExtra("place", address)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        //동적 링크로 메뉴링크 첨부하기
        binding.linkBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkurl))
            startActivity(intent)
        }

        //채팅방 입장 버튼 누를 시
        binding.enterBtn.setOnClickListener {
            //참여자가 채팅방으로 이동하는 버튼 클릭 이벤트 발생.
            //채팅방 정보 저장
            //val chatRoomData = ChatRoomData(title, hostUID)
            //FBRef.chatRoomsRef.child(chatroomkey!!).setValue(chatRoomData)
             if (isMember == 0) {
            //멤버가 아닌경우에만 (처음 입장하는 사용자일경우에만)
            FBRef.chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
                .setValue(true)
            //각 사용자가 무슨 채팅방에 참여하고 있는지 저장
            FBRef.userRoomsRef.child(Auth.current_uid).child(chatroomkey).setValue(true)
            //입장 메시지
            val time = Calendar.getInstance().time
            val enter_chatData =
                ChatData(
                    current_nickname,
                    "enter",
                    Auth.current_email!!,
                    Auth.current_uid,
                    time
                )
            //주문서 작성해달라는 공지 메시지 보내기
            val notice_chatData =
                ChatData(
                    "notice",
                    "[공지] 미리 주문서에 메뉴 올려주세요:)",
                    "notice",
                    "notice",
                    time
                )
            FBRef.chatRoomsRef.child(chatroomkey!!).child("messages").push()
                .setValue(enter_chatData)
            FBRef.chatRoomsRef.child(chatroomkey!!).child("messages").push()
                .setValue(notice_chatData)

            //방장에게 입장알림 보내기
            if (enterSwitch == true) {
                val notiData_enter = NotiModel(
                    "Saveat - 알림",
                    "채팅방에 '${current_nickname}'님이 입장했어요.",
                    Calendar.getInstance().time,
                    hostUID,
                    data.title
                )
                val pushModel_enter = PushNotification(notiData_enter, "${host_token}")
                testPush(pushModel_enter)
            }

            //모두에게 정원 알림 보내기
            if ((user_num + 1) == data.person.replace("[^\\d]".toRegex(), "").toInt()) {
                for (i in 0 until usersUid.size) {
                    Log.d("사용자", usersUid[i])
                    Log.d("사용자", usersTokens[i])
                    val notiData_full =
                        NotiModel(
                            "Saveat - 알림",
                            "채팅방 인원이 다 찼어요.",
                            Calendar.getInstance().time,
                            usersUid[i],
                            data.title
                        )
                    val pushModel_full = PushNotification(notiData_full, "${usersTokens[i]}")
                    testPush(pushModel_full)
                }

            }
             }

            //채팅방으로 바로 이동
            val intent = Intent(this, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
            startActivity(intent)
            //현재 액티비티 종료시키기
            finish()
        }

        //방장 신뢰도 확인 버튼
        binding.btnCheckFeedback.setOnClickListener {
            val intent =
                Intent(this, Board_Detail_Evaluation_Activity::class.java).putExtra("uid", hostUID)
                    .putExtra("nickname", host_nickname)
            startActivity(intent)
        }

//        val dw_title=findViewById<TextView>(R.id.detail_title)
//        val dw_place=findViewById<TextView>(R.id.detail_place)
//        val dw_fee=findViewById<TextView>(R.id.detail_fee)
//        val dw_time=findViewById<TextView>(R.id.detail_time)
//        val dw_mention=findViewById<TextView>(R.id.detail_mention)
//        val linkBtn=findViewById<Button>(R.id.linkBtn)//참여자가 채팅방으로 이동하는 버튼
    }

    //파이어베이스의 비동기 방식 -> 동기 방식으로 바꿨습니다!!!!!(혜경)(함수 호출도
    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                data = dataSnapshot.getValue(dataModel::class.java)!!

                //Log.d("detail",data.toString())
                binding.detailTitle.text = data!!.title
                //이미지
                binding.detailImage

                binding.detailPlace.text = data!!.place
                binding.detailFee.text = data!!.fee
                binding.detailTime.text = data!!.time
                binding.detailMention.text = data!!.mention
                chatroomkey = data!!.chatroomkey
                title = data!!.title
                hostUID = data!!.writer
                //좌표계
                lat = data!!.lat
                lng = data!!.lng
                address = data!!.place
                linkurl = data!!.link

                getImage(hostUID)

                when (data!!.category) {
                    "asian" -> binding.detailCategory.text = "아시안, 양식"
                    "bun" -> binding.detailCategory.text = "분식"
                    "bento" -> binding.detailCategory.text = "도시락"
                    "chicken" -> binding.detailCategory.text = "치킨"
                    "pizza" -> binding.detailCategory.text = "피자"
                    "fastfood" -> binding.detailCategory.text = "패스트푸드"
                    "japan" -> binding.detailCategory.text = "일식"
                    "korean" -> binding.detailCategory.text = "한식"
                    "cafe" -> binding.detailCategory.text = "카페, 디저트"
                    "chi" -> binding.detailCategory.text = "중식"
                }

                if (data.image == "0") {
                    when (data!!.category) {
                        "asian" -> binding.detailImage.setImageResource(R.drawable.asian)
                        "bun" -> binding.detailImage.setImageResource(R.drawable.bun)
                        "bento" -> binding.detailImage.setImageResource(R.drawable.bento)
                        "chicken" -> binding.detailImage.setImageResource(R.drawable.chicken)
                        "pizza" -> binding.detailImage.setImageResource(R.drawable.pizza)
                        "fastfood" -> binding.detailImage.setImageResource(R.drawable.fastfood)
                        "japan" -> binding.detailImage.setImageResource(R.drawable.japan)
                        "korean" -> binding.detailImage.setImageResource(R.drawable.korean)
                        "cafe" -> binding.detailImage.setImageResource(R.drawable.cafe)
                        "chi" -> binding.detailImage.setImageResource(R.drawable.china)
                    }
                } else {
                    var imageUri = data.image
                    Glide.with(this@DetailActivity).load(imageUri).into(binding.detailImage)
                }
                getHostRating()
                //getHostRatingData()
                getHostNickname()
                getHostToken()
                getCurrentNickname()
                getUserNum()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        //FBRef.boardRef.child(key).addValueEventListener(postListener)//db:contents-gyeong
        FBRef.board.child(key).addValueEventListener(postListener)//db:map-contents

    }

    fun getHostRating() {
        FBRef.usersRef.child(hostUID).child("rating_datas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var rating_sum = 0f
                    var rating_num = 0
                    if (snapshot.value == null) {
                        binding.tvRating.text = "3.5"
                        binding.ratingBar.rating = 3.5F
                    } else {
                        for (data in snapshot.children) {
                            val ratingData = data.getValue(RatingData::class.java)
                            rating_sum += ratingData!!.rating
                            rating_num++
                        }
                        var rating_avg = rating_sum / rating_num
                        //소수점 일의자리까지 반올림
                        rating_avg = String.format(
                            "%.1f",
                            rating_avg
                        ).toFloat()
                        binding.tvRating.text = rating_avg.toString()
                        binding.ratingBar.rating = rating_avg
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    //방장 별점 가져오기
    /* fun getHostRatingData() {
         FBRef.usersRef.child(hostUID).child("rating").get().addOnSuccessListener {
             if (it.getValue() == null) {
                 binding.tvRating.text = "3.5"
                 binding.ratingBar.rating = 3.5F
             } else {
                 val rating = it.getValue().toString()
                 //별점 세팅
                 binding.tvRating.text = rating
                 binding.ratingBar.rating = rating.toFloat()
             }
         }
     }*/

    //방장 닉네임 가져오기
    //파이어베이스의 비동기 방식 -> 동기 방식
    fun getHostNickname() {
        FBRef.usersRef.child(hostUID).child("nickname").get().addOnSuccessListener {
            host_nickname = it.getValue().toString()
            binding.tvNickname.text = host_nickname
        }
    }

    fun getCurrentNickname() {
        FBRef.usersRef.child(Auth.current_uid).child("nickname").get().addOnSuccessListener {
            current_nickname = it.getValue().toString()
        }
    }

    fun getUserNum() {
        user_num = 0
        FBRef.chatRoomsRef.child(chatroomkey).child("users").get().addOnSuccessListener {
            for (data in it.children) {
                usersUid.add(data.key.toString())
                if (data.key.toString() == Auth.current_uid) {
                    isMember = 1
                }
                user_num++

            }
            binding.enterBtn.text = "채팅방 입장하기 (${user_num}/${
                data.person.replace("[^\\d]".toRegex(), "").toInt()
            })"
            getUserToken()
        }

    }

    fun getEnterSwitch() {
        FBRef.usersRef.child(Auth.current_uid).get()
            .addOnSuccessListener {
                val data = it.getValue<UserData>()
                enterSwitch = data!!.switch_enterNoti
            }
    }

    fun getUserToken() {
        for (uid in usersUid) {
            FBRef.usersRef.child(uid).child("token").get().addOnSuccessListener {
                val data = it.getValue<String>()
                usersTokens.add(data!!)
            }
        }

    }

    //새글 알림 보내기
    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("pushNoti", notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }

    //새글 알림 보내기
    fun getHostToken() {
        FBRef.usersRef.child(hostUID).child("token").get().addOnSuccessListener {
            host_token = it.value.toString()
        }
    }

    fun getImage(uid: String) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(applicationContext).load(it).into(findViewById(R.id.profile_img))
            }.addOnFailureListener {
                findViewById<ImageView>(R.id.profile_img)
                    .setImageResource(R.drawable.profile_cat)
            }
    }
}
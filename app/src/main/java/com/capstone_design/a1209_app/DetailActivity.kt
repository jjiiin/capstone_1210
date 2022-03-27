package com.capstone_design.a1209_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.chat.ChatRoomActivity
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.ActivityDetailBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        val key = intent.getStringExtra("key")
        Log.d("key", key.toString())

        //파이어베이스의 비동기 방식 -> 동기 방식
        CoroutineScope(Dispatchers.IO).launch {
            getBoardData(key.toString())
            getHostRatingData()
            getHostNickname()
            getCurrentNickname()
            getUserNum()
        }
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

        binding.enterBtn.setOnClickListener {
            //참여자가 채팅방으로 이동하는 버튼 클릭 이벤트 발생.
            //채팅방 정보 저장
            //val chatRoomData = ChatRoomData(title, hostUID)
            //FBRef.chatRoomsRef.child(chatroomkey!!).setValue(chatRoomData)
            FBRef.chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
                .setValue(true)
            //각 사용자가 무슨 채팅방에 참여하고 있는지 저장
            FBRef.userRoomsRef.child(Auth.current_uid).child(chatroomkey).setValue(true)
            //글을 쓴 총대니까 채팅방으로 바로 이동
            val intent = Intent(this, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            //입장 메시지
            val enter_chatData =
                ChatData(
                    current_nickname,
                    "enter",
                    Auth.current_email!!,
                    Auth.current_uid
                )
            //주문서 작성해달라는 공지 메시지
            val notice_chatData =
                ChatData(
                    "notice",
                    "[공지] 미리 주문서에 메뉴 올려주세요:)",
                    Auth.current_email!!,
                    "notice"
                )
            FBRef.chatRoomsRef.child(chatroomkey!!).child("messages").push()
                .setValue(enter_chatData)
            FBRef.chatRoomsRef.child(chatroomkey!!).child("messages").push()
                .setValue(notice_chatData)
            //현재 액티비티 종료시키기
            finish()
        }
//        val dw_title=findViewById<TextView>(R.id.detail_title)
//        val dw_place=findViewById<TextView>(R.id.detail_place)
//        val dw_fee=findViewById<TextView>(R.id.detail_fee)
//        val dw_time=findViewById<TextView>(R.id.detail_time)
//        val dw_mention=findViewById<TextView>(R.id.detail_mention)
//        val linkBtn=findViewById<Button>(R.id.linkBtn)//참여자가 채팅방으로 이동하는 버튼
    }

    //파이어베이스의 비동기 방식 -> 동기 방식
    suspend private fun getBoardData(key: String) = suspendCoroutine<String> { continuation ->
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
                continuation.resume(hostUID)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        //FBRef.boardRef.child(key).addValueEventListener(postListener)//db:contents-gyeong
        FBRef.board.child(key).addValueEventListener(postListener)//db:map-contents

    }

    //방장 별점 가져오기
    //파이어베이스의 비동기 방식 -> 동기 방식
    suspend fun getHostRatingData() = suspendCoroutine<String> { continuation ->
        FBRef.usersRef.child(hostUID).child("rating")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rating = snapshot.getValue().toString()
                    //별점 세팅
                    binding.tvRating.text = rating
                    binding.ratingBar.rating = rating.toFloat()
                    continuation.resume(rating)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    //방장 닉네임 가져오기
    //파이어베이스의 비동기 방식 -> 동기 방식
    suspend fun getHostNickname() = suspendCoroutine<String> { continuation ->
        FBRef.usersRef.child(hostUID).child("nickname")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nickname = snapshot.getValue().toString()
                    //별점 세팅
                    binding.tvNickname.text = nickname
                    continuation.resume(nickname)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    suspend fun getCurrentNickname() = suspendCoroutine<String> { continuation ->
        FBRef.usersRef.child(Auth.current_uid).child("nickname")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    current_nickname = snapshot.getValue().toString()
                    continuation.resume(current_nickname)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun getUserNum() {
        FBRef.chatRoomsRef.child(chatroomkey).child("users")
            .addValueEventListener(object : ValueEventListener {
                var num = 0
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        num++
                    }
                    binding.enterBtn.text = "채팅방 입장하기 (${num}/${data.person})"
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
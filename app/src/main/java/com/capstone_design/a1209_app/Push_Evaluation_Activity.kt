package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.ktx.getValue
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.databinding.ActivityPushEvaluationBinding
import com.capstone_design.a1209_app.fragment.MapHomeFragment
import com.capstone_design.a1209_app.utils.Auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.HashMap

class Push_Evaluation_Activity : AppCompatActivity() {

    lateinit var binding: ActivityPushEvaluationBinding
    lateinit var chatroomkey: String
    val roomUserIDList = mutableListOf<String>()
    val roomUserInfoList = mutableListOf<HashMap<String, Any>>()
    var clicked_nickname = ""
    var clicked_uid = ""
    var currentUserNickname = ""
    var last_click_btn_id = 0   //직전에 눌린 버튼의 id (새로운 버튼 클릭시 이 id의 버튼 클릭 해제하기 위해)
    var last_click_uid = ""   //직전에 눌린 프로필의 uid
    var ratingDatas = mutableListOf<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_evaluation)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_push_evaluation)
        chatroomkey = intent.getStringExtra("chatroomkey").toString()

        getRoomTitle()
        getUsers()

        //뒤로가기
        binding.backbtn.setOnClickListener {
            onBackPressed()
        }
        //별점 매기기
        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            binding.tvRating.text = rating.toString()
        }

        //단축어 클릭
        binding.tv1.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "친절해요")
            binding.etContents.setSelection(binding.etContents.length())
        }

        binding.tv2.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "입금이 빨라요")
            binding.etContents.setSelection(binding.etContents.length())
        }
        binding.tv3.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "별로예요")
            binding.etContents.setSelection(binding.etContents.length())
        }
        binding.tv4.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "입금이 늦어요")
            binding.etContents.setSelection(binding.etContents.length())
        }
        binding.tv5.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "확인이 빨라요")
            binding.etContents.setSelection(binding.etContents.length())
        }
        binding.tv6.setOnClickListener {
            binding.etContents.setText(binding.etContents.text.toString() + "좋아요")
            binding.etContents.setSelection(binding.etContents.length())
        }

        //평가 완료 버튼 누르면
        binding.btnDone.setOnClickListener {
            val rating: Float = binding.tvRating.text.toString().toFloat()
            val content: String = binding.etContents.text.toString()
            val time = Calendar.getInstance().time
            val writer_uid = Auth.current_uid
            val ratingData = HashMap<String, Any>()
            ratingData.put("uid", last_click_uid)
            ratingData.put(
                "data",
                RatingData(rating, content, currentUserNickname!!, time, writer_uid)
            )
            if (ratingDatas.size > last_click_btn_id) { //해당 번째 프로필의 평가가 저장되어 있다면 평가 덮어쓰기
                ratingDatas.set(last_click_btn_id, ratingData)
            } else {    //해당 번째 프로필의 평가가 저장되어 있지않다면 평가 추가
                ratingDatas.add(last_click_btn_id, ratingData)
            }
            for (data in ratingDatas) {
                val uid = data.get("uid")
                val ratingData = data.get("data")
                FBRef.usersRef.child(uid.toString()).child("rating_datas").push()
                    .setValue(ratingData)
            }
            finish()
        }
    }

    fun getUsers() {
        var usersNum = 0
        FBRef.chatRoomsRef.child(chatroomkey).child("users").get().addOnSuccessListener {
            for (data in it.children) {
                roomUserIDList.add(data.key.toString())
                usersNum++
            }
            binding.tvUserNum.text = usersNum.toString()
            getUsersInfo()
        }
    }

    fun getUsersInfo() {
        FBRef.usersRef.get().addOnSuccessListener {
            for (data in it.children) {
                if (roomUserIDList.contains(data.key.toString())) {
                    val _userData = data.getValue<UserData>()
                    //사용자가 나라면
                    if (data.key.toString() == Auth.current_uid) {
                        currentUserNickname = _userData!!.nickname
                    } else {
                        val userData = HashMap<String, Any>()
                        userData.put("nickname", _userData!!.nickname)
                        userData.put("uid", data.key.toString())
                        roomUserInfoList.add(userData!!)
                    }

                }
            }
            if (roomUserInfoList.size != 0) {
                //정보 받아오면 디폴트로 첫번째 사용자 선택 상태 만들기
                clicked_nickname = roomUserInfoList[0].get("nickname").toString()
                clicked_uid = roomUserInfoList[0].get("uid").toString()
                binding.tvNickname.text = clicked_nickname

                //이미지를 포함할 레이아웃
                val layout = binding.layoutScrollView
                //스크롤뷰 내부 LinearLayout의 레이아웃 설정
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.CENTER
                layout.layoutParams = layoutParams

                // 아래와 같이 해줘야 기기에 맞는 DP가 나온다.
                val dm: DisplayMetrics = getResources().getDisplayMetrics()
                val pic_width: Int = Math.round(70 * dm.density)
                val pic_height: Int = Math.round(70 * dm.density)
                val pic_margin: Int = Math.round(14 * dm.density)
                val pic_padding: Long = Math.round(5.5 * dm.density)

                // 프로필 사진이 들어갈 레이아웃 설정
                val pic_layoutParam = LinearLayout.LayoutParams(
                    pic_width,
                    pic_height
                )
                pic_layoutParam.rightMargin = pic_margin
                var index = 0
                for (item in roomUserInfoList) {
                    val imageView = CircleImageView(this)
                    //firebase에서 이미지 불러오기
                    val storage: FirebaseStorage = FirebaseStorage.getInstance()
                    val storageRef: StorageReference = storage.getReference()
                    storageRef.child("profile_img/" + item.get("uid") + ".jpg").getDownloadUrl()
                        .addOnSuccessListener {
                            Glide.with(applicationContext).load(it).into(imageView)
                        }.addOnFailureListener {
                            imageView.setImageResource(R.drawable.profile_cat)
                        }
                    //imageView.setImageResource(R.drawable.profile_cat)
                    imageView.layoutParams = pic_layoutParam
                    imageView.setOnClickListener(ButtonClickListener(item, pic_padding))
                    imageView.id = index
                    layout.addView(imageView)
                    if (index == 0) {     //첫번째 이미지 자동으로 선택되게(테두리 적용)
                        imageView!!.setPadding(pic_padding.toInt())
                        imageView.setBackgroundResource(R.drawable.profile_img_border)
                        last_click_btn_id = imageView.id
                        last_click_uid = item.get("uid").toString()
                    }
                    index++
                }
            }


        }
    }

    fun getRoomTitle() {
        FBRef.chatRoomsRef.child(chatroomkey).child("title").get().addOnSuccessListener {
            binding.tvChatroomTitle.text = it.getValue().toString()
        }
    }

    inner class ButtonClickListener(val item: HashMap<String, Any>, val pic_padding: Long) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            if (v!!.id != last_click_btn_id) {    //직전에 선택된 버튼과 내가 지금 누른 버튼이 다를 때, 직전 버튼 클릭 해제(테두리 없앰)
                val rating: Float = binding.tvRating.text.toString().toFloat()
                val content: String = binding.etContents.text.toString()
                val time = Calendar.getInstance().time
                val writer_uid = Auth.current_uid
                findViewById<ImageView>(last_click_btn_id).setPadding(0)
                findViewById<ImageView>(last_click_btn_id).background = null
                //직전에 선택했던 프로필의 평가 저장
                val ratingData = HashMap<String, Any>()
                ratingData.put("uid", last_click_uid)
                ratingData.put(
                    "data",
                    RatingData(rating, content, currentUserNickname!!, time, writer_uid)
                )
                if (ratingDatas.size > last_click_btn_id) { //해당 번째 프로필의 평가가 저장되어 있다면 평가 덮어쓰기
                    ratingDatas.set(last_click_btn_id, ratingData)
                } else {    //해당 번째 프로필의 평가가 저장되어 있지않다면 평가 추가
                    ratingDatas.add(last_click_btn_id, ratingData)
                }
                //다음 사람 평가를 위해 초기화
                binding.etContents.setText("")
                binding.tvRating.setText("3.5")
                binding.ratingBar.rating = 3.5F
            }
            last_click_btn_id = v.id
            last_click_uid = item.get("uid").toString()
            clicked_nickname = item.get("nickname").toString()
            clicked_uid = item.get("uid").toString()
            binding.tvNickname.text = clicked_nickname
            //현재 클릭한 버튼에에테두리 적용
            v!!.setPadding(pic_padding.toInt())
            v.setBackgroundResource(R.drawable.profile_img_border)
            if (ratingDatas.size > last_click_btn_id) { //만약 프로필 버튼을 클릭해 평가를 다시하는 경우
                val data = ratingDatas.get(last_click_btn_id).get("data") as RatingData
                binding.etContents.setText(data.content)
                binding.tvRating.setText(data.rating.toString())
                binding.ratingBar.rating = data.rating
            }
        }

    }
}

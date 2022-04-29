package com.capstone_design.a1209_app

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
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.ktx.getValue
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.utils.Auth
import java.util.*
import kotlin.collections.HashMap

class Push_Evaluation_Activity : AppCompatActivity() {

    lateinit var chatroomkey: String
    val roomUserIDList = mutableListOf<String>()
    val roomUserInfoList = mutableListOf<HashMap<String, Any>>()
    var clicked_nickname = ""
    var clicked_uid = ""
    var currentUserNickname = ""
    var last_click_btn_id = 0   //직전에 눌린 버튼의 id (새로운 버튼 클릭시 이 id의 버튼 클릭 해제하기 위해)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_evaluation)

        chatroomkey = intent.getStringExtra("chatroomkey").toString()

        getRoomTitle()
        getUsers()

        //별점 매기기
        findViewById<RatingBar>(R.id.ratingBar).setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            findViewById<TextView>(R.id.tv_rating).text = rating.toString()
        }

        //단축어 클릭
        findViewById<TextView>(R.id.tv1).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("친절해요")
        }

        findViewById<TextView>(R.id.tv2).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("입금이 빨라요")
        }
        findViewById<TextView>(R.id.tv3).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("별로예요")
        }
        findViewById<TextView>(R.id.tv4).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("입금이 늦어요")
        }
        findViewById<TextView>(R.id.tv5).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("확인이 빨라요")
        }
        findViewById<TextView>(R.id.tv6).setOnClickListener {
            findViewById<EditText>(R.id.et_contents).setText("좋아요")
        }

        //평가 완료 버튼 누르면
        findViewById<Button>(R.id.btn_done).setOnClickListener {
            val rating: Float = findViewById<TextView>(R.id.tv_rating).text.toString().toFloat()
            val content: String = findViewById<TextView>(R.id.et_contents).text.toString()
            val time = Calendar.getInstance().time
            FBRef.usersRef.child(clicked_uid!!).child("rating_datas").push()
                .setValue(RatingData(rating, content, currentUserNickname!!, time))
        }
    }

    fun getUsers() {
        var usersNum = 0
        FBRef.chatRoomsRef.child(chatroomkey).child("users").get().addOnSuccessListener {
            for (data in it.children) {
                roomUserIDList.add(data.key.toString())
                usersNum++
            }
            findViewById<TextView>(R.id.tv_userNum).text = usersNum.toString()
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
                findViewById<TextView>(R.id.tv_nickname).text = clicked_nickname

                //이미지를 포함할 레이아웃
                val layout = findViewById<LinearLayout>(R.id.layout_scrollView)
                //스크롤뷰 내부 LinearLayout의 레이아웃 설정
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.CENTER
                layout.layoutParams = layoutParams

                // 아래와 같이 해줘야 기기에 맞는 DP가 나온다.
                val dm: DisplayMetrics = getResources().getDisplayMetrics()
                val pic_width:Int = Math . round (70 * dm.density)
                val pic_height:Int = Math . round (70 * dm.density)
                val pic_margin:Int = Math . round (14 * dm.density)
                val pic_padding:Long = Math . round (5.5 * dm.density)

                // 프로필 사진이 들어갈 레이아웃 설정
                val pic_layoutParam = LinearLayout.LayoutParams(
                    pic_width,
                    pic_height
                )
                pic_layoutParam.rightMargin = pic_margin
                var index = 0
                for (item in roomUserInfoList) {
                    val imageView = ImageView(this)
                    imageView.setImageResource(R.drawable.profile_cat)
                    imageView.layoutParams = pic_layoutParam
                    imageView.setOnClickListener(ButtonClickListener(item, pic_padding))
                    imageView.id = index
                    layout.addView(imageView)
                    if(index == 0){     //첫번째 이미지 자동으로 선택되게(테두리 적용)
                        imageView!!.setPadding(pic_padding.toInt())
                        imageView.setBackgroundResource(R.drawable.profile_img_border)
                        last_click_btn_id = imageView.id
                    }
                    index++
                }
            }


        }
    }

    fun getRoomTitle() {
        FBRef.chatRoomsRef.child(chatroomkey).child("title").get().addOnSuccessListener {
            findViewById<TextView>(R.id.tv_chatroom_title).text = it.getValue().toString()
        }
    }

    inner class ButtonClickListener(val item: HashMap<String, Any>, val pic_padding:Long) : View.OnClickListener {
        override fun onClick(v: View?) {
            if(v!!.id != last_click_btn_id){    //직전에 선택된 버튼과 내가 지금 누른 버튼이 다를 때, 직전 버튼 클릭 해제(테두리 없앰)
                findViewById<ImageView>(last_click_btn_id).setPadding(0)
                findViewById<ImageView>(last_click_btn_id).background = null
            }
            last_click_btn_id = v.id
            clicked_nickname = item.get("nickname").toString()
            clicked_uid = item.get("uid").toString()
            findViewById<TextView>(R.id.tv_nickname).text = clicked_nickname
            //현재 클릭한 버튼에에테두리 적용
            v!!.setPadding(pic_padding.toInt())
            v.setBackgroundResource(R.drawable.profile_img_border)
        }

    }
}

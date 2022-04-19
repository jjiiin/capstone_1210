package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.chat.Chat_RVAdapter
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.databinding.ActivityBoardDetailEvaluationBinding
import com.capstone_design.a1209_app.databinding.ActivityDisplayEvaluationBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Board_Detail_Evaluation_Activity : AppCompatActivity() {

    companion object {
        var rating_avg: Float = 0.0f
    }

    lateinit var binding: ActivityBoardDetailEvaluationBinding
    lateinit var uid: String
    val ratingLists = mutableListOf<RatingData>()
    lateinit var rvAdapter: Rating_RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_detail_evaluation)

        val rv = binding.recyclerView
        rvAdapter = Rating_RVAdapter(this, ratingLists)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)


        //해당 uid의 신뢰도 화면
        uid = intent.getStringExtra("uid").toString()
        val nickname = intent.getStringExtra("nickname")
        binding.tvNickname.text = nickname

        getRatingData()

        //별점 불러오기
        /* CoroutineScope(Dispatchers.IO).launch {
             getRatingData()
         }*/

    }

    fun getRatingData() {
        FBRef.usersRef.child(uid).child("rating_datas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ratingLists.clear()
                    rating_avg = 0F
                    var rating_num = 0
                    if (snapshot.value == null) {
                        binding.tvRating.text = "3.5"
                        binding.ratingBar.rating = 3.5F
                    } else {
                        for (data in snapshot.children) {
                            val ratingData = data.getValue(RatingData::class.java)
                            val currentTime = Calendar.getInstance().time
                            if ((currentTime.year - ratingData!!.saved_time.year) != 0) {
                                ratingData.display_time =
                                    (currentTime.year - ratingData!!.saved_time.year).toString() + "년 전"
                            } else if ((currentTime.month - ratingData!!.saved_time.month) != 0) {
                                ratingData.display_time =
                                    (currentTime.month - ratingData!!.saved_time.month).toString() + "달 전"
                            } else if ((currentTime.date - ratingData!!.saved_time.date) != 0) {
                                ratingData.display_time =
                                    (currentTime.date - ratingData!!.saved_time.date).toString() + "일 전"
                            } else if ((currentTime.hours - ratingData!!.saved_time.hours) != 0) {
                                ratingData.display_time =
                                    (currentTime.hours - ratingData!!.saved_time.hours).toString() + "시간 전"
                            } else if ((currentTime.minutes - ratingData!!.saved_time.minutes) != 0) {
                                ratingData.display_time =
                                    (currentTime.minutes - ratingData!!.saved_time.minutes).toString() + "분 전"
                            } else {
                                ratingData.display_time =
                                    (currentTime.seconds - ratingData!!.saved_time.seconds).toString() + "초 전"
                            }

                            rating_avg += ratingData!!.rating
                            rating_num++
                            ratingLists.add(ratingData!!)
                        }
                        //별점 평균 구하기
                        rating_avg /= rating_num
                        //소수점 일의자리까지 반올림
                        rating_avg = String.format("%.1f", rating_avg).toFloat()
                        //사용자가 받은 평가 다 불러온 다음에 "평균 별점" 갱신함
                        FBRef.usersRef.child(uid!!).child("rating").setValue(rating_avg.toString())
                        binding.tvRating.text = rating_avg.toString()
                        binding.ratingBar.rating = rating_avg
                        binding.tvRatingNum.text = rating_num.toString()
                        rvAdapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
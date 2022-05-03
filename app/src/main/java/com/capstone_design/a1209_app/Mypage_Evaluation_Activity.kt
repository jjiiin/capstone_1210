package com.capstone_design.a1209_app

import com.capstone_design.a1209_app.Adapter.Rating_RVAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.databinding.ActivityMypageEvaluationBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class Mypage_Evaluation_Activity : AppCompatActivity() {

    lateinit var binding: ActivityMypageEvaluationBinding
    val ratingLists = mutableListOf<RatingData>()
    lateinit var rvAdapter: Rating_RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_evaluation)

        val rv = binding.recyclerView
        rvAdapter = Rating_RVAdapter(this, ratingLists)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        getRatingData()

        //별점 불러오기
       /* CoroutineScope(Dispatchers.IO).launch {
            getRatingData()
        }*/

    }

    fun getRatingData() {
        FBRef.usersRef.child(Auth.current_uid).child("rating_datas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ratingLists.clear()
                    var rating_num = 0
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

                        rating_num++
                        ratingLists.add(ratingData!!)
                    }
                    binding.tvRatingNum.text = rating_num.toString()
                    rvAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

/*    suspend fun getRatingData() = suspendCoroutine<MutableList<RatingData>>{
        continuation ->   FBRef.usersRef.child(uid).child("rating").addValueEventListener(object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            for(data in snapshot.children){
                val ratindData = data.getValue(RatingData::class.java)
                ratingLists.add(ratindData!!)
            }
            continuation.resume(ratingLists)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
    }*/
}
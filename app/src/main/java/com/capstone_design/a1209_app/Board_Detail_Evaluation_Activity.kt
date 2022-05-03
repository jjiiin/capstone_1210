package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.chat.Chat_RVAdapter
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.databinding.ActivityBoardDetailEvaluationBinding
import com.capstone_design.a1209_app.databinding.ActivityDisplayEvaluationBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
        getImage(uid)

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
                }

            })
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
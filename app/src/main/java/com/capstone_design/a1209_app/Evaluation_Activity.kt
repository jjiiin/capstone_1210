package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.databinding.ActivityDisplayEvaluationBinding
import com.capstone_design.a1209_app.databinding.ActivityEvaluationBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import java.util.*

class Evaluation_Activity : AppCompatActivity() {

    lateinit var binding: ActivityEvaluationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation)

        //해당 uid의 신뢰도 평가
        val uid = intent.getStringExtra("uid")
        val nickname = intent.getStringExtra("nickname")
        binding.tvNickname.text = nickname

        //별점 매기기
        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            binding.tvRating.text = rating.toString()
        }


        //단축어 클릭
        binding.tv1.setOnClickListener {
            binding.etContents.setText("친절해요")
        }

        binding.tv2.setOnClickListener {
            binding.etContents.setText("입금이 빨라요")

        }
        binding.tv3.setOnClickListener {
            binding.etContents.setText("별로예요")
        }
        binding.tv4.setOnClickListener {
            binding.etContents.setText("입금이 늦어요")
        }
        binding.tv5.setOnClickListener {
            binding.etContents.setText("확인이 빨라요")
        }
        binding.tv6.setOnClickListener {
            binding.etContents.setText("좋아요")
        }

        binding.btnDone.setOnClickListener {
            val rating: Float = binding.tvRating.text.toString().toFloat()
            val content: String = binding.etContents.text.toString()
            val time = Calendar.getInstance().time
            FBRef.usersRef.child(uid!!).child("rating_datas").push()
                .setValue(RatingData(rating, content, nickname!!, time))
            finish()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }

    }

}
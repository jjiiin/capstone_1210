package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.databinding.ActivityDisplayEvaluationBinding
import com.capstone_design.a1209_app.databinding.ActivityEvaluationBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class Evaluation_Activity : AppCompatActivity() {

    lateinit var binding: ActivityEvaluationBinding
    var current_nickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation)

        //해당 uid의 신뢰도 평가
        val uid = intent.getStringExtra("uid")
        val nickname = intent.getStringExtra("nickname")
        getImage(uid.toString())
        getNickName()

        binding.tvNickname.text = nickname

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

        binding.btnDone.setOnClickListener {
            val rating: Float = binding.tvRating.text.toString().toFloat()
            val content: String = binding.etContents.text.toString()
            val time = Calendar.getInstance().time
            val writer_uid = Auth.current_uid
            FBRef.usersRef.child(uid!!).child("rating_datas").push()
                .setValue(RatingData(rating, content, current_nickname!!, time, writer_uid))
            finish()
        }
        binding.btnCancel.setOnClickListener {
            finish()
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

    fun getNickName() {
        FBRef.usersRef.child(Auth.current_uid).get().addOnSuccessListener {
            for (data in it.children) {
                if (data.key.toString() == "nickname") {
                    current_nickname = data.getValue().toString()
                }
            }
        }
    }
}
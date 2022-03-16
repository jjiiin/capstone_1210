package com.capstone_design.a1209_app.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.capstone_design.a1209_app.Evaluation_Display_Activity
import com.capstone_design.a1209_app.Mypage_Evaluation_Activity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
import com.capstone_design.a1209_app.databinding.FragmentMyBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container, false)

        getRating()
        getNickName()

        binding.tvLogout.setOnClickListener {
            Auth.auth.signOut()
        }


        return binding.root
    }

    fun getRating() {
        FBRef.usersRef.child(Auth.current_uid).child("rating")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        binding.ratingBar.rating = 3.5F
                        binding.tvRating.text = "3.5"
                    } else {
                        val rating = snapshot.value.toString()
                        binding.ratingBar.rating = rating.toFloat()
                        binding.tvRating.text = rating
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun getNickName(){
        FBRef.usersRef.child(Auth.current_uid).child("nickname").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.getValue().toString()
                binding.tvNickname.text = nickname
                //평가확인 버튼 누를시
                binding.btnDisplayEvaluation.setOnClickListener {
                    val intent =
                        Intent(context, Mypage_Evaluation_Activity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
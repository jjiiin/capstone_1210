package com.capstone_design.a1209_app.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.*
import com.capstone_design.a1209_app.auth.IntroActivity
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.databinding.FragmentMyBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    var isAccountExist = false
    private lateinit var auth: FirebaseAuth

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
        getImage()
        getAccount()

        binding.tvLogout.setOnClickListener {
            Auth.auth.signOut()

        }

        //현재 설정해둔 주소 출력하기
        auth = Firebase.auth
        val database = Firebase.database
        val schRef: DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if(item.set=="1")
                            binding.addressTv.text=item.address
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.layoutAccountSetting.setOnClickListener {
            if (isAccountExist) {
                val intent = Intent(context, Mypage_Account_Activity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, Mypage_Account_Setting_Activity::class.java)
                startActivity(intent)
            }

        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(context, Mypage_Edit_Activity::class.java)
            startActivity(intent)
        }

        binding.layoutMyWritten.setOnClickListener {
            val intent = Intent(context, MyWrittenActivity::class.java)
            startActivity(intent)
        }


        binding.mapGo.setOnClickListener {
            val intent = Intent(context, AddressSearchActivity::class.java)
                .putExtra("page","MyFragment")
            startActivity(intent)
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
                }

            })
    }

    fun getNickName() {
        FBRef.usersRef.child(Auth.current_uid).child("nickname")
            .addValueEventListener(object : ValueEventListener {
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
                }

            })
    }

    fun getImage() {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + Auth.current_uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(requireContext()).load(it).into(binding.imageProfile)
            }.addOnFailureListener {
                binding.imageProfile.setImageResource(R.drawable.profile_cat)
            }
    }

    fun getAccount() {
        FBRef.usersRef.child(Auth.current_uid).child("account").get()
            .addOnSuccessListener {
                if(it.getValue() != null){
                    isAccountExist = true
                }else{
                    isAccountExist = false
                }

            }
            .addOnFailureListener { isAccountExist = false }
    }
}
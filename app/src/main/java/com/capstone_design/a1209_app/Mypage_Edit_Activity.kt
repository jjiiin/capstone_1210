package com.capstone_design.a1209_app

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyLog
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.databinding.ActivityMypageEditBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.lang.Exception
import java.net.URI
import java.util.*

class Mypage_Edit_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageEditBinding
    var photo_uri: Uri? = null
    var nicknameCheck = false
    var blankCheck = true

    //프로필 사진 요청 코드
    private val DEFAULT_GALLERY_REQUEST_CODE = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mypage_edit)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_edit)

        getImage()
        getNickName()
        binding.tvEmail.text = Auth.current_email

        binding.btnGallery.setOnClickListener {
            //갤러리에서 사진 가져오기
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/")
            startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
        }

        binding.btnNicknameCheck.isEnabled = false
        binding.btnDone.isEnabled = false

        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.length > 0) {
                    binding.btnNicknameCheck.isEnabled = true
                    binding.btnNicknameCheck.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#382626"))
                } else {
                    binding.tvNicknamePass.visibility = View.INVISIBLE
                    binding.btnNicknameCheck.isEnabled = false
                    binding.btnNicknameCheck.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.btnNicknameCheck.setOnClickListener {
            checkNickname()
        }

        binding.btnDone.setOnClickListener {
            if (photo_uri != null) {
                //사진 저장
                uploadImage()
            }
            if (binding.etNickname.text.toString() != "" && nicknameCheck) {
                //닉네임 저장
                FBRef.usersRef.child(Auth.current_uid).child("nickname")
                    .setValue(binding.etNickname.text.toString())
            }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            DEFAULT_GALLERY_REQUEST_CODE -> {
                data ?: return
                //갤러리에서 고른 사진의 uri
                photo_uri = data.data as Uri
                binding.imageProfile.setImageURI(photo_uri)
                binding.btnDone.isEnabled = true
                binding.btnDone.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#FD5401"))
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadImage() {

        //파이어베이스에 이미지 저장
        //Storage 객체 만들고 참조
        val fileName: String = Auth.current_uid + ".jpg"
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        val uploadTask: UploadTask =
            storageRef.child("profile_img/" + fileName).putFile(photo_uri!!)
        //새로운 프로필 이미지 저장
        uploadTask.addOnFailureListener { }
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "사진저장!", Toast.LENGTH_SHORT).show()
            }
    }

    fun getImage() {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + Auth.current_uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(applicationContext).load(it).into(binding.imageProfile)
            }.addOnFailureListener {
                binding.imageProfile.setImageResource(R.drawable.profile_cat)
            }
    }

    fun checkNickname() {
        var isAvaliable = true
        val currentNickname: String = binding.etNickname.text.toString()
        FBRef.usersRef.get().addOnSuccessListener {
            first@ for (data in it.children) {
                for (_data in data.children) {
                    if (_data.key.toString() == "nickname") {
                        if (_data.getValue().toString() == currentNickname) {
                            isAvaliable = false
                            break@first
                        }
                    }
                }
            }
            if (isAvaliable) {
                binding.tvNicknamePass.visibility = View.VISIBLE
                binding.tvNicknamePass.text = "사용가능한 닉네임입니다"
                binding.tvNicknamePass.setTextColor(Color.parseColor("#00A531"))
                nicknameCheck = true
                binding.btnDone.isEnabled = true
                binding.btnDone.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#FD5401"))
            } else {
                binding.tvNicknamePass.visibility = View.VISIBLE
                binding.tvNicknamePass.text = "사용할 수 없는 닉네임입니다"
                binding.tvNicknamePass.setTextColor(Color.parseColor("#FF3535"))
                nicknameCheck = false
                binding.btnDone.isEnabled = false
                binding.btnDone.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
            }
        }
    }

    fun getNickName() {
        FBRef.usersRef.child(Auth.current_uid).get().addOnSuccessListener {
            for (data in it.children) {
                if (data.key.toString() == "nickname") {
                    binding.tvCurrentNickname.text = data.getValue().toString()
                }
            }
        }
    }
}
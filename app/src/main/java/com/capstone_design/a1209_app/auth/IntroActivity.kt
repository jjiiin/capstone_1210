package com.capstone_design.a1209_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.MainActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IntroActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIntroBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_intro)

        auth= Firebase.auth


        binding.loginBtn.setOnClickListener {
            //바로 홈 화면이 보여야함.
            val id=binding.idArea.text.toString()
            val pwd=binding.pwdArea.text.toString()

            auth.signInWithEmailAndPassword(id, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent= Intent(this, MainActivity::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(this,"로그인 성공", Toast.LENGTH_LONG).show()
                    } else {
                        //Toast.makeText(this,"로그인 실패",Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.joinBtn.setOnClickListener{
            startActivity(Intent(this, JoinActivity::class.java))
            finish()
        }




    }
}
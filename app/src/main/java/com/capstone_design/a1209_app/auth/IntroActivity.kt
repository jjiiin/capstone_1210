package com.capstone_design.a1209_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyLog
import com.capstone_design.a1209_app.MainActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.databinding.ActivityIntroBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

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
                        //token값 users에 저장하기
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(VolleyLog.TAG, "Fetching FCM registration token failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new FCM registration token
                            val token = task.result
                            FBRef.usersRef.child(auth.currentUser!!.uid).child("token").setValue(token)

                            // Log and toast
                            Log.e("token",token.toString())
                        })
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
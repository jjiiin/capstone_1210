package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.android.volley.VolleyLog
import com.android.volley.VolleyLog.TAG
import com.capstone_design.a1209_app.auth.IntroActivity
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        //token값 users에 저장하기
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(VolleyLog.TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//            FBRef.usersRef.child(auth.currentUser!!.uid).child("token").setValue(token)
//
//            // Log and toast
//            Log.e("token",token.toString())
//        })

        auth= Firebase.auth
        //user-id가 존재한다면 바로 main으로 넘어가고 아니면 intro의 로그인 화면으로 간다.
        if(auth.currentUser?.uid==null){
            Handler().postDelayed({
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            },2000)

        }else{
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },2000)

        }


    }
}
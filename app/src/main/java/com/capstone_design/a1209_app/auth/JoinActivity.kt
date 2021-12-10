package com.capstone_design.a1209_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.MainActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth=Firebase.auth
        binding= DataBindingUtil.setContentView(this, R.layout.activity_join)
        binding.joinBtn.setOnClickListener {
            //id

            var isGotoJoin = true
            val id=binding.idArea.text.toString()

            val pwd=binding.pwdArea.text.toString()
            val name=binding.nickArea.text.toString()
            if(id.isEmpty()){
                //Toast.makeText(this,"id를 입력해주세요",Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(pwd.isEmpty()){
                Toast.makeText(this,"pwd를 입력해주세요",Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(name.isEmpty()){
                Toast.makeText(this,"닉네임을 입력해주세요",Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }

            if(isGotoJoin){
                //신규회원 가입
                Toast.makeText(this,id,Toast.LENGTH_LONG).show()
                auth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //Toast.makeText(this,"성공",Toast.LENGTH_LONG).show()
                            val intent= Intent(this, MainActivity::class.java)
                            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            //Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                        }
                    }
            }

        }



    }
}
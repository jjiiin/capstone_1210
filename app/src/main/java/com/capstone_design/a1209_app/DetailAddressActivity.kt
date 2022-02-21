package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DetailAddressActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_address)


        auth= Firebase.auth
        var addressTv:TextView=findViewById(R.id.addressTv)
        var detailEv: EditText=findViewById(R.id.detailEv)
        var nameEv:EditText=findViewById(R.id.nameEv)
        var saveBtn: Button =findViewById(R.id.saveBtn)

        addressTv.text=intent.getStringExtra("주소").toString()
        val address=intent.getStringExtra("주소").toString()

        saveBtn.setOnClickListener {
            val detail=detailEv.text.toString()
            val name=nameEv.text.toString()
            val model=addressData(
                address,detail,name,false
            )
            FBRef.usersRef.child(auth.currentUser!!.uid).child("address").child(name).setValue(model)
            val intent= Intent(this, AddressSearchActivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
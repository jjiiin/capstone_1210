package com.capstone_design.a1209_app

import Adapter.KwRVAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class KeywordSettingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val dataModelList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword_setting)
        auth= Firebase.auth
        val kwEt:EditText=findViewById(R.id.kwEt)
        val saveBtn: Button =findViewById(R.id.saveBtn)

        //키워드 등록
        saveBtn.setOnClickListener {
            val keyword=kwEt.text.toString()
            FBRef.usersRef.child(auth.currentUser!!.uid).child("keyword").push()
                .setValue(keyword)
        }

        //RVAdapter장착하기
        val rv = findViewById<RecyclerView>(R.id.keywordRV)
        val rvAdapter = KwRVAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(this)
        rv.layoutManager = layout
        rv.setHasFixedSize(true)
        //키워드 리스트 뽑아오기
        val schRef =FBRef.usersRef.child(auth.currentUser!!.uid).child("keyword")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                for (DataModel in snapshot.children) {
                    dataModelList.add(DataModel.getValue(String::class.java)!!)

                }
                rvAdapter.notifyDataSetChanged()
                Log.d("data",dataModelList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
}
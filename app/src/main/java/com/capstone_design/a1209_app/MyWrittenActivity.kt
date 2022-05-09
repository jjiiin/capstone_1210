package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.capstone_design.a1209_app.Adapter.LvAdpater
import com.capstone_design.a1209_app.Adapter.MyWrittenLVAdapter
import com.capstone_design.a1209_app.dataModels.dataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyWrittenActivity : AppCompatActivity() {
    private lateinit var adapter: MyWrittenLVAdapter
    private var items = mutableListOf<dataModel>()
    private lateinit var auth: FirebaseAuth
    val database = Firebase.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_written)
        val Lv=findViewById<ListView>(R.id.LvMain)

        auth = Firebase.auth
        adapter = MyWrittenLVAdapter(items,this)
        Lv.adapter=adapter
        val boardRef : DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        if(item.writer==auth.currentUser!!.uid){
                            items.add(item)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
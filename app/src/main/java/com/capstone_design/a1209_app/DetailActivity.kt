package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.databinding.ActivityDetailBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DetailActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_detail, )

        val key=intent.getStringExtra("key")
        Log.d("key",key.toString())

        getBoardData(key.toString())



        binding.linkBtn.setOnClickListener {
            //참여자가 채팅방으로 이동하는 버튼 클릭 이벤트 발생.
        }
//        val dw_title=findViewById<TextView>(R.id.detail_title)
//        val dw_place=findViewById<TextView>(R.id.detail_place)
//        val dw_fee=findViewById<TextView>(R.id.detail_fee)
//        val dw_time=findViewById<TextView>(R.id.detail_time)
//        val dw_mention=findViewById<TextView>(R.id.detail_mention)
//        val linkBtn=findViewById<Button>(R.id.linkBtn)//참여자가 채팅방으로 이동하는 버튼
    }

    private fun getBoardData(key:String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data=dataSnapshot.getValue(dataModel::class.java)

                Log.d("detail",data.toString())
                binding.detailTitle.text=data!!.title
                binding.detailPlace.text=data!!.place
                binding.detailFee.text=data!!.fee
                binding.detailTime.text=data!!.time
                binding.detailMention.text=data!!.mention

            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)

    }
}
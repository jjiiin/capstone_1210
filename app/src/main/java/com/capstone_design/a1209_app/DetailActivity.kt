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
                when(data!!.category){
                    "asian"-> binding.detailCategory.text = "아시안, 양식"
                    "bun"->binding.detailCategory.text="분식"
                    "bento"->binding.detailCategory.text="도시락"
                    "chicken"->binding.detailCategory.text="치킨"
                    "pizza"->binding.detailCategory.text="피자"
                    "fastfood"->binding.detailCategory.text="패스트푸드"
                    "japan"->binding.detailCategory.text="일식"
                    "korean"->binding.detailCategory.text="한식"
                    "cafe"->binding.detailCategory.text="카페, 디저트"
                    "chi"->binding.detailCategory.text="중식"
                }
                when(data!!.category){
                    "asian"->binding.detailImage.setImageResource(R.drawable.asian)
                    "bun"->binding.detailImage.setImageResource(R.drawable.bun)
                    "bento"->binding.detailImage.setImageResource(R.drawable.bento)
                    "chicken"->binding.detailImage.setImageResource(R.drawable.chicken)
                    "pizza"->binding.detailImage.setImageResource(R.drawable.pizza)
                    "fastfood"->binding.detailImage.setImageResource(R.drawable.fastfood)
                    "japan"->binding.detailImage.setImageResource(R.drawable.japan)
                    "korean"->binding.detailImage.setImageResource(R.drawable.korean)
                    "cafe"->binding.detailImage.setImageResource(R.drawable.cafe)
                    "chi"->binding.detailImage.setImageResource(R.drawable.china)
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)

    }
}
package com.capstone_design.a1209_app.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModel
import com.capstone_design.a1209_app.databinding.ActivityBoardWirteBinding
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardWirteActivity : AppCompatActivity() {
//글쓰기 화면에 스피너 넣어야함.
    private lateinit var binding : ActivityBoardWirteBinding

    private val items = mutableListOf<dataModel>()
    public var title_detail = ""
    public var place_detail = ""
    public var fee_detail = ""
    public var person_detail = ""
    public var mention_detail = ""
    public var time_detail = ""

    public var link_detail=""
    public var timeText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_board_wirte)
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("contents")



        var time=""
        binding.timeFree.setOnClickListener {
            time = "협의 가능"
        }
        //스피너로 시간 구현하기
        var person = ""
        binding.person1List.setOnClickListener {
            person = "1명"
        }

        binding.person2List.setOnClickListener {
            person = "2명"
        }

        binding.person3List.setOnClickListener {
            person = "3명"
        }

        binding.personNList.setOnClickListener {
            person = "제한 없음"
        }


        binding.saveBtn.setOnClickListener {
            val title_dm=binding.titleList.text.toString()

            val person_dm=person

            val time_dm=time
            //스피너로 입력하기
            val fee_dm=binding.textFee.text.toString().plus("원")
            Log.d("BWA",fee_dm)
            val place_dm=binding.placeList.text.toString()
            val mention_dm=binding.mention.text.toString()

            val model=dataModel(
                title_dm,
                person_dm,
                time_dm,
                fee_dm,
                place_dm,
                mention_dm
            )
            items.add(model)
            FBRef.boardRef.push().setValue(model)

            //글을 쓴 총대니까 채팅방으로 바로 이동





        }
    }
}
package com.capstone_design.a1209_app.board

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
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
    private var hour=""
    private var min=""
    private var day=""
    private var time=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_board_wirte)
        // Write a message to the database
        val database = Firebase.database

        var category=""
        //버튼 클릭시 category에 값 할당하기
        binding.categoryAsian.setOnClickListener {

            category="asian"
        }
        binding.categoryBun.setOnClickListener {

            category="bun"
        }
        binding.categoryChicken.setOnClickListener {

            category="chicken"
        }
        binding.categoryPizza.setOnClickListener {

            category="pizza"
        }
        binding.categoryFast.setOnClickListener {

            category="fastfood"
        }
        binding.categoryJap.setOnClickListener {

            category="japan"
        }
        binding.categoryKor.setOnClickListener {

            category="korean"
        }
        binding.categoryDo.setOnClickListener {

            category="bento"
        }
        binding.categoryCafe.setOnClickListener {

            category="cafe"
        }
        binding.categoryChi.setOnClickListener {

            category="chi"
        }

        binding.timeFree.setOnClickListener {
            time = "협의 가능"
        }
        //스피너로 시간 구현하기
        var hourData=resources.getStringArray(R.array.array_hours)
        var minData=resources.getStringArray(R.array.array_minutes)


            var adapter1=ArrayAdapter(this, android.R.layout.simple_spinner_item,hourData)
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHours.adapter=adapter1
            binding.spinnerHours.setSelection(0)

            var adapter2=ArrayAdapter(this, android.R.layout.simple_spinner_item,minData)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMinutes.adapter=adapter2
            binding.spinnerMinutes.setSelection(0)
            binding.spinnerHours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    hour=hourData[p2]
                }

            }


            binding.spinnerMinutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    min= minData[p2]
                    Log.d("detail",min)
                }

            }


            binding.timeAm.setOnClickListener {
                day="AM"
            }
            binding.timePm.setOnClickListener {
                day="PM"
            }
            time="${day} ${hour} ${min}"











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
            val category_dm=category
            val person_dm=person

            val time_dm=time
            //스피너로 입력하기
            val fee_dm=binding.textFee.text.toString().plus("원")
            Log.d("BWA",fee_dm)
            val place_dm=binding.placeList.text.toString()
            val mention_dm=binding.mention.text.toString()
            val link_dm=binding.link.text.toString()

            val model=dataModel(
                title_dm,
                category_dm,
                person_dm,
                time_dm,
                fee_dm,
                place_dm,
                link_dm,
                mention_dm
            )
            items.add(model)
            FBRef.boardRef.push().setValue(model)

            //글을 쓴 총대니까 채팅방으로 바로 이동





        }
    }

}
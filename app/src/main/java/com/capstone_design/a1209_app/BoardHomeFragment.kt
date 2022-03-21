package com.capstone_design.map_test

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyLog
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.board.LvAdpater
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.FragmentBoardHomeBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class BoardHomeFragment : Fragment(){
    private  lateinit var binding : FragmentBoardHomeBinding

    private var cnt=0
    private var items= mutableListOf<dataModel>()
    private val itemsKeyList= mutableListOf<String>()
    private lateinit var adapter:LvAdpater

    private lateinit var auth: FirebaseAuth
    val database = Firebase.database
    val myRef = database.getReference("BoardWirte")
    private var buttoncolor="all"
//    private lateinit var category:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_home, container, false)
        auth = Firebase.auth
        adapter = LvAdpater(items, this)
        binding.count.text = items.size.toString()
        var category=""
        if(cnt==0) {
            listViewAll("0")
            category="all"
            buttonColor("all")
            cnt+=1
        }
        //버튼 클릭시 category에 값 할당하기
        binding.categoryAll.setOnClickListener {
            category="all"
            listViewAll("0")
            buttonColor("all")
        }
        binding.categoryAsian.setOnClickListener {
            category="asian"
            listView("asian","0")
            buttonColor("asian")
        }
        binding.categoryBun.setOnClickListener {
            listView("bun","0")
            category="bun"
            buttonColor("bun")
        }
        binding.categoryChicken.setOnClickListener {
            category = "chicken"
            listView("chicken","0")
            buttonColor("chicken")
        }
        binding.categoryPizza.setOnClickListener {
            category = "pizza"
            listView("chicken","0")
            buttonColor("pizza")
        }
        binding.categoryFast.setOnClickListener {
            category = "fastfood"
            listView("fastfood","0")
            buttonColor("fast")
        }
        binding.categoryJap.setOnClickListener {
            category = "japan"
            listView("japan","0")
            buttonColor("japan")
        }
        binding.categoryKor.setOnClickListener {
            category = "korean"
            listView("korean","0")
            buttonColor("korean")
        }
        binding.categoryDo.setOnClickListener {
            category = "bento"
            listView("bento","0")
            buttonColor("bento")
        }
        binding.categoryCafe.setOnClickListener {
            category = "cafe"
            listView("cafe","0")
            buttonColor("cafe")
        }
        binding.categoryChi.setOnClickListener {
            category = "chi"
            listView("chi","0")
            buttonColor("chi")
        }

        //새글알림 스위치
        binding.noti.setOnCheckedChangeListener { buttonView, isChecked ->
            //token값 users에 저장하기
            var token=""
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(VolleyLog.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                Log.d("switch_cate",category)
                // Get new FCM registration token
                token = task.result.toString()
                //FBRef.usersRef.child(auth.currentUser!!.uid).child("token").setValue(token)
                if(isChecked){
                    FBRef.notificationRef.child(category).child(auth.currentUser!!.uid).setValue(token)
                    FBRef.usersRef.child(auth.currentUser!!.uid).child("category").child(category).setValue("1")
                }
                else{
                    FBRef.notificationRef.child(category).child(auth.currentUser!!.uid).removeValue()
                    FBRef.usersRef.child(auth.currentUser!!.uid).child("category").child(category).setValue("0")
                }

                // Log and toast
                Log.e("token",token)
            })
        }

        //즉각 주문 체크 박스
        binding.quick.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(category=="all"){
                    listViewAll("1")
                }else {
                    listView(category,"1")
                }
            }else{
                if(category=="all"){
                    listViewAll("0")
                }else {
                    listView(category,"0")
                }
            }
        }

        binding.LvMain.adapter=adapter

            binding.LvMain.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(context, DetailActivity::class.java)
                //firebase에 있는 board에 대한 데이터의 id를 가져오기
                intent.putExtra("key", itemsKeyList[position])
                Log.d("key", itemsKeyList[position])
                startActivity(intent)
            }

            binding.writeBtn.setOnClickListener {
                val intent = Intent(context, BoardWirteActivity::class.java)
                startActivity(intent)
            }

            return binding.root
        }

    private fun buttonColor(category:String){
                binding.categoryAsian.setBackgroundResource(R.drawable.round_button)
                binding.categoryAsian.setTextColor(Color.BLACK)
                binding.categoryAsian.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


                binding.categoryBun.setBackgroundResource(R.drawable.round_button)
                binding.categoryBun.setTextColor(Color.BLACK)
                binding.categoryBun.setTypeface(binding.categoryBun.typeface, Typeface.NORMAL)


                binding.categoryKor.setBackgroundResource(R.drawable.round_button)
                binding.categoryKor.setTextColor(Color.BLACK)
                binding.categoryKor.setTypeface(binding.categoryKor.typeface, Typeface.NORMAL)


                binding.categoryJap.setBackgroundResource(R.drawable.round_button)
                binding.categoryJap.setTextColor(Color.BLACK)
                binding.categoryJap.setTypeface(binding.categoryJap.typeface, Typeface.NORMAL)



                binding.categoryChi.setBackgroundResource(R.drawable.round_button)
                binding.categoryChi.setTextColor(Color.BLACK)
                binding.categoryChi.setTypeface(binding.categoryChi.typeface, Typeface.NORMAL)


                binding.categoryFast.setBackgroundResource(R.drawable.round_button)
                binding.categoryFast.setTextColor(Color.BLACK)
                binding.categoryFast.setTypeface(binding.categoryFast.typeface, Typeface.NORMAL)


                binding.categoryDo.setBackgroundResource(R.drawable.round_button)
                binding.categoryDo.setTextColor(Color.BLACK)
                binding.categoryDo.setTypeface(binding.categoryDo.typeface, Typeface.NORMAL)



                binding.categoryCafe.setBackgroundResource(R.drawable.round_button)
                binding.categoryCafe.setTextColor(Color.BLACK)
                binding.categoryCafe.setTypeface(binding.categoryCafe.typeface, Typeface.NORMAL)


                binding.categoryChicken.setBackgroundResource(R.drawable.round_button)
                binding.categoryChicken.setTextColor(Color.BLACK)
                binding.categoryChicken.setTypeface(binding.categoryChicken.typeface, Typeface.NORMAL)


                binding.categoryPizza.setBackgroundResource(R.drawable.round_button)
                binding.categoryPizza.setTextColor(Color.BLACK)
                binding.categoryPizza.setTypeface(binding.categoryPizza.typeface, Typeface.NORMAL)

                binding.categoryAll.setBackgroundResource(R.drawable.round_button)
                binding.categoryAll.setTextColor(Color.BLACK)
                binding.categoryAll.setTypeface(binding.categoryAll.typeface, Typeface.NORMAL)


        when(category){
            "asian"->{binding.categoryAsian.setBackgroundResource(R.drawable.select_round)
                binding.categoryAsian.setTextColor(Color.WHITE)
                binding.categoryAsian.setTypeface(binding.categoryAsian.typeface, Typeface.BOLD)}
            "bun"->{binding.categoryBun.setBackgroundResource(R.drawable.select_round)
                binding.categoryBun.setTextColor(Color.WHITE)
                binding.categoryBun.setTypeface(binding.categoryBun.typeface, Typeface.BOLD)}
            "korean"->{binding.categoryKor.setBackgroundResource(R.drawable.select_round)
                binding.categoryKor.setTextColor(Color.WHITE)
                binding.categoryKor.setTypeface(binding.categoryKor.typeface, Typeface.BOLD)}
            "japan"->{binding.categoryJap.setBackgroundResource(R.drawable.select_round)
                binding.categoryJap.setTextColor(Color.WHITE)
                binding.categoryJap.setTypeface(binding.categoryJap.typeface, Typeface.BOLD)}
            "chi"->{binding.categoryChi.setBackgroundResource(R.drawable.select_round)
                binding.categoryChi.setTextColor(Color.WHITE)
                binding.categoryChi.setTypeface(binding.categoryChi.typeface, Typeface.BOLD)}
            "fast"->{binding.categoryFast.setBackgroundResource(R.drawable.select_round)
                binding.categoryFast.setTextColor(Color.WHITE)
                binding.categoryFast.setTypeface(binding.categoryFast.typeface, Typeface.BOLD)}
            "bento"->{binding.categoryDo.setBackgroundResource(R.drawable.select_round)
                binding.categoryDo.setTextColor(Color.WHITE)
                binding.categoryDo.setTypeface(binding.categoryDo.typeface, Typeface.BOLD)}
            "cafe"->{binding.categoryCafe.setBackgroundResource(R.drawable.select_round)
                binding.categoryCafe.setTextColor(Color.WHITE)
                binding.categoryCafe.setTypeface(binding.categoryCafe.typeface, Typeface.BOLD)}
            "chicken"->{binding.categoryChicken.setBackgroundResource(R.drawable.select_round)
                binding.categoryChicken.setTextColor(Color.WHITE)
                binding.categoryChicken.setTypeface(binding.categoryChicken.typeface, Typeface.BOLD)}
            "pizza"->{binding.categoryPizza.setBackgroundResource(R.drawable.select_round)
                binding.categoryPizza.setTextColor(Color.WHITE)
                binding.categoryPizza.setTypeface(binding.categoryPizza.typeface, Typeface.BOLD)}
            "all"->{binding.categoryAll.setBackgroundResource(R.drawable.select_round)
                binding.categoryAll.setTextColor(Color.WHITE)
                binding.categoryAll.setTypeface(binding.categoryAll.typeface, Typeface.BOLD)}

        }

    }

    private fun listView(category:String,quick:String){
        val boardRef : DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        if(quick=="1") {
                            if (category == item.category) {
                                if(item.quick=="1")
                                items.add(item!!)
                                itemsKeyList.add(data.key.toString())
                            }
                        }else{
                            if (category == item.category) {
                                items.add(item!!)
                                itemsKeyList.add(data.key.toString())
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                itemsKeyList.reverse()
                items.reverse()
                Log.d("bun1",items.toString())

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        Log.d("bun2",items.toString())

    }
    private fun listViewAll(quick:String){
        val boardRef : DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        //Log.d("category",category)
                            if(quick=="1") {
                                if(item.quick=="1") {
                                    items.add(item!!)
                                    itemsKeyList.add(data.key.toString())
                                }
                            }else{
                                items.add(item!!)
                                itemsKeyList.add(data.key.toString())
                            }
                    }

                    adapter.notifyDataSetChanged()
                }
                itemsKeyList.reverse()
                items.reverse()
                Log.d("bun1",items.toString())

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        Log.d("bun2",items.toString())

    }


}
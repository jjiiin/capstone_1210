package com.capstone_design.a1209_app.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyLog
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.Adapter.LvAdpater
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.FragmentBoardHomeBinding
import com.capstone_design.a1209_app.utils.FBRef
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
    private lateinit var adapter: LvAdpater

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
            binding.noti.visibility=View.INVISIBLE
            binding.notiText.visibility=View.INVISIBLE
            buttonColor("all")
            cnt+=1
        }
        //새글알림
        //새글알림 설정 확인 후 스위치 바꾸기
        var cateList=ArrayList<String>()
        val cateRef:DatabaseReference = database.getReference("users")
            .child(auth.currentUser!!.uid).child("category")
        cateRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    if(data!=null){
                            if(data.value=="1"){
                                Log.d("data_",data.key.toString()+category)
                                cateList.add(data.key.toString())
                                //binding.noti.isChecked=true
                            }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        //버튼 클릭시 category에 값 할당하기
        binding.categoryAll.setOnClickListener {
            category="all"
            binding.noti.visibility=View.INVISIBLE
            binding.notiText.visibility=View.INVISIBLE
            binding.quick.isChecked=false
            listViewAll("0")
            buttonColor("all")
        }
        binding.categoryAsian.setOnClickListener {
            category="asian"
            binding.noti.isChecked=false
            binding.quick.isChecked=false
            binding.noti.visibility = View.VISIBLE
            binding.notiText.visibility = View.VISIBLE
            if(cateList.contains("asians")) {
                binding.noti.isChecked=true

            }
            listView("asian","0")
            buttonColor("asian")
        }
        binding.categoryBun.setOnClickListener {
            binding.noti.isChecked=false
            listView("bun","0")
            category="bun"
            binding.noti.visibility = View.VISIBLE
            binding.notiText.visibility = View.VISIBLE
            if(cateList.contains("bun")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            buttonColor("bun")
        }
        binding.categoryChicken.setOnClickListener {
            binding.noti.isChecked=false
            category = "chicken"
            binding.noti.visibility = View.VISIBLE
            binding.notiText.visibility = View.VISIBLE
            if(cateList.contains("chicken")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("chicken","0")
            buttonColor("chicken")
        }
        binding.categoryPizza.setOnClickListener {
            binding.noti.isChecked=false
            category = "pizza"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("pizza")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("chicken","0")
            buttonColor("pizza")
        }
        binding.categoryFast.setOnClickListener {
            binding.noti.isChecked=false
            category = "fastfood"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("fastfood")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("fastfood","0")
            buttonColor("fast")
        }
        binding.categoryJap.setOnClickListener {
            binding.noti.isChecked=false
            category = "japan"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("japan")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("japan","0")
            buttonColor("japan")
        }
        binding.categoryKor.setOnClickListener {
            binding.noti.isChecked=false
            category = "korean"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("korean")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("korean","0")
            buttonColor("korean")
        }
        binding.categoryDo.setOnClickListener {
            binding.noti.isChecked=false
            category = "bento"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("bento")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("bento","0")
            buttonColor("bento")
        }
        binding.categoryCafe.setOnClickListener {
            binding.noti.isChecked=false
            category = "cafe"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("cafe")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
            listView("cafe","0")
            buttonColor("cafe")
        }
        binding.categoryChi.setOnClickListener {
            binding.noti.isChecked=false
            category = "chi"
            binding.noti.visibility=View.VISIBLE
            binding.notiText.visibility=View.VISIBLE
            if(cateList.contains("chi")) {
                binding.noti.isChecked=true
            }
            binding.quick.isChecked=false
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
                    binding.notiText.setTextColor(Color.parseColor("#FD5401"))
                    FBRef.notificationRef.child(category).child(auth.currentUser!!.uid).setValue(token)
                    FBRef.usersRef.child(auth.currentUser!!.uid).child("category").child(category).setValue("1")
                }
                else{
                    binding.notiText.setTextColor(Color.parseColor("#C4C4C4"))
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
                binding.quickText.setTextColor(Color.parseColor("#FD0191"))
            }else{
                if(category=="all"){
                    listViewAll("0")
                }else {
                    listView(category,"0")
                }
                binding.quickText.setTextColor(Color.parseColor("#C4C4C4"))
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
        //내가 설정한 위치 받아오기
        var lat=""
        var lng=""
        val schRef:DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if(item.set=="1") {
                            lat = item.lat
                            lng = item.lng
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        var cnt=0
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
                                    if(item.lat.toDouble()<= lat.toDouble()+0.005
                                        &&item.lat.toDouble()>=lat.toDouble()-0.005
                                        &&item.lng.toDouble()<= lng.toDouble()+0.005
                                        &&item.lng.toDouble()>=lng.toDouble()-0.005){
                                items.add(item!!)
                                cnt+=1
                                itemsKeyList.add(data.key.toString())}
                            }
                        }else{
                            if (category == item.category) {
                                if(item.lat.toDouble()<= lat.toDouble()+0.005
                                    &&item.lat.toDouble()>=lat.toDouble()-0.005
                                    &&item.lng.toDouble()<= lng.toDouble()+0.005
                                    &&item.lng.toDouble()>=lng.toDouble()-0.005){
                                items.add(item!!)
                                cnt+=1
                                itemsKeyList.add(data.key.toString())}
                            }
                        }
                    }

                    adapter.notifyDataSetChanged()
                }
//                itemsKeyList.reverse()
//                items.reverse()
                binding.count.text=cnt.toString()
                Log.d("bun1",items.toString())

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

//        //새글알림 설정 확인 후 스위치 바꾸기
//        val cateRef:DatabaseReference = database.getReference("users")
//            .child(auth.currentUser!!.uid).child("category")
//        cateRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(data in snapshot.children){
//                    if(data!=null){
//                       if(data.key==category){
//                           if(data.value=="1"){
//                               Log.d("data_",data.key.toString()+category)
//                                binding.noti.isChecked=true
//                               break
//                           }
//                       }
//
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })


    }
    private fun listViewAll(quick:String){
        //내가 설정한 위치 받아오기
        var lat=""
        var lng=""
        val schRef:DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if(item.set=="1"){
                            lat=item.lat
                            lng=item.lng
                            Log.d("lng",lng)
                            break
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        var cnt=0
        val boardRef : DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        //Log.d("item_right",item.toString())
                            if(quick=="1") {
                                if(item.quick=="1") {
                                    if(item.lat.toDouble()<= lat.toDouble()+0.005
                                        &&item.lat.toDouble()>=lat.toDouble()-0.005
                                        &&item.lng.toDouble()<= lng.toDouble()+0.005
                                        &&item.lng.toDouble()>=lng.toDouble()-0.005){
                                    items.add(item!!)
                                    cnt+=1
                                    Log.d("lat, lng",lat+lng)
                                    itemsKeyList.add(data.key.toString())}
                                }
                            }else{
                                Log.d("item_right","호출")
                                Log.d("item_right",lat+" "+lng)
                                if(item.lat.toDouble()<= lat.toDouble()+0.005
                                    &&item.lat.toDouble()>=lat.toDouble()-0.005
                                    &&item.lng.toDouble()<= lng.toDouble()+0.005
                                    &&item.lng.toDouble()>=lng.toDouble()-0.005){
                                        items.add(item!!)
                                        Log.d("item_right",item.toString())
                                        cnt+=1
                                        Log.d("lat, lng",lat+" "+lng)
                                        itemsKeyList.add(data.key.toString())}
                            }
                    }
                    binding.count.text=cnt.toString()
                    adapter.notifyDataSetChanged()
                }
                //itemsKeyList.reverse()
                Log.d("items_before",items.toString())
                //items.reverse()
                Log.d("items_after",items.toString())
                Log.d("bun1",items.toString())

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


}
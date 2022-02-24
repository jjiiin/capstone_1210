package com.capstone_design.a1209_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.databinding.ActivityAddressSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddressSearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddressSearchBinding
    private lateinit var auth: FirebaseAuth
    val dataModelList = mutableListOf<addressData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_address_search)
        val database = Firebase.database
        auth=Firebase.auth

        binding.searchBtn.setOnClickListener {
            openActivityForResult()
        }

        binding.mapSearch.setOnClickListener {
            val intent = Intent(this, MylocSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        val rv = findViewById<RecyclerView>(R.id.addressRV)
        val rvAdapter = RVAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(this)
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        val schRef =database.getReference("users").child(auth.currentUser!!.uid).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                for (DataModel in snapshot.children) {
                    dataModelList.add(DataModel.getValue(addressData::class.java)!!)

                }
                rvAdapter.notifyDataSetChanged()
                Log.d("data",dataModelList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        rvAdapter.setItemClickListener(object:RVAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭시 BoardWriteActivity로 돌아가서 주소 전달하기
                val item=dataModelList[position]
                val address=item.address+" "+item.detail
                val latitude=item.lat.toDouble()
                val longitude=item.lng.toDouble()
                BWA(address,latitude,longitude)
                val len : Int=dataModelList.size
                for(i in 0 until len){
                    //모두 해제하기
                    FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("address")
                        .child(dataModelList[i].name).child("set").setValue("0")
                }
                //주소 설정하기
                FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("address")
                    .child(item.name).child("set").setValue("1")
                val setExtra=intent.getStringExtra("mhf")
                if(setExtra=="1"){
                    Log.d("setExtra","호출됨")
                    mainReturn()
                }else{
                    BWA(item.address+" "+item.detail,latitude,longitude)
                }
            }
        })
    }
    fun BWA(address:String,lat:Double,lng:Double){
        val intent= Intent(this, BoardWirteActivity::class.java)
            .putExtra("address",address)
            .putExtra("위도",lat)
            .putExtra("경도",lng)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun mainReturn(){
        val intent= Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    fun openActivityForResult() {
        val intent = Intent(this, WebSearchActivity::class.java)
        startActivityForResult(intent, 123)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            var lat = data?.getStringExtra("location")
            var lang = data?.getStringExtra("latlang")
            Toast.makeText(this,lat+lang,Toast.LENGTH_LONG).show()
        }

    }
}
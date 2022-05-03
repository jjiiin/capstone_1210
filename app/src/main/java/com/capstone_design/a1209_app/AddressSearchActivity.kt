package com.capstone_design.a1209_app

import com.capstone_design.a1209_app.Adapter.RVAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private var page=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_address_search)
        val database = Firebase.database
        auth=Firebase.auth

//        binding.searchBtn.setOnClickListener {
//            openActivityForResult()
//        }
        page=intent.getStringExtra("page").toString()
        val pageReturn=intent.getStringExtra("page_return").toString()

        binding.mapSearch.setOnClickListener {
            val intent = Intent(this, MylocSearchActivity::class.java)
                .putExtra("page",page)
            startActivity(intent)
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

        rvAdapter.setItemClickListener(object: RVAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭시 BoardWriteActivity로 돌아가서 주소 전달하기
                val item=dataModelList[position]
                finish()
                val len : Int=dataModelList.size
                for(i in 0 until len){
                    //모두 해제하기
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(auth.currentUser!!.uid).child("address")
                        .child(dataModelList[i].name).child("set").setValue("0")
                }
                //주소 설정하기
                FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("address")
                    .child(item.name).child("set").setValue("1")

                Log.d("page",page)
                if(page=="MapHomeFragment"){
                    val intent = Intent(this@AddressSearchActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                if(page=="BoardWirteActivity"){
                    val intent = Intent(this@AddressSearchActivity, BoardWirteActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)

                }
                finish()

            }
        })
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            var lat = data?.getStringExtra("location")
            var lang = data?.getStringExtra("latlang")
            //Toast.makeText(this,lat+lang,Toast.LENGTH_LONG).show()
        }

    }
}
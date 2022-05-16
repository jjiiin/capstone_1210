package com.capstone_design.a1209_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailAddressActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val dataModelList = mutableListOf<addressData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_address)


        auth= Firebase.auth
        var addressTv:TextView=findViewById(R.id.addressTv)
        var detailEv: EditText=findViewById(R.id.detailEv)
        var nameEv:EditText=findViewById(R.id.nameEv)
        var saveBtn: Button =findViewById(R.id.saveBtn)

        addressTv.text=intent.getStringExtra("주소").toString()
        val address=intent.getStringExtra("주소").toString()
        val lat= intent.getDoubleExtra("위도",0.0)
        val lang= intent.getDoubleExtra("경도",0.0)
        val page=intent.getStringExtra("page").toString()

        val database = Firebase.database
        val schRef =database.getReference("users").child(auth.currentUser!!.uid).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    dataModelList.add(DataModel.getValue(addressData::class.java)!!)

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        Log.d("dataModel",dataModelList.toString())

        saveBtn.setOnClickListener {
            val detail=detailEv.text.toString()
            val name=nameEv.text.toString()
            //나머지 다 set=0으로 만들기
            val len : Int=dataModelList.size
            Log.d("len",len.toString())
            for(i in 0 until len){
                //모두 해제하기
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(auth.currentUser!!.uid).child("address")
                    .child(dataModelList[i].name).child("set").setValue("0")
            }
            Log.d("detailAdress",dataModelList.toString())
            val model= addressData(
                    address,detail,name,lat.toString(),lang.toString(), "1"
                )
            FBRef.usersRef.child(auth.currentUser!!.uid).child("address").child(name).setValue(model)
            if(page=="MapHomeFragment") {
                val intent = Intent(this, MainActivity::class.java)
                    .putExtra("page", page)
                Log.d("DAA", page)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            else if(page=="BoardWriteActivity"){
                val intent = Intent(this, AddressSearchActivity::class.java)
//                Log.d("DAA", page)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }else{
                finish()
            }
            }
        }
    }




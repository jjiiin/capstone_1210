package com.capstone_design.a1209_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone_design.a1209_app.dataModels.AccountData
import com.capstone_design.a1209_app.databinding.ActivityMypageAccountBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class Mypage_Account_Activity : AppCompatActivity() {

    lateinit var binding: ActivityMypageAccountBinding
    val items = mutableListOf<AccountData>()
    lateinit var rvAdapter: Account_RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_account)

        val rv = binding.rvMypageAccount
        rvAdapter = Account_RVAdapter(items)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        binding.btnDone.setOnClickListener {

            val bankName: String = binding.tvBankName.text.toString()
            val receiverName: String = binding.tvReceiverName.text.toString()
            val accountNum: String = binding.tvAccountNum.text.toString()

            val accountData = AccountData(bankName, receiverName, accountNum)
            FBRef.usersRef.child(Auth.current_uid).child("account").setValue(accountData)

            binding.tvBankName.setText("")
            binding.tvReceiverName.setText("")
            binding.tvAccountNum.setText("")
        }

        //계좌 목록 불러오기
        getAccountData()
    }

    fun getAccountData() {
        FBRef.usersRef.child(Auth.current_uid).child("account")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    items.clear()
                   if(snapshot.getValue() != null){
                       val data = snapshot.getValue<AccountData>()
                       items.add(data!!)
                   }
                    rvAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
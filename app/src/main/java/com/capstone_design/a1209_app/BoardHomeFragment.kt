package com.capstone_design.map_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.board.LvAdpater
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.FragmentBoardHomeBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class BoardHomeFragment : Fragment() {
    private  lateinit var binding : FragmentBoardHomeBinding

    private var cnt=0
    private val items= mutableListOf<dataModel>()
    private val itemsKeyList= mutableListOf<String>()

    private lateinit var auth: FirebaseAuth
    val database = Firebase.database
    val myRef = database.getReference("BoardWirte")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_board_home, container, false)

        val adapter= LvAdpater(items)
        binding.count.text=items.size.toString()

        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for(data in dataSnapshot.children){
                    val item=data.getValue(dataModel::class.java)

                    items.add(item!!)


                    itemsKeyList.add(data.key.toString())

                }
                itemsKeyList.reverse()
                items.reverse()
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.addValueEventListener(postListener)



        //데이터를 파이어베이스에서 불러오기-맨 아래 함수 정의하고 호출하였음.
        //getFBBoardData()


        binding.LvMain.adapter=adapter

        binding.LvMain.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, DetailActivity::class.java)
            //firebase에 있는 board에 대한 데이터의 id를 가져오기
            intent.putExtra("key",itemsKeyList[position])
            Log.d("key",itemsKeyList[position])
            startActivity(intent)
        }
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWirteActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}
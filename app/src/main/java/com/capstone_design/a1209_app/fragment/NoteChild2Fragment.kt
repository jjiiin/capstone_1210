package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone_design.a1209_app.R
import Adapter.RVKWAdapter
import com.capstone_design.a1209_app.dataModels.kwNotiData
import com.capstone_design.a1209_app.databinding.FragmentNoteChild2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class NoteChild2Fragment : Fragment() {
    private var dataModelList = mutableListOf<kwNotiData>()
    private var kwnotiHash=HashSet<kwNotiData>()
    private lateinit var binding: FragmentNoteChild2Binding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_note_child2, container, false)

        val database = Firebase.database
        auth= Firebase.auth

        val rv = binding.rvKWNoti
        val rvAdapter = RVKWAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(requireActivity().getApplicationContext())
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        //kwNoti에서 hashset으로 걸러준 다음 rv에 보여주기
        val schRef =database.getReference("users").child(auth.currentUser!!.uid).child("kwNoti")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                kwnotiHash.clear()
                for (DataModel in snapshot.children) {
                    var item=DataModel.getValue(kwNotiData::class.java)!!
                    if(item!=null) {
                        kwnotiHash.add(item)
                    }
                }
                for( i in kwnotiHash){
                    if(i!=null) {
                        dataModelList.add(i)
                    }
                }

                rvAdapter.notifyDataSetChanged()
                //dataModelList.reverse()
                Log.d("data",dataModelList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

       return binding.root
    }



}
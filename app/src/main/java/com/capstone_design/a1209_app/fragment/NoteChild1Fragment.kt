package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.RVAdapter
import com.capstone_design.a1209_app.RVNoteAdapter
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.kwNotiData
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.databinding.FragmentNoteChild1Binding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class NoteChild1Fragment : Fragment() {
    private var dataModelList = mutableListOf<notiData>()
    private lateinit var binding:FragmentNoteChild1Binding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_note_child1, container, false)

       //새글알림 rvNoti
        val database = Firebase.database
        auth= Firebase.auth

        val rv = binding.rvNote
        val rvAdapter = RVNoteAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(requireActivity().getApplicationContext())
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        val schRef =database.getReference("users").child(auth.currentUser!!.uid).child("newNoti")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                for (DataModel in snapshot.children) {
                    var item=DataModel.getValue(notiData::class.java)!!
                    if(item !=null) {
                        dataModelList.add(item)
                    }
                }
                rvAdapter.notifyDataSetChanged()
                dataModelList.reverse()
                Log.d("data",dataModelList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        return binding.root
    }

}
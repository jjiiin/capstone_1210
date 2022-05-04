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
import com.capstone_design.a1209_app.Adapter.RVNoteAdapter
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.databinding.FragmentNoteChild1Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class NoteChild1Fragment : Fragment() {
    private var dataModelList = mutableListOf<notiData>()
    private lateinit var binding: FragmentNoteChild1Binding
    private lateinit var auth: FirebaseAuth
    //알람 고유키 저장
    private var dataKeyList = mutableListOf<String>()

    companion object {
        var rvAdapter: RVNoteAdapter? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_child1, container, false)

        //새글알림 rvNoti
        val database = Firebase.database
        auth = Firebase.auth

        val rv = binding.rvNote
        rvAdapter = RVNoteAdapter(dataModelList, requireContext(), dataKeyList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(requireActivity().getApplicationContext())
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        val schRef = database.getReference("users").child(auth.currentUser!!.uid).child("newNoti")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                dataKeyList.clear()
                for (DataModel in snapshot.children) {
                    var item = DataModel.getValue(notiData::class.java)!!
                    if (item != null) {
                        dataModelList.add(item)
                        //알림 고유키 저장
                        dataKeyList.add(DataModel.key.toString())
                    }
                }
                rvAdapter!!.notifyDataSetChanged()
                //dataModelList.reverse()
                Log.d("data", dataModelList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



        return binding.root
    }

//    fun detectCheck() {
//        if (NoteFragment.isDeleteBtnClick == 0) {
//            //아이템들 체크버튼 안보이게
//            rvAdapter!!.updateCheckBox(false)
//            rvAdapter!!.notifyDataSetChanged()
//        } else {
//            //아이템들 체크버튼 보이게
//            rvAdapter!!.updateCheckBox(true)
//            rvAdapter!!.notifyDataSetChanged()
//        }
//    }
}
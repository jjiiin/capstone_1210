package com.capstone_design.a1209_app.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.board.LvAdpater
import com.capstone_design.a1209_app.dataModel
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.MapHomeFragment
import com.capstone_design.map_test.BoardHomeFragment
import com.capstone_design.map_test.FragmentListener


class HomeFragment : Fragment() , FragmentListener {

    private lateinit var binding : FragmentHomeBinding
    var board=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)



        childFragmentManager.beginTransaction()
            .replace(R.id.home , MapHomeFragment()).commit();


        return binding.root
    }

    override fun onReceivedData(data: Int) {
        if(data==1){
            childFragmentManager.beginTransaction()
                .replace(R.id.home , BoardHomeFragment()).commit();
        }
    }


}



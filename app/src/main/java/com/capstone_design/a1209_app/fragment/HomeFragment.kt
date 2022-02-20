package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
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



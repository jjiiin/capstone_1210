package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
import com.capstone_design.a1209_app.databinding.FragmentMyBinding


class MyFragment : Fragment() {
    private lateinit var binding : FragmentMyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_my, container, false)

        binding.chatTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_myFragment_to_chatFragment)
        }
        binding.noteTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_myFragment_to_noteFragment)
        }
        binding.homeTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_myFragment_to_homeFragment)
        }
        return binding.root
    }

}
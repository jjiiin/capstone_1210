package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.FragmentChatBinding
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding


class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_chat, container, false)

        binding.homeTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }
        binding.noteTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_noteFragment)
        }
        binding.myTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_myFragment)
        }
        return binding.root
    }

}
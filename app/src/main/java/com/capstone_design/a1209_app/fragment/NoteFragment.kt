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
import com.capstone_design.a1209_app.databinding.FragmentNoteBinding

class NoteFragment : Fragment() {
    private lateinit var binding : FragmentNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_note, container, false)

        binding.homeTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteFragment_to_homeFragment)
        }
        binding.chatTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteFragment_to_chatFragment)
        }
        binding.myTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteFragment_to_myFragment)
        }
        return binding.root


    }

}
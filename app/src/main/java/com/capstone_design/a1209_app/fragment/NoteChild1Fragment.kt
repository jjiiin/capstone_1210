package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone_design.a1209_app.R


class NoteChild1Fragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //1. 카테고리 별 새글 알림->카테고리 새글알림을 설정해놓은 사람에게만
        //token값을 유저정보에 저장함



        return inflater.inflate(R.layout.fragment_note_child1, container, false)
    }

}
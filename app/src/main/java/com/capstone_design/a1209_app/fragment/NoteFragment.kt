package com.capstone_design.a1209_app.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.capstone_design.a1209_app.*
import com.capstone_design.a1209_app.Adapter.RVNoteAdapter
import com.capstone_design.a1209_app.chat.ChatList_RVAdapter
import com.capstone_design.a1209_app.KeywordSettingActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.Adapter.ViewPagerAdapter
import com.capstone_design.a1209_app.databinding.FragmentNoteBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val click=0

    //몇 번째 탭이 선택됐는지
    var selectedTab: Int = 0
    var isClick = 0
    var isClick2 = 0
    var isClick3 = 0

    companion object {
        //휴지통 버튼 눌린것에따라 내부 프래그먼트에서 처리해주기위해
        var isDeleteBtnClick = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)

        tabLayout = binding.tabs
        viewPager = binding.viewpager

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        //휴지통 버튼 클릭할때마다 이미지 변경
        binding.deleteBtn.setOnClickListener {
            if (isDeleteBtnClick == 0) {
                binding.deleteBtn.setImageResource(R.drawable.trash_open)
                binding.deleteText.visibility = View.VISIBLE
                binding.kwplus.visibility = View.INVISIBLE
                isDeleteBtnClick = 1
                if (selectedTab == 0) {
                    NoteChild1Fragment().detectCheck()
                } else {
                    NoteChild2Fragment().detectCheck()
                }
            } else {
                binding.deleteBtn.setImageResource(R.drawable.trash_round)
                binding.deleteText.visibility = View.INVISIBLE
                binding.kwplus.visibility = View.VISIBLE
                isDeleteBtnClick = 0
                if (selectedTab == 0) {
                    NoteChild1Fragment().detectCheck()
                } else {
                    NoteChild2Fragment().detectCheck()
                }
            }
        }

        //휴지통 아이콘 눌렀을 때 보이는 "나가기" 글자를 눌렀을때 이벤트
        binding.deleteText.setOnClickListener {
            if (RVNoteAdapter.checked_noti_List.isNotEmpty()) {
                //알람 삭제하기
                for (key in RVNoteAdapter.checked_noti_List) {
                    FBRef.usersRef.child(Auth.current_uid).child("newNoti").child(key).removeValue()
                    FBRef.usersRef.child(Auth.current_uid).child("kwNoti").child(key).removeValue()
                }
            }
            //휴지통 버튼 클릭 해제하기
            binding.deleteBtn.setImageResource(R.drawable.trash_round)
            binding.deleteText.visibility = View.INVISIBLE
            isDeleteBtnClick = 0
            if (selectedTab == 0) {
                NoteChild1Fragment().detectCheck()
            } else {
                NoteChild2Fragment().detectCheck()
            }
        }

        val tabName = arrayOf<String>("알림", "키워드알림")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName[position].toString()
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                selectedTab = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        binding.kwplus.setOnClickListener {
            //키워드 설정 페이지로 넘어가기
            val intent = Intent(context, KeywordSettingActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}
package com.capstone_design.a1209_app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.capstone_design.a1209_app.KeywordSettingActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.Adapter.ViewPagerAdapter
import com.capstone_design.a1209_app.databinding.FragmentNoteBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class NoteFragment : Fragment() {
    private lateinit var binding : FragmentNoteBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_note, container, false)

        tabLayout=binding.tabs
        viewPager=binding.viewpager

        val adapter= ViewPagerAdapter(this)
        viewPager.adapter=adapter

        val tabName= arrayOf<String>("알림","키워드알림")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName[position].toString()
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        { override fun onTabSelected(tab: TabLayout.Tab?)
        { viewPager.currentItem = tab!!.position }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
        binding.kwplus.setOnClickListener {
            //키워드 설정 페이지로 넘어가기
            val intent = Intent(context, KeywordSettingActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}
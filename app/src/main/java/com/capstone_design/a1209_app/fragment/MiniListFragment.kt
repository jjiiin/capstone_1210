package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.Adapter.LvAdpater
import android.content.Intent
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.FragmentMiniListBinding
import com.capstone_design.map_test.FragmentListener


class MiniListFragment : Fragment(), FragmentListener {

    private  lateinit var binding : FragmentMiniListBinding
    private lateinit var adapter: LvAdpater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_list, container, false)
        var result : ArrayList<dataModel> = arguments?.getParcelableArrayList<dataModel>("list") as ArrayList<dataModel>
        var keyList: ArrayList<String> = arguments?.getStringArrayList("keyList") as ArrayList<String>
        var items= mutableListOf<dataModel>()
        var cnt=0
        var first=1
        if(first==1) {
            for (i in result) {
                items.add(i)
                cnt += 1
            }
            first+=1
            //목록 결과 숫자
            binding.count.text=cnt.toString()
        }
        //뒤로가기 화살표 누르면 MHF 나오기
        binding.goMhf.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.home , MapHomeFragment()).commit()
        }

        //목록 부분에 주소
        var addressTitle:String = items[0].placeAddress
        binding.miniAddressTitle.text=addressTitle

        //바로주문만 보기
        binding.quick.setOnCheckedChangeListener { buttonView, isChecked ->
            items.clear()
            cnt=0
            if(isChecked){
                Log.d("isChecked","1")
                for (i in result) {
                    if(i.quick=="1") {
                        items.add(i)
                        cnt += 1
                    }
                }
                binding.quickText.setTextColor(Color.parseColor("#FD0191"))
            }else{
                for (i in result) {
                    items.add(i)
                    cnt += 1
                }
                binding.quickText.setTextColor(Color.parseColor("#C4C4C4"))
            }
            adapter.notifyDataSetChanged()
            binding.count.text=cnt.toString()
        }


        adapter = LvAdpater(items, requireContext()!!, keyList)
        binding.LvMain.adapter=adapter

        binding.LvMain.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, DetailActivity::class.java)
            //firebase에 있는 board에 대한 데이터의 id를 가져오기
            intent.putExtra("key", keyList[position])
            Log.d("key_list", keyList[position])
            startActivity(intent)
        }


        return binding.root
    }

    override fun onReceivedData(data: Int) {
        TODO("Not yet implemented")
    }


}
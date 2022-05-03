package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.databinding.FragmentHomeBinding
import com.capstone_design.map_test.FragmentListener
import java.io.Serializable


class HomeFragment : Fragment() , FragmentListener {

    private lateinit var binding : FragmentHomeBinding
    var board=false
    private var itemsList= arrayListOf<String>()

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
        if(data==2){
                var fragment2 = MiniListFragment()
                var bundle = Bundle()
                bundle.putSerializable("list", itemsList as Serializable)
                Log.d("homeF",itemsList.toString())
//                fragment2.arguments = bundle
//                childFragmentManager
//                    .beginTransaction()
//                    .addToBackStack(null)
//                    .replace(R.id.home , fragment2)
//                    .commit();
        }
    }



}



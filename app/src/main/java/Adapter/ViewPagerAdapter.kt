package Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone_design.a1209_app.fragment.NoteChild1Fragment
import com.capstone_design.a1209_app.fragment.NoteChild2Fragment

class ViewPagerAdapter (fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int =2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> NoteChild1Fragment()
            else-> NoteChild2Fragment()
        }
    }


}
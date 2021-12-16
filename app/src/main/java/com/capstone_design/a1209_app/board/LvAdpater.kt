package com.capstone_design.a1209_app.board

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModel
import org.w3c.dom.Text

class LvAdpater(private val boardList:MutableList<dataModel>):BaseAdapter() {

    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(position: Int): Any {
        return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var converView=convertView

        if(converView==null){
            converView=LayoutInflater.from(parent?.context).inflate(R.layout.listview_item,parent,false)
        }
//        val cv_title : TextView= converView!!.findViewById(R.id.item_title)
//        //val cv_img=converView!!.findViewById(R.id.item_image)
//        val cv_place: TextView=converView!!.findViewById(R.id.item_place)
//        val cv_fee: TextView=converView!!.findViewById(R.id.item_fee)
//        val cv_time: TextView=converView!!.findViewById(R.id.item_time)
//        val cv_person: TextView=converView!!.findViewById(R.id.item_person)


        val cv_title=converView!!.findViewById<TextView>(R.id.item_title)
        val cv_img=converView!!.findViewById<ImageView>(R.id.item_image)
        val cv_place=converView!!.findViewById<TextView>(R.id.item_place)
        val cv_fee=converView!!.findViewById<TextView>(R.id.item_fee)
        val cv_time=converView!!.findViewById<TextView>(R.id.item_time)
        val cv_person=converView!!.findViewById<TextView>(R.id.item_person)

        val content:dataModel=boardList[position]
        var t=0
        if(content.title==null){
            t=1
        }
        Log.e("LvAdapter", t.toString())
        cv_title.text=content.title

        when(content.category){
            "asian"->cv_img.setImageResource(R.drawable.asian)
            "bun"->cv_img.setImageResource(R.drawable.bun)
            "bento"->cv_img.setImageResource(R.drawable.bento)
            "chicken"->cv_img.setImageResource(R.drawable.chicken)
            "pizza"->cv_img.setImageResource(R.drawable.pizza)
            "fastfood"->cv_img.setImageResource(R.drawable.fastfood)
            "japan"->cv_img.setImageResource(R.drawable.japan)
            "korean"->cv_img.setImageResource(R.drawable.korean)
            "cafe"->cv_img.setImageResource(R.drawable.cafe)
            "chi"->cv_img.setImageResource(R.drawable.china)
        }

        cv_place.text=content.place
        cv_fee.text=content.fee
        cv_time.text=content.time
        cv_person.text=content.person

        return converView!!
    }
}
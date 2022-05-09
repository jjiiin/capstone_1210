package com.capstone_design.a1209_app.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.MyWrittenActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyWrittenLVAdapter (private val boardList: MutableList<dataModel>, val context:MyWrittenActivity) :
    BaseAdapter() {
    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(p0: Int): Any {
        return boardList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var converView = p1

        if (converView == null) {
            converView =
                LayoutInflater.from(p2?.context).inflate(R.layout.listview_item, p2, false)
        }
        // 왜 커밋이 안되지
        val cv_title = converView!!.findViewById<TextView>(R.id.item_title)
        val cv_img = converView!!.findViewById<ImageView>(R.id.item_image)
        val cv_place = converView!!.findViewById<TextView>(R.id.item_place)
        val cv_fee = converView!!.findViewById<TextView>(R.id.item_fee)
        val cv_time = converView!!.findViewById<TextView>(R.id.item_time)
        val cv_person = converView!!.findViewById<TextView>(R.id.item_person)
        val cv_quick = converView!!.findViewById<ImageView>(R.id.quick)
        val tv_current_userNum = converView.findViewById<TextView>(R.id.tv_current_userNum)
        val tv_maximum_userNum = converView.findViewById<TextView>(R.id.tv_maximum_userNum)
        val card_view_layout = converView.findViewById<LinearLayout>(R.id.card_view_layout)

        val content: dataModel = boardList[p0]
        var t = 0
        if (content.title == null) {
            t = 1
        }
        Log.e("LvAdapter", t.toString())
        cv_title.text = content.title



        if (content.image == "0") {
            when (content.category) {
                "asian" -> cv_img.setImageResource(R.drawable.asian)
                "bun" -> cv_img.setImageResource(R.drawable.bun)
                "bento" -> cv_img.setImageResource(R.drawable.bento)
                "chicken" -> cv_img.setImageResource(R.drawable.chicken)
                "pizza" -> cv_img.setImageResource(R.drawable.pizza)
                "fastfood" -> cv_img.setImageResource(R.drawable.fastfood)
                "japan" -> cv_img.setImageResource(R.drawable.japan)
                "korean" -> cv_img.setImageResource(R.drawable.korean)
                "cafe" -> cv_img.setImageResource(R.drawable.cafe)
                "chi" -> cv_img.setImageResource(R.drawable.china)
            }
        } else {
            var imageUri = content.image
            Glide.with(context).load(imageUri).into(cv_img)
        }

        if (content.quick == "1") {
            cv_quick.visibility = View.VISIBLE
        } else {
            cv_quick.visibility = View.INVISIBLE
        }

        cv_place.text = content.place
        cv_fee.text = content.fee
        cv_time.text = content.time
        //cv_person.text=content.person

        //모집정원
        tv_maximum_userNum.text = content.person
        getUserNum(content.chatroomkey, tv_current_userNum)
        //isClosed(content.chatroomkey, card_view_layout, converView)
        return converView!!
    }
    fun getUserNum(chatroomkey: String, tv_current_userNum: TextView) {
        FBRef.chatRoomsRef.child(chatroomkey).child("users")
            .addValueEventListener(object : ValueEventListener {
                var num = 0
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        num++
                    }
                    tv_current_userNum.text = num.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}

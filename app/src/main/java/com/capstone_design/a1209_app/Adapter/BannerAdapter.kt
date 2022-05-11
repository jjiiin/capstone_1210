package com.capstone_design.a1209_app.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.fragment.MapHomeFragment
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.map_test.FragmentListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class bannerAdapter(
    val items: MutableList<dataModel>,
    private val fragment: MapHomeFragment,
    val context: Context
) : RecyclerView.Adapter<bannerAdapter.ViewHolder>() {
    private lateinit var mFragmentListener: FragmentListener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val place: TextView = itemView.findViewById(R.id.item_place)
        val time: TextView = itemView.findViewById(R.id.item_time)
        val fee: TextView = itemView.findViewById(R.id.item_fee)
       // val person: TextView = itemView.findViewById(R.id.item_person)
        val img: ImageView = itemView.findViewById(R.id.item_image)
        val quick: ImageView = itemView.findViewById(R.id.quick)

        val listbtn: ImageView = itemView.findViewById(R.id.listBtn)

        val tv_current_userNum = itemView.findViewById<TextView>(R.id.tv_current_userNum)
        val tv_maximum_userNum = itemView.findViewById<TextView>(R.id.tv_maximum_userNum)
        val card_view_layout = itemView.findViewById<LinearLayout>(R.id.card_view_layout)
        val itemview = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mhf_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.place.text = item.place
        holder.time.text = item.time
        holder.fee.text = item.fee
        //holder.person.text = item.person
        //image
        if (item.image == "0") {
            when (item.category) {
                "asian" -> holder.img.setImageResource(R.drawable.asian)
                "bun" -> holder.img.setImageResource(R.drawable.bun)
                "bento" -> holder.img.setImageResource(R.drawable.bento)
                "chicken" -> holder.img.setImageResource(R.drawable.chicken)
                "pizza" -> holder.img.setImageResource(R.drawable.pizza)
                "fastfood" -> holder.img.setImageResource(R.drawable.fastfood)
                "japan" -> holder.img.setImageResource(R.drawable.japan)
                "korean" -> holder.img.setImageResource(R.drawable.korean)
                "cafe" -> holder.img.setImageResource(R.drawable.cafe)
                "chi" -> holder.img.setImageResource(R.drawable.china)
            }
        } else {
            var imageUri = item.image
            Glide.with(context).load(imageUri).into(holder.img)
            holder.img.clipToOutline = true
        }

        if (item.quick == "1") {
            holder.quick.visibility = View.VISIBLE
        } else {
            holder.quick.visibility = View.INVISIBLE
        }
        holder.listbtn.setOnClickListener {
            //소그룹 목록으로 이동하기
            //val mActivity=activity as MainActivity
            //Toast.makeText(context,items.toString(),Toast.LENGTH_LONG).show()
            btnClickListener.onClick(it, position)

        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

        //모집정원
        holder.tv_maximum_userNum.text = item.person
        getUserNum(item.chatroomkey, holder.tv_current_userNum)
        isClosed(item.chatroomkey, holder.card_view_layout, holder.itemview)
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener


    //버튼 클릭 리스너를 만들어보자
    interface OnBtnClickListener {
        fun onClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setBtnClickListener(onBtnClickListener: OnBtnClickListener) {
        this.btnClickListener = onBtnClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var btnClickListener: OnBtnClickListener


    override fun getItemCount(): Int {
        return items.size
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

    //모집 끝난 글이면 처리하는 코드
    fun isClosed(chatroomkey: String, card_view_layout: LinearLayout, converView: View) {
        FBRef.chatRoomsRef.child(chatroomkey).child("isClosed")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == true) {
                        var paint = Paint()
                        paint.setColor(Color.WHITE)
                        paint.alpha = 235
                        //카드뷰를 덮는 레이아웃에 붙투명 흰색 적용
                        card_view_layout.setBackgroundColor(paint.color)
                        //카드뷰 클릭해도 아무일도 일어나지않게
                        converView.setOnClickListener { }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }
}
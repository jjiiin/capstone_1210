package com.capstone_design.a1209_app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.fragment.MapHomeFragment
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.map_test.FragmentListener

class bannerAdapter(val items:MutableList<dataModel>, private val fragment: MapHomeFragment, val context: Context): RecyclerView.Adapter<bannerAdapter.ViewHolder>()  {
    private lateinit var mFragmentListener: FragmentListener
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title: TextView =itemView.findViewById(R.id.item_title)
        val place: TextView =itemView.findViewById(R.id.item_place)
        val time: TextView =itemView.findViewById(R.id.item_time)
        val fee: TextView =itemView.findViewById(R.id.item_fee)
        val person: TextView =itemView.findViewById(R.id.item_person)
        val img: ImageView =itemView.findViewById(R.id.item_image)
        val quick:ImageView=itemView.findViewById(R.id.quick)

        val listbtn:ImageView=itemView.findViewById(R.id.listBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.mhf_cardview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item=items[position]
        holder.title.text=item.title
        holder.place.text=item.place
        holder.time.text=item.time
        holder.fee.text=item.fee
        holder.person.text=item.person
        //image
        if(item.image=="0") {
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
        }else{
            var imageUri=item.image
            Glide.with(context).load(imageUri).into(holder.img)
            holder.img.clipToOutline=true
        }

        if(item.quick=="1"){
            holder.quick.visibility=View.VISIBLE
        }else{
            holder.quick.visibility=View.INVISIBLE
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
    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener)  {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


    //버튼 클릭 리스너를 만들어보자
    interface OnBtnClickListener{
        fun onClick(v:View,position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setBtnClickListener(onBtnClickListener: OnBtnClickListener)  {
        this.btnClickListener = onBtnClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var btnClickListener : OnBtnClickListener


    override fun getItemCount(): Int {
        return items.size
    }

}
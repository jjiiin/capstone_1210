package com.capstone_design.a1209_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.dataModel

class bannerAdapter(val items:MutableList<dataModel>): RecyclerView.Adapter<bannerAdapter.ViewHolder>()  {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title: TextView =itemView.findViewById(R.id.item_title)
        val place: TextView =itemView.findViewById(R.id.item_place)
        val time: TextView =itemView.findViewById(R.id.item_time)
        val fee: TextView =itemView.findViewById(R.id.item_fee)
        val person: TextView =itemView.findViewById(R.id.item_person)
        val img: ImageView =itemView.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.banner_item,parent,false)
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
        when(item.category){
            "asian"->holder.img.setImageResource(R.drawable.asian)
            "bun"->holder.img.setImageResource(R.drawable.bun)
            "bento"->holder.img.setImageResource(R.drawable.bento)
            "chicken"->holder.img.setImageResource(R.drawable.chicken)
            "pizza"->holder.img.setImageResource(R.drawable.pizza)
            "fastfood"->holder.img.setImageResource(R.drawable.fastfood)
            "japan"->holder.img.setImageResource(R.drawable.japan)
            "korean"->holder.img.setImageResource(R.drawable.korean)
            "cafe"->holder.img.setImageResource(R.drawable.cafe)
            "chi"->holder.img.setImageResource(R.drawable.china)
        }



    }

    override fun getItemCount(): Int {
        return items.size
    }
}
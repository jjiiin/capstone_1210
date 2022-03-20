package com.capstone_design.a1209_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.fcm.NotiModel

class RVNoteAdapter(val items:MutableList<notiData>):RecyclerView.Adapter<RVNoteAdapter.ViewHolder> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.listview_noti,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.content.text=item.content
        holder.date.text=item.date
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
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val content: TextView =itemView.findViewById(R.id.notiContent)
        val date: TextView =itemView.findViewById(R.id.notiDate)
        val img: ImageView =itemView.findViewById(R.id.item_image)
    }
}
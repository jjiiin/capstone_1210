package com.capstone_design.a1209_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.dataModels.notiData
import com.capstone_design.a1209_app.fcm.NotiModel

class RVNoteAdapter(val items: MutableList<notiData>, val context:Context) :
    RecyclerView.Adapter<RVNoteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listview_noti, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.content.text = item.content
        holder.date.text = item.date
        //image
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
            "full" -> holder.img.setImageResource(R.drawable.notification_chatroom_full)
            "enter" -> holder.img.setImageResource(R.drawable.notification_enter)
            "pay"->holder.img.setImageResource(R.drawable.notification_paid)
            "receipt"->holder.img.setImageResource(R.drawable.notification_receipt)
            "evaluation"->{
                holder.img.setImageResource(R.drawable.notification_evaluation)

            }
        }
        if (item.chatroom_title != "") {
            holder.chatroom_title.text = item.chatroom_title
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.notiContent)
        val date: TextView = itemView.findViewById(R.id.notiDate)
        val img: ImageView = itemView.findViewById(R.id.item_image)
        val chatroom_title = itemView.findViewById<TextView>(R.id.noti_chatroom_title)

        fun bind(item:notiData){
            if(item.category=="evaluation"){
                itemView.setOnClickListener {
                    val intent = Intent(context, Push_Evaluation_Activity::class.java)
                    intent.putExtra("chatroomkey", item.chatroom_key)
                    context.startActivity(intent)
                }
            }
        }
    }
}
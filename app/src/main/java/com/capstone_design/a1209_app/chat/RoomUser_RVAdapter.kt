package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.EvaluationActivity
import com.capstone_design.a1209_app.R

class RoomUser_RVAdapter(val items: MutableList<String>, val context: Context) :
    RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.roomuser_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: String) {
            val nickname = itemView.findViewById<TextView>(R.id.rv_nickname_textView)
            nickname.setText(item)

            itemView.setOnClickListener {
                val intent = Intent(context, EvaluationActivity::class.java)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }
}
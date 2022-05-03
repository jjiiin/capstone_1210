package com.capstone_design.a1209_app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.RatingData

class Rating_RVAdapter(val context: Context, val items: MutableList<RatingData>) :
    RecyclerView.Adapter<Rating_RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rating_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: RatingData) {
            val textView_nickname = itemView.findViewById<TextView>(R.id.tv_nickname)
            val content = itemView.findViewById<TextView>(R.id.tv_content)
            val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
            val textView_time = itemView.findViewById<TextView>(R.id.tv_time)

            textView_nickname.text = item.writer
            content.text = item.content
            ratingBar.rating = item.rating
            textView_time.text = item.display_time
        }
    }
}
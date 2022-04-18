package Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R

class KwRVAdapter(val items:MutableList<String>): RecyclerView.Adapter<KwRVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView =itemView.findViewById(R.id.item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.keyword_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.name.text=item

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
package Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.kwNotiData

class RVKWAdapter(val items:MutableList<kwNotiData>): RecyclerView.Adapter<RVKWAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.keywordnote_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.content.text=item.content
        holder.date.text=item.date
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val content: TextView =itemView.findViewById(R.id.content)
        val date: TextView =itemView.findViewById(R.id.date)

    }
}
package Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.addressData

class RVAdapter (val items:MutableList<addressData>): RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.address_item,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.name.text=item.name
        holder.detail.text=item.address+" "+item.detail
        if(item.set=="1"){
            holder.setCheck.visibility=View.VISIBLE//현재 설정된 주소 택하기
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

    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.name)
        val detail: TextView =itemView.findViewById(R.id.detail)
        val setCheck:ImageView=itemView.findViewById(R.id.setCheck)
    }
}
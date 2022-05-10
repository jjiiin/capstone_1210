package com.capstone_design.a1209_app.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.kwNotiData
import java.util.*

class RVKWAdapter(val items:MutableList<kwNotiData>, val keys:MutableList<String>): RecyclerView.Adapter<RVKWAdapter.ViewHolder> () {
    private var isCheckBtn_Show = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.keywordnote_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.content.text=item.content
        val key = keys[position]
        holder.bind(key)
        val currentTime = Calendar.getInstance().time
        if ((currentTime.year - item.date.year) != 0) {
            holder.date.text =
                (currentTime.year - item.date.year).toString() + "년 전"
        } else if ((currentTime.month - item.date.month) != 0) {
            holder.date.text =
                (currentTime.month - item.date.month).toString() + "달 전"
        } else if ((currentTime.date - item.date.date) != 0) {
            holder.date.text =
                (currentTime.date - item.date.date).toString() + "일 전"
        } else if ((currentTime.hours - item.date.hours) != 0) {
            holder.date.text =
                (currentTime.hours - item.date.hours).toString() + "시간 전"
        } else if ((currentTime.minutes - item.date.minutes) != 0) {
            holder.date.text =
                (currentTime.minutes - item.date.minutes).toString() + "분 전"
        } else {
            holder.date.text =
                (currentTime.seconds - item.date.seconds).toString() + "초 전"
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateCheckBox(show: Boolean) {
        isCheckBtn_Show = show
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val content: TextView =itemView.findViewById(R.id.content)
        val date: TextView =itemView.findViewById(R.id.date)

        fun bind(key:String) {
            //휴지통 버튼 눌렸으면 체크버튼 보이게
            if (isCheckBtn_Show == false) {
                itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked = false
                itemView.findViewById<CheckBox>(R.id.checkBtn).visibility = View.GONE
                RVNoteAdapter.checked_noti_List.clear()
            } else {
                //휴지통 버튼 눌렀을때는 클릭해도 채팅방으로 넘어가는것을 막음
                itemView.setOnClickListener {
                    itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked = !itemView.findViewById<CheckBox>(R.id.checkBtn).isChecked
                }
                itemView.findViewById<CheckBox>(R.id.checkBtn).visibility = View.VISIBLE
                itemView.findViewById<CheckBox>(R.id.checkBtn).setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        //체크된 알림의 키를 list에 저장
                        RVNoteAdapter.checked_noti_List.add(key)
                    } else {
                        //체크해제되면 해당 채팅방키를 체크 list에서 삭제
                        RVNoteAdapter.checked_noti_List.remove(key)
                    }

                    Log.d("체크됨", RVNoteAdapter.checked_noti_List.toString())
                }
            }
        }
    }
}
package com.capstone_design.a1209_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.dataModels.AccountData

class Account_RVAdapter(val items: MutableList<AccountData>) :
    RecyclerView.Adapter<Account_RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mypage_account_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: AccountData) {
            itemView.findViewById<TextView>(R.id.tv_bank_name).setText(item.bankName)
            itemView.findViewById<TextView>(R.id.tv_receiver_name).setText(item.receiverName)
            itemView.findViewById<TextView>(R.id.tv_account_num).setText(item.accountNum)
        }
    }
}
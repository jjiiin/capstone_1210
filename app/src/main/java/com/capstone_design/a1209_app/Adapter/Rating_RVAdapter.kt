package com.capstone_design.a1209_app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.RatingData
import com.capstone_design.a1209_app.utils.Auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

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
            val uid = item.writer_uid

            textView_nickname.text = item.writer
            content.text = item.content
            ratingBar.rating = item.rating
            calculateTime(item, itemView)
            getImage(uid, itemView)
        }
    }

    fun calculateTime(item: RatingData, itemView: View) {
        val currentTime = Calendar.getInstance().time
        if ((currentTime.year - item.saved_time.year) != 0) {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.year - item.saved_time.year).toString() + "년 전"
        } else if ((currentTime.month - item.saved_time.month) != 0) {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.month - item.saved_time.month).toString() + "달 전"
        } else if ((currentTime.date - item.saved_time.date) != 0) {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.date - item.saved_time.date).toString() + "일 전"
        } else if ((currentTime.hours - item.saved_time.hours) != 0) {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.hours - item.saved_time.hours).toString() + "시간 전"
        } else if ((currentTime.minutes - item.saved_time.minutes) != 0) {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.minutes - item.saved_time.minutes).toString() + "분 전"
        } else {
            itemView.findViewById<TextView>(R.id.tv_time).text =
                (currentTime.seconds - item.saved_time.seconds).toString() + "초 전"
        }

    }

    fun getImage(uid: String, itemView: View) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("profile_img/" + uid + ".jpg").getDownloadUrl()
            .addOnSuccessListener {
                Glide.with(context).load(it).into(itemView.findViewById(R.id.image_profile))
            }.addOnFailureListener {
                itemView.findViewById<ImageView>(R.id.image_profile)
                    .setImageResource(R.drawable.profile_cat)
            }
    }
}
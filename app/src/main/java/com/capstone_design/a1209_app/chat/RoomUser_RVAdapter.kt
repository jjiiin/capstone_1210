package com.capstone_design.a1209_app.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.Evaluation_Display_Activity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RoomUser_RVAdapter(
    val items: MutableList<UserData>,
    val context: Context,
    val usersIdList: MutableList<String>
) :
    RecyclerView.Adapter<RoomUser_RVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.roomuser_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position], usersIdList[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: UserData, uid: String) {
            itemView.findViewById<TextView>(R.id.rv_nickname_textView).setText(item.nickname)
            getRating(uid)

            itemView.setOnClickListener {
                val intent =
                    Intent(context, Evaluation_Display_Activity::class.java).putExtra("uid", uid)
                        .putExtra("nickname", item.nickname)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            getImage(uid)
        }

        fun getRating(uid: String) {
            FBRef.usersRef.child(uid).child("rating")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            itemView.findViewById<TextView>(R.id.tv_rating).text = "3.5"
                        } else {
                            val rating = snapshot.value.toString()
                            itemView.findViewById<TextView>(R.id.tv_rating).text = rating
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

        fun getImage(uid:String) {
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.getReference()
            storageRef.child("profile_img/" + uid + ".jpg").getDownloadUrl()
                .addOnSuccessListener {
                    Glide.with(context).load(it).into(itemView.findViewById(R.id.rv_profile_btn))
                }.addOnFailureListener {
                    itemView.findViewById<ImageView>(R.id.rv_profile_btn).setImageResource(R.drawable.profile_cat)
                }
        }
    }
}
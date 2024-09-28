package com.minorproject.librarymanagementapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.librarymanagementapp.R
import com.minorproject.librarymanagementapp.User

class UserAdapter(private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentitem = userList[position]

            holder.name.text = currentitem.name
            holder.sid.text = currentitem.sid
            holder.gender.text = currentitem.gender
            holder.email.text = currentitem.email
            holder.course.text = currentitem.course


        }

        override fun getItemCount(): Int {
            return userList.size
        }

        class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

            val name: TextView = itemView.findViewById(R.id.tv_user_name)
            val sid : TextView = itemView.findViewById(R.id.tv_user_sid)
            val gender : TextView = itemView.findViewById(R.id.tv_user_gender)
            val email : TextView = itemView.findViewById(R.id.tv_user_email)
            val course : TextView = itemView.findViewById(R.id.tv_user_course)

        }


}
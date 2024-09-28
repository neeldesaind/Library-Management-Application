package com.minorproject.librarymanagementapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.librarymanagementapp.IssuedBooks
import com.minorproject.librarymanagementapp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ReturnBookAdapter(private val bookList: ArrayList<IssuedBooks>) :
    RecyclerView.Adapter<ReturnBookAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.returnbook_item,parent,false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = bookList[position]

        holder.bookname.text = currentitem.bname
        holder.studid.text = currentitem.sid
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.issuedate.text = simpleDateFormat.format(currentitem.issue_date)
        if(currentitem.return_date == null){
            val duration = Calendar.getInstance().timeInMillis - currentitem.issue_date!!
            val days = TimeUnit.MILLISECONDS.toDays(duration)
            if(days < 11){
                holder.fine.text = "0"
            }else{
            holder.fine.text = ((days - 10) * 5).toString()
            }
        }else{
            holder.fine.text = "0"
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        val bookname : TextView = itemView.findViewById(R.id.tvBookName3)
        val studid : TextView = itemView.findViewById(R.id.tvStudId3)
        val issuedate : TextView = itemView.findViewById(R.id.tvIssueDate3)
        val fine : TextView = itemView.findViewById(R.id.tvFine3)



    }
}
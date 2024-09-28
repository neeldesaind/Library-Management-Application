package com.minorproject.librarymanagementapp.Adapter

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


class BookTransactionAdapter(private val bookList: ArrayList<IssuedBooks>) :
    RecyclerView.Adapter<BookTransactionAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booktransaction_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = bookList[position]

        holder.bookname.text = currentitem.bname!!
        holder.studid.text = currentitem.sid!!
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.issuedate.text = simpleDateFormat.format(currentitem.issue_date!!)
        if(currentitem.return_date == null){
            holder.returndate.text = "Yet to be submitted"
            val duration = Calendar.getInstance().timeInMillis - currentitem.issue_date
            val days = TimeUnit.MILLISECONDS.toDays(duration)
            if(days < 11){
                holder.fine.text = "0"
            }else{
                holder.fine.text = ((days - 10) * 5).toString()
            }
        }else{
            holder.fine.text = "0"
            holder.returndate.text = simpleDateFormat.format(currentitem.return_date)
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        val bookname : TextView = itemView.findViewById(R.id.tvBookName2)
        val studid : TextView = itemView.findViewById(R.id.tvStudId2)
        val issuedate : TextView = itemView.findViewById(R.id.tvIssueDate2)
        val returndate : TextView = itemView.findViewById(R.id.tvReturnDate2)
        val fine : TextView = itemView.findViewById(R.id.tvFine2)


    }
}
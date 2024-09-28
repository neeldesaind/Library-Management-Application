package com.minorproject.librarymanagementapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.minorproject.librarymanagementapp.Book
import com.minorproject.librarymanagementapp.R
import com.minorproject.librarymanagementapp.Reservation

class SearchAdapter(private val bookList: ArrayList<Book>) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item,parent,false)
        return MyViewHolder(itemView)
    }

    var studid: String = ""
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = bookList[position]
        holder.isbn.text = currentitem.isbn
        holder.bookname.text = currentitem.name
        holder.stock.text = currentitem.stock.toString()
        holder.author.text = currentitem.author
        holder.publication.text = currentitem.publication
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        val isbn : TextView = itemView.findViewById(R.id.tv_search_isbn)
        val bookname : TextView = itemView.findViewById(R.id.tv_search_bname)
        val stock : TextView = itemView.findViewById(R.id.tv_search_stock)
        val author : TextView = itemView.findViewById(R.id.tv_search_author)
        val publication : TextView = itemView.findViewById(R.id.tv_search_publication)
    }
}
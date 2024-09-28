package com.minorproject.librarymanagementapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.librarymanagementapp.Book
import com.minorproject.librarymanagementapp.R

class BookAdapter(private val bookList: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = bookList[position]

        holder.isbn.text = currentitem.isbn
        holder.bookname.text = currentitem.name
        holder.author.text = currentitem.author
        holder.publication.text = currentitem.publication
        holder.stock.text = currentitem.stock.toString()


    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        val isbn: TextView = itemView.findViewById(R.id.tvisbn1)
        val bookname : TextView = itemView.findViewById(R.id.tvBookName1)
        val author : TextView = itemView.findViewById(R.id.tvAuthor1)
        val publication : TextView = itemView.findViewById(R.id.tvPublication1)
        val stock : TextView = itemView.findViewById(R.id.tvStock1)

    }
}
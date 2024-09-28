package com.minorproject.librarymanagementapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.minorproject.librarymanagementapp.Book
import com.minorproject.librarymanagementapp.R
import com.minorproject.librarymanagementapp.Reservation
import java.util.*
import kotlin.collections.ArrayList

class CustomAdapter(val book_table: ArrayList<Book>,val sid: String) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var db : DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create and return view holder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reserve_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // bind data to view holder
        val book = book_table[position]

        holder.isbn.text = book.isbn
        holder.bname.text = book.name
        db = FirebaseDatabase.getInstance().reference
        if(book.stock!! > 0){
            holder.btn.visibility = View.VISIBLE
            holder.available.text = "Yes"
            holder.btn.setOnClickListener {
                db.child("Reservation").orderByChild("sid").equalTo(sid).limitToLast(1).get().addOnCompleteListener {
                    if(it.result.exists()){
                        for (i in it.result.children){
                            if(i.hasChild("status")){
                                val res = Reservation(book.isbn,book.name,sid, Calendar.getInstance().timeInMillis)
                                val key = db.push().key.toString()
                                db.child("Reservation").child(key).setValue(res).addOnSuccessListener {
                                    Toast.makeText(holder.itemView.context, "Request for reservation sent", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(holder.itemView.context, "You already have requested", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        val res = Reservation(book.isbn,book.name,sid)
                        val key = db.push().key.toString()
                        db.child("Reservation").child(key).setValue(res).addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Applied for reservation", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }else{
            holder.btn.visibility = View.GONE
            holder.available.text = "No"
        }
    }

    override fun getItemCount(): Int {
        // return total number of items
        return book_table.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // initialize views in view holder
        val isbn: TextView = itemView.findViewById(R.id.tvisbn4)
        val bname: TextView = itemView.findViewById(R.id.tvBookName4)
        val available : TextView = itemView.findViewById(R.id.tvAvailable)
        val btn : Button = itemView.findViewById(R.id.reserve_btn)
    }
}

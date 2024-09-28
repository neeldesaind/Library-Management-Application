package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.*
import com.minorproject.librarymanagementapp.Adapter.BookAdapter
import com.minorproject.librarymanagementapp.Adapter.BookTransactionAdapter
import com.minorproject.librarymanagementapp.Adapter.ReturnBookAdapter
import com.minorproject.librarymanagementapp.Adapter.UserAdapter
import kotlinx.android.synthetic.main.activity_generatereports.*



import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class activity_generatereports : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var reportRecyclerview : RecyclerView
    private lateinit var IssuedBookArrayList : ArrayList<IssuedBooks>
    private lateinit var BookArrayList : ArrayList<Book>
    private lateinit var UserArrayList : ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generatereports)

        back.setOnClickListener {
            finish()
        }

        val spin: Spinner = findViewById(R.id.sp1)
        val arraySpinner =
            arrayOf("Select Report", "Book Transaction", "Book Reports", "Return pending","User Reports")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spin.adapter = adapter


        reportRecyclerview = rv_reports
        reportRecyclerview.layoutManager = LinearLayoutManager(this)
        reportRecyclerview.setHasFixedSize(true)

        


        generatereport_btn.setOnClickListener {
            if (sp1.selectedItemPosition == 1) {
                //IssuedBookArrayList.clear()
                IssuedBookArrayList = arrayListOf<IssuedBooks>()
                BookTransactionReport()
            }else if(sp1.selectedItemPosition == 2){
                BookArrayList = arrayListOf<Book>()
                BooksReport()
            }else if(sp1.selectedItemPosition == 3){
                IssuedBookArrayList = arrayListOf<IssuedBooks>()
                ReturnPendingReport()
            }else if(sp1.selectedItemPosition == 4){
                UserArrayList = arrayListOf<User>()
                UsersReport()
            }
        }
    }

    private fun BookTransactionReport() {
        dbref = FirebaseDatabase.getInstance().getReference("IssuedBooks")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                           val key = i.getValue(IssuedBooks::class.java)
                        IssuedBookArrayList.add(key!!)
                    }
                    reportRecyclerview.adapter = BookTransactionAdapter(IssuedBookArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun ReturnPendingReport() {
        dbref = FirebaseDatabase.getInstance().getReference("IssuedBooks")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        //if return is pending
                        if(!i.hasChild("return_date")){
                            val user = i.getValue(IssuedBooks::class.java)
                            IssuedBookArrayList.add(user!!)
                        }
                    }
                    reportRecyclerview.adapter = ReturnBookAdapter(IssuedBookArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun BooksReport() {
        dbref = FirebaseDatabase.getInstance().getReference("Book")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.e("before loop", snapshot.childrenCount.toString())
                    for (i in snapshot.children) {
                        Log.e("in loop", i.childrenCount.toString())
                        val user = i.getValue(Book::class.java)
                        BookArrayList.add(user!!)
                    }
                    reportRecyclerview.adapter = BookAdapter(BookArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun UsersReport() {
        dbref = FirebaseDatabase.getInstance().getReference("User")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val user = i.getValue(User::class.java)
                        UserArrayList.add(user!!)
                    }
                    reportRecyclerview.adapter = UserAdapter(UserArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}
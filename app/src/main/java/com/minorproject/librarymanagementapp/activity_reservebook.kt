package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.minorproject.librarymanagementapp.Adapter.CustomAdapter
import com.minorproject.librarymanagementapp.Adapter.SearchAdapter
import kotlinx.android.synthetic.main.activity_reservebook.*
import java.util.*

class activity_reservebook : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var bookRecyclerview: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var adapter1: CustomAdapter
    private lateinit var BookArrayList: ArrayList<Book>
    var studid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservebook)

        back.setOnClickListener {
            finish()
        }

        bookRecyclerview = rv_search
        bookRecyclerview.layoutManager = LinearLayoutManager(this)
        bookRecyclerview.setHasFixedSize(true)
        searchview1.clearFocus()



        BookArrayList = ArrayList()
        BooksReport1()
        searchview1.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchList1(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList1(newText.toString())
                return true
            }
        })
    }


    fun searchList1(text: String) {
        val searchList1 = ArrayList<Book>()

        for (dataClass in BookArrayList) {
            if (dataClass.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList1.add(dataClass)
            }
        }


        bookRecyclerview.adapter = CustomAdapter(searchList1,studid)
    }

    fun BooksReport() {

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
                    bookRecyclerview.adapter = CustomAdapter(BookArrayList,studid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun BooksReport1() {
        dbref = FirebaseDatabase.getInstance().getReference("Book")
        dbref.get().addOnSuccessListener{
            Log.e("before loop", it.childrenCount.toString())
            for (i in it.children) {
                Log.e("in loop", i.childrenCount.toString())
                val user = i.getValue(Book::class.java)
                BookArrayList.add(user!!)
            }
            val email = FirebaseAuth.getInstance().currentUser?.email.toString()
            Log.w("getsid","email = $email")
            dbref = FirebaseDatabase.getInstance().reference
            dbref.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
                for (i in it.children) {
                    studid = i.child("sid").value.toString()
                    Log.w("getsid1","sid = $studid")
                }
                Log.w("getsid","sid = $studid")
                bookRecyclerview.adapter = CustomAdapter(BookArrayList,studid)
            }

        }
    }


}


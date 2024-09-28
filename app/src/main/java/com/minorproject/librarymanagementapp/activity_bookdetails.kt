package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bookdetails.*
import kotlinx.android.synthetic.main.activity_bookdetails.back
import kotlinx.android.synthetic.main.activity_forgotpassword.*

class activity_bookdetails : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var bookRecyclerview: RecyclerView
    private lateinit var bookArrayList: ArrayList<IssuedBooks>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookdetails)

        back.setOnClickListener {
            finish()
        }


        bookRecyclerview = rv_bookdetails
        bookRecyclerview.layoutManager = LinearLayoutManager(this)
        bookRecyclerview.setHasFixedSize(true)

        bookArrayList = arrayListOf<IssuedBooks>()
        getBookData()

    }

    private fun getBookData() {

        dbref = FirebaseDatabase.getInstance().reference

        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
        var studid = ""
        dbref.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
            for(i in it.children){
                studid = i.child("sid").value.toString()
            }
            dbref.child("IssuedBooks").orderByChild("sid").equalTo(studid).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (i in snapshot.children) {
                            val key = i.getValue(IssuedBooks::class.java)
                            bookArrayList.add(key!!)
                        }
                        if(!bookArrayList.isNullOrEmpty()){
                            bookRecyclerview.adapter = BookDetailsAdapter(bookArrayList)
                        }else{
                            Toast.makeText(this@activity_bookdetails,"There is no record for this",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@activity_bookdetails,"There is no record for this",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }
            })
        }
    }
}
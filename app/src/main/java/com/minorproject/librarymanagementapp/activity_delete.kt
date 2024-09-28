package com.minorproject.librarymanagementapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_delete.*
import kotlinx.android.synthetic.main.activity_delete.back
import kotlinx.android.synthetic.main.activity_forgotpassword.*
import kotlinx.android.synthetic.main.activity_generatebarcode.*
import kotlinx.android.synthetic.main.activity_updatebook.sp1

class activity_delete : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    lateinit var isbns: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        back.setOnClickListener {
            finish()
        }

        val database = FirebaseDatabase.getInstance().reference.child("Book")
        isbns = mutableListOf<String>()
        //to fill the spinner with book names
        database.get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result!!.children){
                    val isbn = i.child("isbn").value?.toString()
                    if(!isbn.isNullOrEmpty()) {
                        isbns.add(isbn)
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, isbns)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    sp1.adapter = adapter

                }
            }
        }

        sp1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                database.child(sp1.selectedItem.toString()).child("name").get().addOnSuccessListener {
                    tv_delete_bname.text = it.value.toString()
                }
            }
        }

        deletebook_btn.setOnClickListener{
            if(sp1.selectedItemPosition != -1){
                val book = sp1.selectedItem.toString()
                var ad = AlertDialog.Builder(this)
                ad.setTitle("Delete")
                ad.setMessage("Are you sure you want to Remove $book?")
                ad.setPositiveButton("Yes") { dialog, id ->
                    database.child(book).removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this,"Book deleted successfully",Toast.LENGTH_SHORT).show()
                            isbns.remove(book)
                            fill()
                        }else{
                            //todo handle any error in future
                        }
                    }
                }
                ad.setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                }
                ad.show()

            }
        }

    }

    fun fill() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, isbns)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        sp1.adapter = adapter
    }
}
package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.android.synthetic.main.activity_issuesubmit.*
import java.util.*
import java.util.concurrent.TimeUnit


class activity_issuesubmit : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase //changes
    var bookname1: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issuesubmit)

        back.setOnClickListener {
            finish()
        }


        stuid_scan.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Scan barcode for student id")
            options.setCameraId(0) // Use a specific camera of the device
            options.setOrientationLocked(true)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            barcodeLauncher.launch(options)
        }

        isbn_scan.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Scan barcode for book")
            options.setDesiredBarcodeFormats(ScanOptions.EAN_13)
            options.setCameraId(0) // Use a specific camera of the device
            options.setOrientationLocked(true)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            barcodeLauncher1.launch(options)
        }

        database = FirebaseDatabase.getInstance()




        issue_btn.setOnClickListener {
            if (validate()) {
                val studid = studid.text.toString()
                val isbn = isbn.text.toString()
                getBookName(isbn)
                val db = database.reference
                //check if stock is there and also book with this name is there or not
                db.child("Book").child(isbn)
                    .get().addOnSuccessListener { bookSnapshot ->
                        if (bookSnapshot.exists()) {
                            var reservedBy: MutableList<String> = ArrayList()
                            if(bookSnapshot.hasChild("reservedBy")){
                                reservedBy = bookSnapshot.child("reservedBy").value as MutableList<String>
                            }
                            val stock = bookSnapshot.child("stock").value.toString().toInt()
                            if (stock < 1 && !(reservedBy.contains(studid))) { //not in stock and not resserved by
                                Toast.makeText(this,"Book is not available",Toast.LENGTH_SHORT).show()
                            } else {
                                //check if the student exists
                                db.child("User").child(studid).get().addOnSuccessListener {
                                    if (it.exists()) {
                                        db.child("IssuedBooks").orderByChild("sid").equalTo(studid)
                                            .limitToLast(1).get().addOnCompleteListener {
                                                if (it.result.exists()) {
                                                    for (i in it.result.children) {
                                                        if (i.hasChild("return_date")) {
                                                            Log.w("bookname", bookname1)
                                                            issueBook(isbn,bookname1, studid)
                                                        } else {
                                                            Toast.makeText(
                                                                this,
                                                                "$studid has already issued book",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                } else {
                                                    issueBook(isbn,bookname1, studid)
                                                    //if the user has never issued this book
                                                }
                                            }

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Enter the correct student id",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Enter the correct book, this book is not there in database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


            }
        }

        submit_btn.setOnClickListener {
            val studid = studid.text.toString()
            val isbn = isbn.text.toString()
            getBookName(isbn)
            val db = database.reference
            //check if book with this name is there or not
            db.child("Book").child(isbn).get().addOnSuccessListener {
                    if (it.exists()) {
                        Log.w("book", "exists")
                        //check if the student exists
                        db.child("User").child(studid)
                            .get().addOnSuccessListener {
                                if (it.exists()) {
                                    Log.w("user", "exists")
                                    db.child("IssuedBooks").orderByChild("sid").equalTo(studid)
                                        .limitToLast(1).get().addOnCompleteListener {
                                            if (it.result.exists()) {
                                                Log.w("record", "exists")
                                                for (i in it.result.children) {
                                                    if(i.child("isbn").value.toString() == isbn){
                                                        if (i.hasChild("return_date")) {
                                                            Toast.makeText(
                                                                this,
                                                                "Book already submitted",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            Log.w("book", "already submitted")
                                                        } else {
                                                            Log.w("book", "not yet submitted")
                                                            //check for fine
                                                            val today =
                                                                Calendar.getInstance().timeInMillis
                                                            val issued =
                                                                i.child("issue_date").value as Long
                                                            val duration = today - issued
                                                            val days =
                                                                TimeUnit.MILLISECONDS.toDays(duration)
                                                            if (days > 10) {
                                                                val fine = (days - 10) * 5
                                                                Log.w("fine", fine.toString())
                                                                //check if fine is paid
                                                                if(fine == (i.child("fine").value as Long)) {
                                                                    Log.w("fine", "paid")
                                                                    submitBook(i.key.toString(),isbn)
                                                                }else{
                                                                    Toast.makeText(this@activity_issuesubmit,
                                                                        "Please pay the fine",Toast.LENGTH_SHORT).show()
                                                                }
                                                            } else {
                                                                //no fine is there submitting within time period
                                                                Log.w("fine", "No fine is there")
                                                                submitBook(i.key.toString(),isbn)
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.w("book", "have not issued this book")
                                                Toast.makeText(
                                                    this,
                                                    "$studid have not issued $bookname1",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                //flag = false //if the user has never issued this book
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Enter the correct student id",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.w("stud", "not exists")
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Enter the correct book, this book is not there in database",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w("book", "not exists")
                    }
                }

        }

    }

    fun validate(): Boolean {
        if(studid!!.length() == 0){
            studid!!.error = "This field is required"
            return false
        }
        if(isbn!!.length() == 0){
            isbn!!.error = "This field is required"
            return false
        }
        return true
    }

    private fun issueBook(isbn:String, bookname: String, studid: String) {
        val db = database.reference
        val issued =
            IssuedBooks(isbn,bookname, studid, Calendar.getInstance().timeInMillis, null)
        val key = db.push().key.toString()
        db.child("IssuedBooks").child(key).setValue(issued).addOnSuccessListener {
            Toast.makeText(this, "$bookname issued to $studid", Toast.LENGTH_SHORT)
                .show()
            Log.w("book", "Book issued")

            db.child("Book").child(isbn).get().addOnSuccessListener {
                if(it.exists() && it.hasChild("reservedBy")){ //remove from reservedBy and not update stock as it is already updated
                    val reserved = it.child("reservedBy").value as MutableList<*>
                    if(reserved.contains(studid)){
                        val i = reserved.indexOf(studid)
                        if(i >= 0){
                            reserved.remove(studid)
                            db.child("Book").child(isbn).child("reservedBy").setValue(reserved)
                        }
                    }
                }else{
                    // update stock count
                    val bookRef = db.child("Book").child(isbn)
                    bookRef.child("stock")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val stock = snapshot.value.toString().toInt()
                                    if (stock > 0) {
                                        bookRef.child("stock").setValue(stock - 1)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w("TAG", "onCancelled", error.toException())
                            }
                        })
                }
            }

        }
    }

    private fun submitBook(key: String,isbn: String) {
        val db = database.reference
        db.child("IssuedBooks").child(key).child("return_date")
            .setValue(Calendar.getInstance().timeInMillis).addOnSuccessListener {
                Toast.makeText(this@activity_issuesubmit, "Book submitted",Toast.LENGTH_SHORT).show()
                // update stock count
                val bookRef = db.child("Book").child(isbn)
                bookRef.child("stock")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val stock = snapshot.value.toString().toInt()
                                if (stock > 0) {
                                    bookRef.child("stock").setValue(stock + 1)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("TAG", "onCancelled", error.toException())
                        }
                    })
            }
    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            studid.setText(result.contents)
        }
    }

    private val barcodeLauncher1 = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            isbn.setText(result.contents)
        }
    }

    private fun getBookName(isbn: String){
        val db = database.reference
        db.child("Book").orderByChild("isbn").equalTo(isbn).get().addOnSuccessListener {
            Log.w("bookname", it.childrenCount.toString())
            for (i in it.children) {
                Log.w("bookname", i.child("name").value.toString())
                bookname1 = i.child("name").value.toString()
            }
        }
        Thread.sleep(1000)
    }
}


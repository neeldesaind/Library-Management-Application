package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.android.synthetic.main.activity_addbook.*
import kotlinx.android.synthetic.main.activity_addbook.back
import kotlinx.android.synthetic.main.activity_forgotpassword.*

class activity_addbook : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase //changes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)

        back.setOnClickListener {
            finish()
        }

        val database = FirebaseDatabase.getInstance()

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


        addbook_btn.setOnClickListener{
            if(validate()){
                val isbn1 = isbn.text.toString()
                val bookname1 = bookname.text.toString()
                val bookauthor1 = bookauthor.text.toString()
                val bookpub1 = bookpub.text.toString()
                val stock1 = stock.text.toString().toInt()

                //to check if there is book with same name
                val db = database.reference.child("Book")

                db.child(isbn1).get().addOnSuccessListener {
                    if(it.exists()){
                        Toast.makeText(this,"Book already registered", Toast.LENGTH_SHORT).show()
                        //todo prompt to redirect towards update
                    }
                    else{
                        val books = Book(isbn1,bookname1,bookauthor1,bookpub1,stock1)
                        db.child(isbn1).setValue(books).addOnSuccessListener {
                            Toast.makeText(this,"Book inserted successfully",Toast.LENGTH_SHORT).show()
                            isbn.text.clear()
                            bookname.text.clear()
                            bookauthor.text.clear()
                            bookpub.text.clear()
                            stock.text.clear()
                        }
                    }
                }
            }
        }
    }

    private fun validate(): Boolean{
        return true
    }

    private val barcodeLauncher1 = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            isbn.setText(result.contents)
        }
    }
}
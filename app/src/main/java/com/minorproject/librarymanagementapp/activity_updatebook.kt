package com.minorproject.librarymanagementapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.android.synthetic.main.activity_updatebook.*

class activity_updatebook : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatebook)

        back.setOnClickListener {
            finish()
        }

        val database = FirebaseDatabase.getInstance().reference.child("Book")
        val isbns = mutableListOf<String>()
        isbns.clear()// to clear the values before hand if any
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



        //to fill the edit text according to value selected from spinner
        sp1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isbn1 = sp1.selectedItem.toString()

                database.orderByChild("isbn").equalTo(isbn1).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (i in it.result!!.children) {

                            val book_name = i.child("name").value.toString()
                            val authorName = i.child("author").value.toString()
                            val stockCount = i.child("stock").value.toString()
                            val pub = i.child("publication").value.toString()

                            // Update the EditText fields with the author and stock values
                            isbn.setText(isbn1)
                            bookname.setText(book_name)
                            bookauthor.setText(authorName)
                            bookpub.setText(pub)
                            stock.setText(stockCount)
                        }
                    } else {
                        // Handle any errors that occur
                    }
                }

            }

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

        updatebook_btn.setOnClickListener {
            if (validate()) {

                val isbn1 = isbn.text.toString()
                val bookName = bookname.text.toString()
                val bookAuthor = bookauthor.text.toString()
                val bookPub = bookpub.text.toString()
                val bookStock = stock.text.toString().toInt()

                val bookValues = mapOf(
                    "isbn" to isbn1,
                    "name" to bookName,
                    "author" to bookAuthor,
                    "publication" to bookPub,
                    "stock" to bookStock
                )

                val bookvals = Book(isbn1,bookName,bookAuthor,bookPub,bookStock)

                var ad = AlertDialog.Builder(this)
                ad.setTitle("Update")
                ad.setMessage("Are you sure you want to update?")
                ad.setPositiveButton("Yes") { dialog, id ->
                    val current_isbn = sp1.selectedItem.toString()
                    database.child(current_isbn).get().addOnSuccessListener {

                        //remove current data
                        database.child(current_isbn).removeValue()

                        //add it again
                        database.child(isbn1).updateChildren(bookValues).addOnCompleteListener {
                            if(it.isSuccessful)
                                Toast.makeText(this, "Book updated successfully", Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(this, "Failed to update book", Toast.LENGTH_SHORT).show()
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


    private fun validate():Boolean {
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
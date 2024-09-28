package com.minorproject.librarymanagementapp

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_generatebarcode.*
import java.io.File
import java.io.FileOutputStream


class activity_generatebarcode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generatebarcode)

        back.setOnClickListener {
            finish()
        }

        val database = FirebaseDatabase.getInstance().reference.child("Book")
        val bookNames = mutableListOf<String>()
        //bookNames.clear()// to clear the values before hand if any
        //to fill the spinner with book names
        database.get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result!!.children){
                    val bookname = i.child("isbn").value?.toString()
                    if(!bookname.isNullOrEmpty()) {
                        bookNames.add(bookname)
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    book_name_spinner.adapter = adapter

                }
            }
        }

        book_name_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                iv_barcode.setImageBitmap(generateBarcode(book_name_spinner.selectedItem.toString()))
                database.child(book_name_spinner.selectedItem.toString()).child("name").get().addOnSuccessListener {
                    tv_barcode_bname.text = it.value.toString()
                }
            }

        }




        download_qr.setOnClickListener {
            if(book_name_spinner.selectedItemPosition != -1){
                val book_name = book_name_spinner.selectedItem.toString()
                val barcode = generateBarcode(book_name)
                if (barcode != null) {

                    val downloadFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Barcode")
                    downloadFolder.mkdirs() // Create the folder if it doesn't exist
                    val filename = book_name + "_barcode.jpg"
                    val file = File(downloadFolder, filename)
                    val out = FileOutputStream(file)
                    barcode.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    Toast.makeText(this, "Saved as Downloads/barcode/$filename", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to generate barcode", Toast.LENGTH_SHORT).show()
                }
            }
        }
}
        //todo get the code which worked from chat gpt alt+tab above






    fun generateBarcode(text: String): Bitmap? {
        var bmp:Bitmap? = null
        try {
            val barcodeEncoder = BarcodeEncoder()
            bmp = barcodeEncoder.encodeBitmap(text, BarcodeFormat.EAN_13,700,500)
            iv_barcode.setImageBitmap(bmp)
            return bmp
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return bmp
    }
}




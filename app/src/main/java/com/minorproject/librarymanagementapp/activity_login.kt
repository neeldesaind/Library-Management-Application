package com.minorproject.librarymanagementapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class activity_login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Home Page"

        val email = FirebaseAuth.getInstance().currentUser!!.email
        val dbref = FirebaseDatabase.getInstance().reference
        dbref.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
            for(i in it.children){
                displayusername.text = "Welcome, ${i.child("name").value.toString()}"

            }
        }



        aud_btn.setOnClickListener {
            val intent = Intent(this, activity_addupdatedel::class.java)
            startActivity(intent)
        }

        generatebarcode_btn.setOnClickListener {
            val intent = Intent(this, activity_generatebarcode::class.java)
            startActivity(intent)
        }
        generatereports_lib_btn.setOnClickListener {
            val intent = Intent(this, activity_generatereports::class.java)
            startActivity(intent)
        }

        issuesubmit_btn.setOnClickListener {
            val intent = Intent(this, activity_issuesubmit::class.java)
            startActivity(intent)
        }

        search_lib_btn.setOnClickListener {
            val intent = Intent(this, activity_searchbook::class.java)
            startActivity(intent)
        }

        reservationRequests_btn.setOnClickListener {
            val intent = Intent(this, activity_reservation::class.java)
            startActivity(intent)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cp -> startActivity(Intent(this, activity_newpassword::class.java))
            R.id.logout -> {
                var ad = AlertDialog.Builder(this)
                ad.setTitle("Logout")
                ad.setMessage("Are you sure you want to logout?")
                ad.setPositiveButton("Yes") { dialog, id ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                ad.setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                }
                ad.show()
            }

        }

        return super.onOptionsItemSelected(item)
    }


}
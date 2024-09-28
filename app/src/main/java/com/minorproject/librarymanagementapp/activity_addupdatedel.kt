package com.minorproject.librarymanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_addupdatedel.*
import kotlinx.android.synthetic.main.activity_addupdatedel.back
import kotlinx.android.synthetic.main.activity_forgotpassword.*

class activity_addupdatedel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addupdatedel)
        back.setOnClickListener {
            finish()
        }

        addbook.setOnClickListener {
            val intent = Intent(this, activity_addbook::class.java)
            startActivity(intent)
        }
        updatebook.setOnClickListener {
            val intent = Intent(this, activity_updatebook::class.java)
            startActivity(intent)
        }
        deletebook.setOnClickListener {
            val intent = Intent(this, activity_delete::class.java)
            startActivity(intent)
        }
    }
}
package com.minorproject.librarymanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_forgotpassword.*

class activity_forgotpassword : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase //changes
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
        title = "Forgot Password"
        back_login.setOnClickListener {
            finish()
        }
        back.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        sendotp.setOnClickListener{
            if(email.length() == 0){
                email!!.error = "Please enter email"
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                email!!.error = "Enter Valid E-mail"
            }
            auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Email sent successfully to reset your password",Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this,"No email found",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
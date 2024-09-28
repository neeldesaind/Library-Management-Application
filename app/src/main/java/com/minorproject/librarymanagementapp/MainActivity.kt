package com.minorproject.librarymanagementapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var db : DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "CHARUSAT LIBRARY"

        FirebaseApp.initializeApp(this);



        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                if(auth.currentUser?.isEmailVerified == true){
                    if(user.email == "devmpatel73@gmail.com"){
                        val intent = Intent(this, activity_login::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this, activity_loginstudent::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        //to get values after sign up successfully
        val extras = intent.extras
        if (extras != null) {
            username.setText(extras.getString("email"))
            password.setText(extras.getString("pass"))
        }

        var forgotpwwd: TextView = findViewById(R.id.forgotpswd)
        var sgnup: TextView = findViewById(R.id.register)

        sgnup.setOnClickListener {
            var intent = Intent(this, activity_registration::class.java)
            startActivity(intent)
            finish()
        }
        forgotpwwd.setOnClickListener {
            var intent = Intent(this, activity_forgotpassword::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        loginbtn.setOnClickListener{
            if(validate()){
                auth.signInWithEmailAndPassword(username.text.toString(),password.text.toString()).addOnSuccessListener {
                    if(auth.currentUser?.isEmailVerified == true){
                        var token = ""
                        if(username.text.toString() == "devmpatel73@gmail.com"){
                            val email = auth.currentUser!!.email
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w("token", "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                token = task.result
                                // Log and toast
                                Log.d("token", "token = $token")
                            })
                            db.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
                                for(i in it.children){
                                    val sid =  i.child("sid").value.toString()
                                    db.child("User").child(sid).child("fcmToken").setValue(token)
                                }
                            }

                            val intent = Intent(this, activity_login::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            val email = auth.currentUser!!.email
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w("token", "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                token = task.result
                                // Log and toast
                                Log.d("token", "token = $token")
                            })
                            db.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
                                for(i in it.children){
                                    val sid =  i.child("sid").value.toString()
                                    db.child("User").child(sid).child("fcmToken").setValue(token)
                                }
                            }

                            val intent = Intent(this, activity_loginstudent::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthStateListener)
    }

    private fun validate(): Boolean{
        if(!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()){
            username!!.error = "Enter Valid E-mail"
            return false
        }
        if(password!!.length() < 6){
            password!!.error = "Password should be more than 6 characters"
            return false
        }
        else if(password!!.length() == 0){
            password!!.error = "This field is required"
            return false
        }
        return true
    }
}
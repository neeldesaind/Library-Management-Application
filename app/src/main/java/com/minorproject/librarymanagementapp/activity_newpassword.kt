package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgotpassword.back
import kotlinx.android.synthetic.main.activity_newpassword.*

class activity_newpassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpassword)
        title = "Change Passsword"
        back.setOnClickListener {
            finish()
        }

        changepassword_btn.setOnClickListener {
            if(validate()){
                val currentUser = FirebaseAuth.getInstance().currentUser
                val email = currentUser?.email.toString()
                val credential = EmailAuthProvider.getCredential(email, currentpassword.text.toString())
                currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                    if(it.isSuccessful){
                        currentUser.updatePassword(newpassword.text.toString()).addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(this,"Password Changed successfully",Toast.LENGTH_SHORT).show()
                                finish() //go to last activity
                            }
                        }
                    }else{
                        Toast.makeText(this,"Please enter the correct password",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }


    }
    fun validate():Boolean{
        if(currentpassword!!.length() == 0){
            currentpassword!!.error = "This field is required"
            return false
        }
        if(newpassword!!.length() == 0){
            newpassword!!.error = "This field is required"
            return false
        }
        if(newpassword1!!.length() == 0){
            newpassword1!!.error = "This field is required"
            return false
        }
        else if(newpassword.text.toString() != newpassword.text.toString()){
            Toast.makeText(this,"Please enter same new password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}
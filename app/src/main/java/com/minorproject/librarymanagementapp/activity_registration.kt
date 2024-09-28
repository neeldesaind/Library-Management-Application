package com.minorproject.librarymanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*
import java.time.LocalDate
import java.util.*

class activity_registration : AppCompatActivity() {


    private lateinit var database: FirebaseDatabase //changes
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        title = "Registration"

        val spin: Spinner = findViewById(R.id.sp1)
        val arraySpinner = arrayOf("Select Course", "BCA", "MCA", "BSCIT", "MSCIT")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spin.adapter = adapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        btnsignup.setOnClickListener {
            if(validate()){
                val name = fullname.text.toString()
                val sid = studentid.text.toString()
                var gender = ""
                if(male.isChecked) { gender = "Male" } else if(female.isChecked) { gender = "Female"}
                val course = sp1.selectedItem.toString()
                val email = username.text.toString()
                val pass = password.text.toString()

                //check if user already exists
                db = FirebaseDatabase.getInstance().getReference("User")
                //to check if there is user with same sid
                db.child(sid).get().addOnSuccessListener {
                    if(it.exists()){
                        Toast.makeText(this,"Student Id already registered",Toast.LENGTH_SHORT).show()
                        studentid!!.error = "Student Id already registered"
                    } else{
                        //todo go back to login if already exists
                        //to check if email already there
                        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener{
                            if(it.result.signInMethods!!.isNotEmpty()){
                                Toast.makeText(this,"Email already registered",Toast.LENGTH_SHORT).show()
                                username!!.error = "Email already registered"
                            }
                            else{
                                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        val databaseRef = database.reference.child("User").child(sid)
                                        val users = User(name, sid, gender, course, email)
                                        databaseRef.setValue(users).addOnSuccessListener {
                                            auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                                Toast.makeText(this,"Verification mail sent",Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, MainActivity::class.java)
                                                intent.putExtra("email",email)
                                                intent.putExtra("pass",pass)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    }else{
                                        Toast.makeText(this,it.exception!!.message,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }




            }
        }


        val movetologin: TextView = findViewById(R.id.loginpage)
        movetologin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }







    }
    private fun validate(): Boolean{
        var selected = sp1.selectedItem.toString()
        val sid = studentid.text.toString()

        selected = when {
            selected == "BSCIT" -> "BSIT"
            selected == "MSCIT" -> "MSIT"
            else -> selected
        }

        if(fullname!!.length() == 0) {
            fullname!!.error = "This field is required"
            return false
        }


        //todo better validation for student id
        if(studentid!!.length() == 0) {
            studentid!!.error = "This field is required"
            return false
        }

        if(gender_radioGroup.checkedRadioButtonId == -1) {
            gender_tv!!.error = "Please select your gender"
            return false
        }
        if(sp1.selectedItemPosition <= 0){
            course_tv!!.error = "Please select your course"
            return false
        }
        if(!sid.contains(selected, ignoreCase = true)){
            studentid!!.error = "Course should be matching"
            return false
        }
        if(!validateText(studentid.text.toString())){
            studentid!!.error = "Please enter valid Id"
            return false
        }

        if(username!!.length() == 0){
            username!!.error = "This field is required"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
            username!!.error = "Enter Valid E-mail"
            return false
        }
        if(password!!.length() == 0){
            password!!.error = "This field is required"
            return false
        }
        if(confirmpassword!!.length() == 0){
            confirmpassword!!.error = "This field is required"
            return false
        }
        else if(password.text.toString() != confirmpassword.text.toString()){
            Toast.makeText(this,"Please enter same password", Toast.LENGTH_SHORT).show()
            return false
        }



        return true
    }

    fun validateText(input: String): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val pattern = Regex("^(00|[01]\\d|2[0-$currentYear])(MCA|MSIT|BCA|BSIT)([0-1]\\d{2}|2[0-9][0-9]|200)$", RegexOption.IGNORE_CASE)
        return pattern.matches(input)
    }

}
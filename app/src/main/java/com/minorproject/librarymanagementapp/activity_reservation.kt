package com.minorproject.librarymanagementapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.minorproject.librarymanagementapp.Adapter.ReservationRequestsAdapter
import kotlinx.android.synthetic.main.activity_reservation.*

class activity_reservation : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var reservationRecyclerview: RecyclerView
    private lateinit var reservationArrayList: ArrayList<Reservation>
    private lateinit var stockArrayList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        back.setOnClickListener {
            finish()
        }

        reservationRecyclerview = rv_reservations
        reservationRecyclerview.layoutManager = LinearLayoutManager(this)
        reservationRecyclerview.setHasFixedSize(true)

        reservationArrayList = arrayListOf<Reservation>()
        stockArrayList = arrayListOf<String>()

        getReservationDetails()

    }

    private fun getReservationDetails() {
        dbref = FirebaseDatabase.getInstance().reference

        dbref.child("Reservation").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Log.e("before loop", snapshot.childrenCount.toString())
                for (i in snapshot.children) {
                    Log.e("in loop", i.childrenCount.toString())
                    //if the status is pending
                    if(!i.hasChild("status")){
                        val user = i.getValue(Reservation::class.java)

                        Log.w("isbn value", "isbn = ${user?.isbn}")
                        dbref.child("Book").child(user?.isbn.toString()).child("stock").get().addOnCompleteListener { stockSnapshot ->
                            Log.w("stock count", "stock = ${stockSnapshot.result.value}")
                            val stock = stockSnapshot.result.value.toString()
                            reservationArrayList.add(user!!)
                            stockArrayList.add(stock!!)
                        }
                    }
                }
                Thread.sleep(1000)
                reservationRecyclerview.adapter = ReservationRequestsAdapter(reservationArrayList, stockArrayList, this)
                Log.w("adapter setting", "setting adapter")

            }
        }.addOnFailureListener { error ->
            Log.w("TAG", "getReservationDetails: failed to get reservation data", error)
        }

    }

}
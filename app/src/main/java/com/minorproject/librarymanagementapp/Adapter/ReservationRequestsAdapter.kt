package com.minorproject.librarymanagementapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.minorproject.librarymanagementapp.R
import com.minorproject.librarymanagementapp.Reservation
import org.json.JSONObject

class ReservationRequestsAdapter(val reservation_table: ArrayList<Reservation>, val stock_list: ArrayList<String>, val context: Context) : RecyclerView.Adapter<ReservationRequestsAdapter.ViewHolder>() {
    private lateinit var db : DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create and return view holder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reservation_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // bind data to view holder
        if (reservation_table.isNullOrEmpty()) {
            return
        }

        //null safety check for stock_list
        val stock = if (stock_list.isNullOrEmpty()) {
            "0"
        } else {
            stock_list[position]
        }

        val current_item = reservation_table[position]

        holder.isbn.text = current_item.isbn
        holder.bname.text = current_item.bname
        holder.sid.text = current_item.sid
        holder.stock.text = stock
        var key: String = ""
        holder.approve_btn.setOnClickListener {
            Log.w("approve"," button click")
            db = FirebaseDatabase.getInstance().reference
            //get key to change the status
            db.child("Reservation").orderByChild("sid").equalTo(current_item.sid).limitToLast(1)
                .get().addOnSuccessListener {
                    Log.w("approve ","children count ${it.childrenCount}")
                    for(i in it.children) {
                    Log.w("approve"," key for reservation = ${i.key}")
                    key = i.key.toString()
                }
                    //change status to approve
                    db.child("Reservation").child(key).child("status").setValue("approved").addOnSuccessListener {
                        //update stock and add user in reserved by
                        Log.w("approve", "status approved")
                        db.child("Book").child(current_item.isbn.toString()).get().addOnSuccessListener { it1 ->
                            val stock1 = it1.child("stock").value.toString().toInt()
                            Log.w("approve", "current stock = $stock1")
                            if(stock1 > 0) {
                                db.child("Book").child(current_item.isbn.toString()).child("stock")
                                    .setValue(stock1 - 1).addOnSuccessListener {
                                        Log.w("approve", "stock updated")
                                        var currentList: MutableList<String> = ArrayList()
                                        if(it1.hasChild("reservedBy")){
                                            Log.w("approve", "has reservedBy")
                                            currentList = it1.child("reservedBy").value as MutableList<String>
                                        }
                                        currentList?.add(current_item.sid.toString())
                                        db.child("Book").child(current_item.isbn.toString()).child("reservedBy")
                                            .setValue(currentList).addOnSuccessListener {
                                                Log.w("approve", "added ${current_item.sid} to reservedBy")
                                                Toast.makeText(holder.itemView.context, "Approved", Toast.LENGTH_SHORT).show()
                                                db.child("User").child(current_item.sid.toString()).child("fcmToken").get().addOnSuccessListener {
                                                    val token = it.value.toString()
                                                    notif(current_item.bname.toString(),"approved",token)
                                                }
                                            }
                            }
                        }
                        }
                    }
            }
        }

        holder.deny_btn.setOnClickListener {
            db = FirebaseDatabase.getInstance().reference
            db.child("Reservation").orderByChild("sid").equalTo(current_item.sid).limitToLast(1)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for(i in snapshot.children){
                                Log.w("reservation",i.childrenCount.toString() + "${i.key}")
                                db.child("Reservation").child(i.key.toString()).child("status").setValue("denied")
                                    .addOnSuccessListener {
                                        Toast.makeText(holder.itemView.context, "Denied", Toast.LENGTH_SHORT).show()
                                        db.child("User").child(current_item.sid.toString()).child("fcmToken").get().addOnSuccessListener {
                                            val token = it.value.toString()
                                            notif(current_item.bname.toString(),"denied",token)
                                        }
                                    }

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("TAG", "onCancelled", error.toException())
                    }
                })

        }


    }

    override fun getItemCount(): Int {
        // return total number of items
        return if (reservation_table.isEmpty()) 0 else reservation_table.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // initialize views in view holder
        val isbn: TextView = itemView.findViewById(R.id.tvisbn_reservation)
        val bname: TextView = itemView.findViewById(R.id.tvBookName_reservation)
        val stock : TextView = itemView.findViewById(R.id.tvStock_reservation)
        val sid : TextView = itemView.findViewById(R.id.tvSid_reservation)
        val approve_btn : Button = itemView.findViewById(R.id.approve_btn)
        val deny_btn : Button = itemView.findViewById(R.id.deny_btn)
    }

    private fun notif(book: String,msg: String,key: String){
        val json = JSONObject()
        json.put("to", key)
        val notification = JSONObject()
        notification.put("title", "Reservation Request Update for book: $book")
        notification.put("body", "Reservation $msg")
        notification.put("icon", "logo_appication")
        json.put("notification", notification)

        val url = "https://fcm.googleapis.com/fcm/send"
        val request = object : JsonObjectRequest(
            Request.Method.POST, url, json,
            Response.Listener<JSONObject> { response ->
                Log.d("TAG", "Notification sent")
            },
            Response.ErrorListener { error ->
                Log.e("TAG", "Notification send failed: $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=AAAAxPxSp4o:APA91bEEW0WhSqJ5kBRN2Dqgj9W0slowKUEK7u6QukxSIL_ynGHB6kb3w1IqI3A8hnr3K-iup-3sFHVs2el384ktgyCAewqO_JtaQY5HziOGuC6f60nd1a5IZaCwqQuQJU77b3sXrNTU"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(request)

    }


}
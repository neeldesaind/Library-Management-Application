package com.minorproject.librarymanagementapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import kotlinx.android.synthetic.main.activity_payfine.back
import kotlinx.android.synthetic.main.activity_payfine.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class activity_payfine : AppCompatActivity(){

    private lateinit var dbref : DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payfine)

        //set it as invisible for oncreate
        table_fine5.visibility = View.INVISIBLE
        payfine_btn.visibility = View.INVISIBLE

        back.setOnClickListener {
            finish()
        }


        dbref = FirebaseDatabase.getInstance().reference
        database = FirebaseDatabase.getInstance()

        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
        var studid = ""
        var name = ""
        var key1 = ""
        dbref.child("User").orderByChild("email").equalTo(email).get().addOnSuccessListener {
            for (i in it.children) {
                studid = i.child("sid").value.toString()
                name = i.child("name").value.toString()
            }
            dbref.child("IssuedBooks").orderByChild("sid").equalTo(studid).limitToLast(1).get().addOnCompleteListener {
                if(it.result.exists()){
                    for(i in it.result.children){
                        val today = Calendar.getInstance().timeInMillis
                        val issued = i.child("issue_date").value as Long
                        val duration = today - issued
                        var days = TimeUnit.MILLISECONDS.toDays(duration)
                        if (days > 10 ) {

                            days -= 10 //days after due due is 10 days
                            var fine = days * 5
                            if(fine == (i.child("fine").value as Long)){
                                table_fine5.visibility = View.INVISIBLE
                                payfine_btn.visibility = View.INVISIBLE
                                Toast.makeText(this,"There is no pending fees",Toast.LENGTH_SHORT).show()
                            }else{
                                if((i.child("fine").value.toString().toInt()) > 0) {
                                    fine -= i.child("fine").value.toString().toLong()
                                }
                                tv_BookName5.text = i.child("bname").value.toString()
                                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                                tv_IssueDate5.text = simpleDateFormat.format(i.child("issue_date").value)
                                tv_duedays5.text = days.toString()
                                tv_Fine5.text = fine.toString()
                                key1 = i.key.toString()
                                table_fine5.visibility = View.VISIBLE
                                payfine_btn.visibility = View.VISIBLE
                            }

                        }else{
                            table_fine5.visibility = View.INVISIBLE
                            payfine_btn.visibility = View.INVISIBLE
                            Toast.makeText(this,"There is no pending fees",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    table_fine5.visibility = View.INVISIBLE
                    payfine_btn.visibility = View.INVISIBLE
                    Toast.makeText(this,"There is no pending fees",Toast.LENGTH_SHORT).show()
                }
            }
        }


        payfine_btn.setOnClickListener {

            paynow(email,tv_Fine5.text.toString(),tv_BookName5.text.toString(),name,studid,key1)
        }
    }


    fun paynow(email: String,fine: String,bname: String,name: String,studid: String,key1: String){

        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF1] = "udf1"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF2] = "udf2"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF3] = "udf3"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF4] = "udf4"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF5] = "udf5"
        additionalParamsMap[PayUCheckoutProConstants.SODEXO_SOURCE_ID] = "srcid123"

        val key="oRFhma"



        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount(fine)
            .setIsProduction(false)
            .setKey(key)
            .setProductInfo(bname)
            .setPhone("9977733475")
            .setTransactionId(Calendar.getInstance().timeInMillis.toString())
            .setFirstName(name)
            .setEmail(email)
            .setSurl("https://payuresponse.firebaseapp.com/success")
            .setFurl("https://payuresponse.firebaseapp.com/failure")
        .build()

        PayUCheckoutPro.open(
            this, payUPaymentParams,
            object : PayUCheckoutProListener {


                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    val db = database.reference
                    db.child("IssuedBooks").child(key1).child("fine").get().addOnSuccessListener {
                        db.child("IssuedBooks").child(key1).child("fine").setValue((it.value as Long) + fine.toLong())

                        Toast.makeText(this@activity_payfine,"success ${fine.toLong()}",Toast.LENGTH_SHORT).show()
                        finish()

                        //Toast.makeText(this@activity_payfine,"success",Toast.LENGTH_SHORT).show()
                    }

                }


                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                    Toast.makeText(this@activity_payfine,"fail",Toast.LENGTH_SHORT).show()
                }


                override fun onPaymentCancel(isTxnInitiated:Boolean) {
                    Toast.makeText(this@activity_payfine,"cancel",Toast.LENGTH_SHORT).show()
                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage: String
                    if (errorResponse != null && errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                        errorMessage = errorResponse.errorMessage!!
                    else {
                        //errorMessage = resources.getString(R.string.some_error_occurred)
                    }
                    error.text = errorResponse.errorMessage
                    Toast.makeText(this@activity_payfine,errorResponse.errorMessage,Toast.LENGTH_SHORT).show()
                    Log.e("debugging the error",errorResponse.errorMessage.toString())
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    if ( valueMap.containsKey(CP_HASH_STRING)
                        && valueMap.containsKey(CP_HASH_STRING) != null
                        && valueMap.containsKey(CP_HASH_NAME)
                        && valueMap.containsKey(CP_HASH_NAME) != null) {

                        val hashData = valueMap[CP_HASH_STRING]
                        val hashName = valueMap[CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? = HashGenerationUtils.generateHashFromSDK(
                            hashData.toString(),
                            "RYmYI6iecGER0UB9EGVBBp0BQjXQIGVj"
                        )
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }


}
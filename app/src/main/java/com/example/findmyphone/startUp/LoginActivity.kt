package com.example.findmyphone.startUp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.findmyphone.Data.UserData
import com.example.findmyphone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = Firebase.auth


        btRegister.setOnClickListener {
            UserData(this).savePhoneNumber(editTextPhone.text.toString())
            val mDatabase = FirebaseDatabase.getInstance().reference
            val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
            val date = Date()
            mDatabase.child("Users").child(editTextPhone.text.toString()).child("request").setValue(df.format(date).toString())
            mDatabase.child("Users").child(editTextPhone.text.toString()).child("finders").setValue(df.format(date).toString())
            finish()
        }
        mAuth!!.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                    val user = mAuth!!.currentUser
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
    }

}
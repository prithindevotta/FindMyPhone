package com.example.findmyphone.startUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findmyphone.Data.UserData
import com.example.findmyphone.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btRegister.setOnClickListener {
            UserData(this).savePhoneNumber(editTextPhone.text.toString())
            finish()
        }

    }
}
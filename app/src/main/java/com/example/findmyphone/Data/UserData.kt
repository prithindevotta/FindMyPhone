package com.example.findmyphone.Data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.findmyphone.startUp.LoginActivity

class UserData(var context: Context) {
    var sharedPreferences: SharedPreferences? = context.getSharedPreferences("userData", Context.MODE_PRIVATE)

    fun savePhoneNumber(number: String){
        val editor = sharedPreferences!!.edit()
        editor.putString("phoneNumber", number)
        editor.apply()
    }

    fun loadPhoneNumber() {
        val phoneNumber = sharedPreferences!!.getString("phoneNumber", "empty")
        if(phoneNumber.equals("empty")){
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
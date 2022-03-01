package com.example.findmyphone.Data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import com.example.findmyphone.MainActivity
import com.example.findmyphone.startUp.LoginActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class UserData(var context: Context) {
    var sharedPreferences: SharedPreferences? = context.getSharedPreferences("userData", Context.MODE_PRIVATE)

    fun savePhoneNumber(number: String){
        val editor = sharedPreferences!!.edit()
        editor.putString("phoneNumber", number)
        editor.apply()
    }

    fun loadPhoneNumber(): String?{
        val phoneNumber = sharedPreferences!!.getString("phoneNumber", "empty")
        return phoneNumber
    }
    fun checkFirstLoad(){
        val phoneNumber = sharedPreferences!!.getString("phoneNumber", "empty")
        if(phoneNumber.equals("empty")){
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        return
    }
    fun saveContactInfo(){
        var listOfTrackers: String = ""
        for((key, value) in myTrackers){
            listOfTrackers+= "@$key%$value"
        }
        if(listOfTrackers.isEmpty()){
            listOfTrackers = "empty"
        }
        val editor = sharedPreferences!!.edit()
        editor.putString("listOfTrackers", listOfTrackers)
        editor.apply()
    }
    fun loadContactInfo(){
        myTrackers.clear()
        val listOfTrackers = sharedPreferences!!.getString("listOfTrackers", "empty")
        if(!listOfTrackers.equals("empty")){
            val keyValue = listOfTrackers!!.split("@").toTypedArray()
            var i=1;
            while(i<keyValue.size){
                if(keyValue[i].contains("%")){
                    val user = keyValue[i].split("%").toTypedArray()
                    myTrackers[user[0]] = user[1]
                }
                i++
            }
        }
    }
    companion object{
        var myTrackers: MutableMap<String, String> = HashMap()
        fun formatPhoneNumber(phoneNumber: String): String{
            var onlyNumbers = phoneNumber.replace("[^0-9]".toRegex(), "")
            if(phoneNumber[0] == '+'){
                onlyNumbers = "+$onlyNumbers"
            }
            return onlyNumbers
        }
    }
}
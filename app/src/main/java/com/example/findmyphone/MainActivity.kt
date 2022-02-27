package com.example.findmyphone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.findmyphone.Data.UserData

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData = UserData(this)
        userData.loadPhoneNumber()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addTracker -> {
                val intent = Intent(this, MyTracker::class.java)
                startActivity(intent)
            }
            R.id.help ->{
                //Help
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}
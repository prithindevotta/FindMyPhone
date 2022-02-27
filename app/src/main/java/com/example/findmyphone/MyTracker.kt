package com.example.findmyphone

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findmyphone.Data.UserContact
import kotlinx.android.synthetic.main.activity_my_tracker.*
import java.security.Permission

class MyTracker : AppCompatActivity() {
    var adapter: ContactAdapter? = null
    var contactList =  ArrayList<UserContact>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tracker)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(contactList)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        menuInflater.inflate(R.menu.tracker_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.tracker -> {
                checkPermission()
            }
            R.id.finish ->{
                finish()
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
    private val REQUEST_CODE = 123
    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CODE)
            }
        }
        pickContact()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickContact()
                }
                else{
                    Toast.makeText(this, "Contacts cannot be accessed", Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    fun pickContact(){

    }
}
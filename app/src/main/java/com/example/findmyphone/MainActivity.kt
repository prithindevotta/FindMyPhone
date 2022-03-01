package com.example.findmyphone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.renderscript.Sampler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmyphone.Data.UserContact
import com.example.findmyphone.Data.UserData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), OnClick{
    var mAdapter: ContactAdapter? = null
    var contactList =  ArrayList<UserContact>()
    var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData = UserData(this)
        userData.checkFirstLoad()
        mDatabase = FirebaseDatabase.getInstance().reference
        mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ContactAdapter(this, contactList)
        mainRecyclerView.adapter = mAdapter
        refreshData()
    }

    override fun onStart() {
        if (!MyService.isServiceRunning){
            checkContactPermission()
            checkLocationPermission()
        }
        super.onStart()
    }
    override fun onResume() {
        super.onResume()

        refreshData()
    }

    private fun refreshData(){
        val userData = UserData(this)
        mDatabase!!.child("Users").child(userData.loadPhoneNumber()!!).child("finders").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val td = snapshot.children
                contactList.clear()
                for (child in td){
                    contactList.add(UserContact(contactHash[child.key], child.key))
                }
                if(contactList.size == 0){
                    empty.visibility = View.VISIBLE
                }
                else{
                    empty.visibility = View.GONE
                }
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
    private val REQUEST_CODE = 123
    private fun checkContactPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CODE)
            }
        }
        loadContact()
    }
    val contactHash = HashMap<String, String>()
    private fun loadContact(){
        try{
            contactHash.clear()
            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
            cursor!!.moveToFirst()
            do{
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phoneNumber = UserData.formatPhoneNumber(phoneNumber)
                contactHash[phoneNumber] = name
            }while (cursor.moveToNext())
        }catch (e: Exception){

        }
    }

    override fun onClick(position: Int) {
        val userInfo = contactList[position]
        val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
        val date = Date()
        mDatabase!!.child("Users").child(userInfo.phoneNumber!!).child("request").setValue(df.format(date).toString())
        val intent = Intent(applicationContext, MapsActivity::class.java)
        intent.putExtra("phoneNumber", userInfo.phoneNumber)
        startActivity(intent)

    }
    private val REQUEST_LOC_CODE = 124
    private fun checkLocationPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOC_CODE)
            }
        }
        getUserLocation()
    }
    fun getUserLocation(){
        //start the service
        if(!MyService.isServiceRunning){
            val intent = Intent(baseContext, MyService::class.java)
            startService(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadContact()
                }
                else{
                    Toast.makeText(this, "Contacts cannot be accessed", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_LOC_CODE ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }
                else{
                    Toast.makeText(this, "Location cannot be accessed", Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

}
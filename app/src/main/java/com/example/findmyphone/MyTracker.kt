package com.example.findmyphone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findmyphone.Data.UserContact
import com.example.findmyphone.Data.UserData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_my_tracker.*
import java.security.Permission

class MyTracker : AppCompatActivity(), OnClick{
    var adapter: ContactAdapter? = null
    var contactList =  ArrayList<UserContact>()
    var mUserData: UserData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tracker)
        mUserData = UserData(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(this, contactList)
        recyclerView.adapter = adapter
        mUserData!!.loadContactInfo()
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
    private val CONTACT_CODE = 1234
    fun pickContact(){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            CONTACT_CODE -> {
                if(resultCode == Activity.RESULT_OK){
                    val contactData = data!!.data
                    val contact = contactData?.let { contentResolver.query(it, null, null, null, null) }

                    if(contact!!.moveToFirst()){
                        val id = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasPhone = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if(hasPhone.equals("1")){
                            val phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id, null, null)
                            phone!!.moveToFirst()
                            var phoneNumber = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val name = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                            phoneNumber = UserData.formatPhoneNumber(phoneNumber)
                            UserData.myTrackers[phoneNumber] = name
                            //update the UI using myTracker map
                            refreshData()

                            //save myTracker map to shared Ref
                            mUserData!!.saveContactInfo()

                            //save user details to database
                            val mDatabase = FirebaseDatabase.getInstance().reference
                            mDatabase.child("Users").child(phoneNumber).child("finders").child(UserData(applicationContext).loadPhoneNumber()!!).setValue(true)
                        }
                    }
                }

            }
            else ->{
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
    private fun refreshData(){
        contactList.clear()
        for((key, value) in UserData.myTrackers ){
            contactList.add(UserContact(value, key))
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onClick(position: Int) {
        val clicked = contactList[position]
        UserData.myTrackers.remove(clicked.phoneNumber)
        refreshData()
        mUserData!!.saveContactInfo()
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("Users").child(clicked.phoneNumber!!).child("finders").child(UserData(this).loadPhoneNumber()!!).removeValue()
    }
}

interface OnClick{
    fun onClick(position: Int)
}
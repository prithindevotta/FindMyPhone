package com.example.findmyphone

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import com.example.findmyphone.Data.UserData
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MyService: Service() {
    var mDatabase: DatabaseReference? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mDatabase = FirebaseDatabase.getInstance().reference
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var locationListener = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, locationListener)
        val userData = UserData(this)
        val myPhoneNumber = userData.loadPhoneNumber()
        mDatabase!!.child("Users").child(myPhoneNumber!!).child("request").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (myLocation == null){
                    return
                }
                val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
                val date = Date()
                mDatabase!!.child("Users").child(myPhoneNumber).child("location").child("latitude").setValue(
                    myLocation!!.latitude)
                mDatabase!!.child("Users").child(myPhoneNumber).child("location").child("longitude").setValue(
                    myLocation!!.longitude)
                mDatabase!!.child("Users").child(myPhoneNumber).child("location").child("lastOnline").setValue(df.format(date).toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return START_NOT_STICKY
    }
    companion object{
        var myLocation: Location? = null
        var isServiceRunning = false
    }
    inner class MyLocationListener: LocationListener {
        constructor() : super() {

            myLocation = Location("me")
            myLocation!!.latitude = 0.0
            myLocation!!.longitude = 0.0
        }
        override fun onLocationChanged(location: Location) {
            myLocation = location
        }

    }
}
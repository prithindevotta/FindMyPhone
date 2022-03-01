package com.example.findmyphone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.findmyphone.databinding.ActivityMapsBinding
import com.google.firebase.database.*
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        val phoneNumber = bundle!!.getString("phoneNumber")
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase!!.child("Users").child(phoneNumber!!).child("location").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude= snapshot.child("longitude").value.toString()
                    lastOnline = snapshot.child("lastOnline").value.toString()
                    Toast.makeText(applicationContext, "lat: $latitude long: $longitude", Toast.LENGTH_SHORT).show()
                    sydney = LatLng(latitude.toDouble(), longitude.toDouble())
                    loadMap()
                }catch (e: Exception){}
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }

    fun loadMap(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    companion object{
        var sydney = LatLng(-34.0, 151.0)
        var lastOnline = "NO DATA"
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        mMap.addMarker(MarkerOptions().position(sydney).title(lastOnline))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
    }
}
package com.example.yelpapp

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.yelpapp.databinding.ActivityMapsBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentAddress: Address
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myButton: MaterialButton
    private var currentLatitude: Double=38.89
    private var currentLongitude: Double=-77.187

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton=findViewById(R.id.locationBtn)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (savedInstanceState!=null){
            val latitude =savedInstanceState.getDouble("latitude")
            val longitude=savedInstanceState.getDouble("longitude")
            val latLng=LatLng(latitude,longitude)
            Log.d("Map","Saved Instance state is not null $latitude,$longitude")

            doGeoCoding(latLng)
        }
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val GWU = LatLng(38.0, -77.0)
        mMap.addMarker(MarkerOptions().position(GWU).title("GWU"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GWU))


        mMap.setOnMapLongClickListener { latLng ->
            Log.d("Map","Long clicked at ${latLng.latitude}, ${latLng.longitude}")
            currentLatitude=latLng.latitude
            currentLongitude=latLng.longitude
            doGeoCoding(latLng)

        }
    }
    private suspend fun getAddressesFromLocation(latitude: Double, longitude: Double,
                                                 context: Context ) :List<Address>?{

        return withContext(Dispatchers.IO){
            try{
                val geocoder= Geocoder(context)
                val addresses=geocoder.getFromLocation(latitude,longitude,5)
                addresses?.ifEmpty {
                    null
                }
            }catch (e: IOException){
                e.printStackTrace()
                listOf()
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Map", "inside onSaveInstanceState")

//        outState.putInt("", 5)
//        outState.putBoolean("", true)

        outState.putDouble("latitude", currentLatitude)
        outState.putDouble("longitude", currentLongitude)
    }
    private fun doGeoCoding(latLng:LatLng) {
        lifecycleScope.launch {
            val addresses=getAddressesFromLocation(
                latLng.latitude,
                latLng.longitude,
                this@MapsActivity
            )
            if (addresses.isNullOrEmpty()){
                Toast.makeText(this@MapsActivity,"No Results",Toast.LENGTH_SHORT).show()

            }else{
                currentAddress=addresses[0]
                val addressLine=currentAddress.getAddressLine(0)
                Log.d("Map","Current address is $addressLine")
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(addressLine)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                myButton.text=addressLine
                myButton.icon=ContextCompat.getDrawable(this@MapsActivity, R.drawable.baseline_check_24)
                myButton.setBackgroundColor(getColor(R.color.green))
                myButton.isEnabled= true

            }
            myButton.setOnClickListener {
                val yelpListingIntent= Intent(this@MapsActivity, YelpListings::class.java)
                //pass address
                yelpListingIntent.putExtra("address", currentAddress)
                startActivity(yelpListingIntent)
            }


        }
    }
}
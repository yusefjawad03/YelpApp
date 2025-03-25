package com.example.yelpapp

import YelpAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.location.Address
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class YelpListings : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myYelpText: EditText
    private lateinit var addNewYelpButton: FloatingActionButton
    private lateinit var firebaseDatabase: FirebaseDatabase

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_yelp_listings)

        recyclerView = findViewById(R.id.YelpRecyclerView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myYelpText = findViewById(R.id.yelpName)
        addNewYelpButton = findViewById(R.id.floatingActionButton)
        firebaseDatabase = FirebaseDatabase.getInstance()

        val address = intent.getParcelableExtra("address", Address::class.java)!!
        val addressLine = address.getAddressLine(0)
//        title="search $addressLine"

        val state=address.adminArea ?:"unknown"
        val reference=firebaseDatabase.getReference("YelpBusinesses/$state")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val yelps= mutableListOf<YelpBusiness>()
                snapshot.children.forEach{ childSnapshot: DataSnapshot->
                    val yelp: YelpBusiness?=childSnapshot.getValue(YelpBusiness::class.java)
                    if (yelp!=null){
                        yelps.add(yelp)
                    }
                }
                val adapter = YelpAdapter(yelps)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this@YelpListings)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("YelpFirebase", "Failed to connect", error.toException())
            }
        })

        addNewYelpButton.setOnClickListener {
            val restaurantName=myYelpText.text.toString()
            val email= FirebaseAuth.getInstance().currentUser!!.email!!
            val yelp=YelpBusiness(
                restaurantName=restaurantName,
                category=email,
                url="http...",
                rating=5.0,
                icon="..."
            )

            reference.push().setValue(yelp)
        }


//        titleTextView.text = title

//        val GWlat = 38.899
//        val GWlong = -77.040
//        val lat = address.latitude
//        val long = address.longitude
//        val apiKey = getString(R.string.yelpAPI)
//
//        val yelpManager = YelpManager()
//        var yelps = listOf<YelpBusiness>()
//
//        CoroutineScope(IO).launch {
//            yelps = yelpManager.retrieveYelps(lat, long, apiKey)
//
//            withContext(Main) {
//                val adapter = YelpAdapter(yelps)
//                recyclerView.adapter = adapter
//                recyclerView.layoutManager = LinearLayoutManager(this@YelpListings)
//            }
//        }
//    }
//
//    private fun getFakeYelpData(): List<YelpBusiness> {
//        return listOf(
//            YelpBusiness("Panera", "Breakfast", 4.2, "https://...", "none"),
//            YelpBusiness("WingsToGo", "Comfort", 5.3, "https://...", "none"),
//            YelpBusiness("Log Cabin", "Seafood", 4.2, "https://...", "none"),
//            YelpBusiness("Dunkin Donut", "Breakfast", 3.1, "https://...", "none"),
//            YelpBusiness("Starbucks", "Coffee", 3.1, "https://...", "none"),
//            YelpBusiness("Panera", "Breakfast", 3.1, "https://...", "none"),
//            YelpBusiness("Panera", "Breakfast", 3.1, "https://...", "none"),
//            YelpBusiness("Panera", "Breakfast", 2.8, "https://...", "none"),
//            YelpBusiness("Panera", "Breakfast", 5.3, "https://...", "none"),
//            YelpBusiness(
//                icon = "https://....",
//                category = "Dinner",
//                rating = 1.2,
//                restaurantName = "Subway",
//                url = "none"
//            )
//
//        )
    }
//
}
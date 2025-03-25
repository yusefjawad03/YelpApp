package com.example.yelpapp
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class CountryList : AppCompatActivity() {
    private lateinit var myCountryList: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_country_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        myCountryList=findViewById(R.id.countryList)
        var countryListData=resources.getStringArray(R.array.countries)
        var arrayAdapter=ArrayAdapter(this, android.R.layout.simple_list_item_1, countryListData)
        myCountryList.adapter=arrayAdapter
        myCountryList.setOnItemClickListener { parent, view, position, id ->
            val countryClicked = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "you clicked $countryClicked", Toast.LENGTH_LONG).show()
        }
    }
}
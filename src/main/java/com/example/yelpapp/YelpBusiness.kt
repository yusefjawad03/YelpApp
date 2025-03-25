package com.example.yelpapp

data class YelpBusiness(

    val restaurantName: String,
    val category: String,
    val rating: Double,
    val icon: String,
    val url:String
) {
    constructor():this("", "", 5.0, "", "")
}

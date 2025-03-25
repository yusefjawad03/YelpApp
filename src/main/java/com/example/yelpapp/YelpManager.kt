package com.example.yelpapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class YelpManager {
    val okHttpClient: OkHttpClient

    init{
        val builder=OkHttpClient.Builder()
        val loggingInterceptor=HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient=builder.build()

    }
    suspend fun retrieveYelps(latitude:Double, longitude:Double, apikey:String): List<YelpBusiness>
    {
        val request=Request.Builder()
            .url("https://api.yelp.com/v3/businesses/search?longitude=$longitude&latitude=$latitude")
            .header("authorization","Bearer $apikey")
            .get()
            .build()

        val response: Response =okHttpClient.newCall(request).execute()
        val responseBody=response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()){
            val yelps=mutableListOf<YelpBusiness>()
            val json= JSONObject(responseBody)
            val businesses=json.getJSONArray("businesses")
            for (i in 0 until businesses.length()) {
                val currentBusiness=businesses.getJSONObject(i)
                val name=currentBusiness.getString("name")
                val rating=currentBusiness.getDouble("rating")
                val icon=currentBusiness.getString("image_url")
                val categories=currentBusiness.getJSONArray("categories")
                val currentCategory=categories.getJSONObject(0)
                val title=currentCategory.getString("title")
                val url=currentBusiness.getString("url")

                val yelp=YelpBusiness(
                    restaurantName = name,
                    category=title,
                    rating=rating,
                    icon=icon,
                    url=url
                )
                yelps.add(yelp)
            }
            return yelps
        }else{
            return listOf()
        }

    }
}
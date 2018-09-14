package com.project.niko.fortakotlin.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.project.niko.fortakotlin.Adapter.AdapterRestaurant
import com.project.niko.fortakotlin.Helper.HelperAPI
import com.project.niko.fortakotlin.Helper.HelperAPI.Companion.get
import com.project.niko.fortakotlin.Model.Location
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.Model.UserRating
import com.project.niko.fortakotlin.R
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentHome : Fragment() {
    internal lateinit var context : Context
    private var adapterRestaurant: AdapterRestaurant? = null
    private var myLocation: Location? = null
    private val restaurantList = ArrayList<Restaurant>()
    private var tv_count: TextView? = null
    private var tv_location_name: TextView? = null
    private var rv_list_restorant: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        context = view.context
        initial(view)

        adapterRestaurant = AdapterRestaurant(restaurantList, context)
        rv_list_restorant!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_list_restorant!!.adapter = adapterRestaurant

        getRestaurantData()

        return view
    }

    private fun initial(view: View) {
        tv_location_name = view.findViewById(R.id.tv_location_name)
        rv_list_restorant = view.findViewById(R.id.rv_nearby_rest)
        tv_count = view.findViewById(R.id.tv_count)

        view.findViewById<View>(R.id.cv_search).setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
//                    val intent = Intent(context, ActivityViewResult::class.java)
//                    intent.putExtra("title", "Delivery")
//                    intent.putExtra("key", "category")
//                    intent.putExtra("time", System.currentTimeMillis())
//                    startActivity(intent)
        }
    }

    private fun getRestaurantData() {
        val params = RequestParams()
        params.put("lat", -6.907745)
        params.put("lon", 107.609444)

        val progressDialog = ProgressDialog.show(context, null, "Loading")

        get("geocode", params, object : JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val timeWithoutNetwork = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                myLocation = Location()
                try {
                    myLocation!!.address = (response!!.getJSONObject("location").getString("title") + ", "
                            + response.getJSONObject("location").getString("city_name"))
                    myLocation!!.latitude = (response.getJSONObject("location").getDouble("latitude"))
                    myLocation!!.longitude = (response.getJSONObject("location").getDouble("longitude"))
                    tv_location_name!!.text = myLocation!!.address

                    val restorants = response.getJSONArray("nearby_restaurants")
                    tv_count!!.text = restorants.length().toString() + " Restaurant"

                    restaurantList.forEach { eachRestaurant -> println(eachRestaurant.name) }

                    restorants.takeIf { eachRestaurant -> true }

                    for (i in 0 until restorants.length()) {
                        val restaurant = Restaurant()
                        restaurant.id = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("id"))
                        restaurant.name = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("name"))
                        restaurant.url = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("url"))

                        val location = Location()
                        location.address = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address"))
                        location.locality = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality"))
                        location.city = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("city"))
                        location.cityId = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("city_id"))
                        location.latitude = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("latitude"))
                        location.longitude = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("longitude"))
                        location.zipcode = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("zipcode"))
                        location.countryId = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("country_id"))
                        location.localityVerbose = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality_verbose"))
                        restaurant.location = (location)
                        restaurant.cuisines = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("cuisines"))
                        restaurant.averageCostForTwo = (restorants.getJSONObject(i).getJSONObject("restaurant").getInt("average_cost_for_two"))

                        val userRating = UserRating()
                        userRating.aggregateRating = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating"))
                        userRating.ratingText = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_text"))
                        userRating.ratingColor = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_color"))
                        userRating.votes = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("votes"))
                        restaurant.userRating = (userRating)

                        restaurant.photosUrl = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("photos_url"))
                        restaurant.featuredImage = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("featured_image"))

                        restaurantList.add(restaurant)
                    }

                    restaurantList
                            .filter { it.name!!.startsWith("a") }
                            .sortedBy { it.name }

                    adapterRestaurant!!.notifyDataSetChanged()
                    progressDialog.dismiss()
                    val currentTime = System.currentTimeMillis()
                    System.out.println("Total time home + network: " + (currentTime - (context as MainActivity).startTime))
                    System.out.println("Total time without network: " + (currentTime - timeWithoutNetwork))
                    println("total time " + operate(currentTime, timeWithoutNetwork))

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private val operate = { x: Long, y : Long -> x - y }

    private fun Sum(a : Int, b : Int): Int {
        if (a != 0) {
            return 1
        } else {
            return 0
        }
//        return a + b;
    }

    private fun nestedLoop() {
        for (i in 0..9) {
            for (j in 0 until i) {
                //
            }
        }
    }
}

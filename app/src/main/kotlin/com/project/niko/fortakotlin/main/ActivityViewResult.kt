package com.project.niko.fortakotlin.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.project.niko.fortakotlin.Adapter.AdapterRestaurant
import com.project.niko.fortakotlin.Helper.HelperAPI
import com.project.niko.fortakotlin.Model.Location
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.Model.UserRating
import com.project.niko.fortakotlin.R
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ActivityViewResult : AppCompatActivity() {

    private var rv_result_rest: RecyclerView? = null
    private var tv_result_count: TextView? = null
    private var progressBar: ProgressBar? = null
    private var adapterRestaurant: AdapterRestaurant? = null
    private val restaurantList = ArrayList<Restaurant>()

    internal var timeWithoutNetwork: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_result)
        supportActionBar!!.title = intent.getStringExtra("title")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initial()

        adapterRestaurant = AdapterRestaurant(restaurantList, this)
        rv_result_rest!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_result_rest!!.adapter = adapterRestaurant

        getRestaurantData(0)
        //Thread(Runnable { getRestaurantData(0) }).start()
    }

    private fun getRestaurantData(start: Int) {
        val params = RequestParams()
        params.put("lat", -6.907745)
        params.put("lon", 107.609444)
        params.put("start", start)
        params.put(intent.getStringExtra("key"), intent.getStringExtra("value"))

        progressBar!!.visibility = View.VISIBLE

        HelperAPI.get("search", params, object : JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val startTime = System.currentTimeMillis()
                println(response.toString())
                println("tasijfsdf " + Thread.currentThread().name)
                super.onSuccess(statusCode, headers, response)
                try {
                    val restorants = response!!.getJSONArray("restaurants")
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
                        restaurant.location = location
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

                    timeWithoutNetwork += System.currentTimeMillis() - startTime
                    if (restaurantList.size < 100) {
                        if (restaurantList.size > 80) {
                            getRestaurantData(0)
                        } else {
                            getRestaurantData(restaurantList.size)
                        }
                    } else {
                        tv_result_count!!.text = restaurantList.size.toString() + " Hasil"
                        adapterRestaurant!!.notifyDataSetChanged()
                        progressBar!!.visibility = View.GONE
                        val endTime = System.currentTimeMillis()
                        println("Time get" + intent.getStringExtra("key") + "() " + (endTime - intent.getLongExtra("time", System.currentTimeMillis())))
                        println("Time result without network: $timeWithoutNetwork")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                Toast.makeText(this@ActivityViewResult, "Request Timeout", Toast.LENGTH_SHORT).show()
            }

            override fun getUseSynchronousMode(): Boolean {
                return false
            }
        })
    }

    private fun initial() {
        rv_result_rest = this.findViewById(R.id.rv_search_result)
        tv_result_count = this.findViewById(R.id.tv_result_count)
        progressBar = this.findViewById(R.id.progressBar)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}

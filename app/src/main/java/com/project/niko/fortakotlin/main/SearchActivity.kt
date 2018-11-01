package com.project.niko.fortakotlin.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.project.niko.fortakotlin.Adapter.AdapterLocation
import com.project.niko.fortakotlin.Adapter.AdapterRestaurant
import com.project.niko.fortakotlin.Helper.HelperAPI.Companion.get
import com.project.niko.fortakotlin.Model.City
import com.project.niko.fortakotlin.Model.Location
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.Model.UserRating
import com.project.niko.fortakotlin.R
import kotlinx.android.synthetic.main.activity_search.*
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SearchActivity : AppCompatActivity() {
    private var adapterRestaurant: AdapterRestaurant? = null
    private val restaurantList = ArrayList<Restaurant>()

    private var adapterLocation: AdapterLocation? = null
    private val cityList = ArrayList<City>()

    private var executionTime : Long = 0
    private var loadTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        loadTime = System.currentTimeMillis()
        init()
        rv_res.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterLocation = AdapterLocation(cityList, this)
        rv_res.adapter = adapterLocation
        getCityDetail()
    }

    private fun init() {
        et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                adapterRestaurant = AdapterRestaurant(restaurantList, this)
                rv_res!!.adapter = adapterRestaurant

                restaurantList.clear()
                getRestaurantData(0)
            }
            false
        }
    }

    private fun getRestaurantData(start: Int) {
        progressBar!!.visibility = View.VISIBLE
        val params = RequestParams()
        params.put("q", et_search?.text.toString())
        params.put("start", start)
        params.put("lat", -6.907745)
        params.put("lon", 107.609444)

        get("search", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val startTime = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                try {
                    val restorants = response!!.getJSONArray("restaurants")
                    for (i in 0 until restorants.length()) {
                        val restaurant : Restaurant? = null
                        restaurant!!.id = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("id"))
                        restaurant.name = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("name"))
                        restaurant.url = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("url"))

                        val location : Location? = null
                        location!!.address = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address"))
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

                        val userRating : UserRating? = null
                        userRating!!.aggregateRating = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating"))
                        userRating.ratingText = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_text"))
                        userRating.ratingColor = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_color"))
                        userRating.votes = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("votes"))
                        restaurant.userRating = (userRating)

                        restaurant.photosUrl = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("photos_url"))
                        restaurant.featuredImage = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("featured_image"))

                        restaurantList.add(restaurant)
                    }

                    executionTime += System.currentTimeMillis() - startTime
                    if (restaurantList.size < 100) {
                        getRestaurantData(restaurantList.size)
                    } else {
                        adapterRestaurant!!.notifyDataSetChanged()
                        progressBar?.visibility = View.GONE
                        val elapsedTime = System.currentTimeMillis()
                        println("Time search() with network: " + (elapsedTime - loadTime))
                        println("Time search() without network: $executionTime")
                    }
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun getCityDetail() {
        val params = RequestParams()
        params.put("query", "jakarta")
        params.put("lat", -6.907745)
        params.put("lon", 107.609444)
        params.put("count", 10)

        get("locations", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val startTime = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                try {
                    val jsonArray = response!!.getJSONArray("location_suggestions")
                    for (i in 0 until jsonArray.length()) {
                        val city : City? = null
                        city!!.cityId = (jsonArray.getJSONObject(i).getString("entity_id"))
                        city.cityName = (jsonArray.getJSONObject(i).getString("title"))
                        city.entityType = (jsonArray.getJSONObject(i).getString("entity_type"))
                        city.country = (jsonArray.getJSONObject(i).getString("country_name"))

                        cityList.add(city)
                    }
                    adapterLocation!!.notifyDataSetChanged()
                    executionTime += System.currentTimeMillis() - startTime
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun getLocation(city: City) {
        progressBar!!.visibility = View.VISIBLE
        adapterRestaurant = AdapterRestaurant(restaurantList, this@SearchActivity)
        rv_res!!.adapter = adapterRestaurant

        val params = RequestParams()
        params.put("entity_id", city.cityId)
        params.put("entity_type", city.entityType)

        get("location_details", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val startTime = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                try {
                    val restorants = response!!.getJSONArray("best_rated_restaurant")
                    for (i in 0 until restorants.length()) {
                        val restaurant : Restaurant? = null
                        restaurant!!.id = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("id"))
                        restaurant.name = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("name"))
                        restaurant.url = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("url"))

                        val location : Location? = null
                        location!!.address = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address"))
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

                        val userRating : UserRating? = null
                        userRating!!.aggregateRating = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating"))
                        userRating.ratingText = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_text"))
                        userRating.ratingColor = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_color"))
                        userRating.votes = (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("votes"))
                        restaurant.userRating = (userRating)

                        restaurant.photosUrl = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("photos_url"))
                        restaurant.featuredImage = (restorants.getJSONObject(i).getJSONObject("restaurant").getString("featured_image"))

                        restaurantList.add(restaurant)
                    }

                    adapterRestaurant!!.notifyDataSetChanged()
                    progressBar!!.visibility = View.GONE
                    val endtime = System.currentTimeMillis()
                    executionTime += endtime - startTime
                    println("Time getByCity with network : " + (endtime - loadTime))
                    println("Time getByCity without network : $executionTime")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                Toast.makeText(this@SearchActivity, "Request Timeout", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null &&
                (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
                v is EditText &&
                !v.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.left - scrcoords[0]
            val y = ev.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }
}

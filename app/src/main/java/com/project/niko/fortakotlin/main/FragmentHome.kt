package com.project.niko.fortakotlin.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.project.niko.fortakotlin.Adapter.AdapterRestaurant
import com.project.niko.fortakotlin.Helper.HelperAPI
import com.project.niko.fortakotlin.Model.Location
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.Model.UserRating
import com.project.niko.fortakotlin.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentHome : Fragment() {
    internal lateinit var context : Context
    private var adapterRestaurant: AdapterRestaurant? = null
    private lateinit var myLocation: Location
    private val restaurantList = ArrayList<Restaurant>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        context = view.context
        initial(view)

        adapterRestaurant = AdapterRestaurant(restaurantList, context)
        view.rv_nearby_rest.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.rv_nearby_rest.adapter = adapterRestaurant

        getRestaurantData(view)

        return view
    }

    private fun initial(view: View) {
        view.cv_search.setOnClickListener{
            startActivity(Intent(context, SearchActivity::class.java))
//                    val intent = Intent(context, ActivityViewResult::class.java)
//                    intent.putExtra("title", "Delivery")
//                    intent.putExtra("key", "category")
//                    intent.putExtra("time", System.currentTimeMillis())
//                    startActivity(intent)
        }
    }

    private fun getRestaurantData(view: View) {
        val params = RequestParams()
        params.put("lat", -6.907745)
        params.put("lon", 107.609444)

        val progressDialog = ProgressDialog.show(context, null, "Loading")

        HelperAPI.get("geocode", params, object : JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val timeWithoutNetwork = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                myLocation = Location("","","",0,0.0,0.0,"",0,"")
                try {
                    myLocation.address = (response!!.getJSONObject("location").getString("title") + ", "
                            + response.getJSONObject("location").getString("city_name"))
                    myLocation.latitude = (response.getJSONObject("location").getDouble("latitude"))
                    myLocation.longitude = (response.getJSONObject("location").getDouble("longitude"))
                    view.tv_location_name!!.text = myLocation.address

                    val restorants = response.getJSONArray("nearby_restaurants")
                    view.tv_count!!.text = restorants.length().toString() + " Restaurant"

                    for (i in 0 until restorants.length()) {
                        val location = Location(
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("city")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("city_id")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("latitude")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("longitude")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("zipcode")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("country_id")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality_verbose")))

                        val userRating = UserRating(
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_text")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_color")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("votes")))

                        val restaurant = Restaurant(
                        (restorants.getJSONObject(i).getJSONObject("restaurant").getString("id")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getString("name")),
                            (restorants.getJSONObject(i).getJSONObject("restaurant").getString("url")),
                             (location),
                        (restorants.getJSONObject(i).getJSONObject("restaurant").getString("cuisines")),
                        (restorants.getJSONObject(i).getJSONObject("restaurant").getInt("average_cost_for_two")),
                                0, "IDR", "",
                        (userRating),
                        (restorants.getJSONObject(i).getJSONObject("restaurant").getString("photos_url")), "",
                        (restorants.getJSONObject(i).getJSONObject("restaurant").getString("featured_image")))

                        restaurantList.add(restaurant)
                    }

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
}

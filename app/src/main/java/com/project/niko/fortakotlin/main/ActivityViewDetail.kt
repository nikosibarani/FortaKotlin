package com.project.niko.fortakotlin.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.*
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.project.niko.fortakotlin.Adapter.AdapterDailyMenu
import com.project.niko.fortakotlin.Adapter.AdapterReview
import com.project.niko.fortakotlin.Helper.HelperAPI
import com.project.niko.fortakotlin.Model.DailyMenu
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.Model.User
import com.project.niko.fortakotlin.Model.UserReview
import com.project.niko.fortakotlin.R
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@SuppressLint("Registered")
class ActivityViewDetail : AppCompatActivity(), ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    private var slider: SliderLayout? = null

    private var tv_userRating: TextView? = null
    private var tv_restaurant_name: TextView? = null
    private var tv_restorant_address: TextView? = null
    private var tv_restorant_cuisines: TextView? = null
    private var tv_average_cost: TextView? = null
    private var tv_menu_no_data: TextView? = null
    private var tv_review : TextView? = null
    private var rv_daily_menu: RecyclerView? = null
    private var rv_review: RecyclerView? = null

    private var ln_review: LinearLayout? = null
    private var img_arrow: ImageView? = null
    private var progressBar: ProgressBar? = null

    private val userReviewList = ArrayList<UserReview>()
    private val dailyMenuList = ArrayList<DailyMenu>()

    private var reviewAdapter: AdapterReview? = null
    private val adapterDailyMenu: AdapterDailyMenu? = null

    var progressDialog: ProgressDialog? = null

    private var showReview = false
    internal var startTime: Long = 0
    private var loadTime : Long = 0

    internal var restaurant = Restaurant()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTime = System.currentTimeMillis()
        setContentView(R.layout.activity_view_detail)

        init()

        reviewAdapter = AdapterReview(userReviewList, this)
        rv_review!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_review!!.adapter = reviewAdapter
        rv_daily_menu!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        getDetail()
    }

    private fun init() {
        slider = this.findViewById(R.id.slider)
        tv_average_cost = this.findViewById(R.id.tv_average_cost)
        tv_restorant_address = this.findViewById(R.id.tv_restorant_address)
        tv_restorant_cuisines = this.findViewById(R.id.tv_restaurant_cuisines)
        tv_userRating = this.findViewById(R.id.tv_userRating)
        rv_daily_menu = this.findViewById(R.id.rv_daily_menu)
        rv_review = this.findViewById(R.id.rv_review)
        tv_restaurant_name = this.findViewById(R.id.tv_restorant_name)
        tv_menu_no_data = this.findViewById(R.id.tv_menu_no_data)
        tv_review = this.findViewById(R.id.tv_review)
        ln_review = this.findViewById(R.id.ln_review)
        img_arrow = this.findViewById(R.id.img_arrow)
        progressBar = this.findViewById(R.id.progressBar)

        this.findViewById<View>(R.id.btn_direction).setOnClickListener {
            val intent = Intent(this@ActivityViewDetail, MapsActivit::class.java)
            intent.putExtra("originLat", -6.907745)
            intent.putExtra("originLng", 107.609444)
            intent.putExtra("restaurant", restaurant)
            startActivity(intent)
        }

        ln_review?.setOnClickListener {
            if (showReview) {
                showReview = false
                img_arrow?.startAnimation(collapse())
                rv_review?.visibility = View.GONE
            } else {
                showReview = true
                img_arrow?.startAnimation(expand())
                progressBar?.visibility = View.VISIBLE
                rv_review?.visibility = View.VISIBLE
                userReviewList.clear()
                getAllReviews()
            }
        }
    }

    private fun getDetail() {
        progressDialog = ProgressDialog.show(this, null, "Loading")
        val params = RequestParams()
        params.put("res_id", intent.getStringExtra("res_id"))
        HelperAPI.get("restaurant", params, object : JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val exeTime = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                try {
                    restaurant.name = (response!!.getString("name"))
                    val location = com.project.niko.fortakotlin.Model.Location()
                    location.address = (response.getJSONObject("location").getString("address"))
                    location.latitude = (response.getJSONObject("location").getDouble("latitude"))
                    location.longitude = (response.getJSONObject("location").getDouble("longitude"))
                    restaurant.location = location
                    tv_restaurant_name!!.text = response.getString("name")
                    tv_restorant_address!!.text = response.getJSONObject("location").getString("address")
                    tv_restorant_cuisines!!.text = response.getString("cuisines")
                    tv_average_cost!!.text = (response.getString("average_cost_for_two") + " " + response.getString("currency")
                            + " for two people (approx.)")
                    tv_userRating!!.text = response.getJSONObject("user_rating").getString("aggregate_rating")
                    tv_userRating!!.setBackgroundColor(Color.parseColor("#" + response.getJSONObject("user_rating").getString("rating_color")))
                    restaurant.featuredImage = (response.getString("featured_image"))
                    initSlider(response.getString("featured_image"))
                    //getDailyMenu()

                    tv_menu_no_data!!.setVisibility(View.VISIBLE)
                    startTime += System.currentTimeMillis() - exeTime
                    getAllReviews()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun getDailyMenu() {
        val params = RequestParams()
        params.put("res_id", intent.getStringExtra("res_id"))
        HelperAPI.get("dailymenu", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
                //DailyMenu dailyMenu = new DailyMenu();
                println("Daily menu " + response!!)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                tv_menu_no_data!!.visibility = View.VISIBLE
            }
        })
    }

    private fun getAllReviews() {
        val params = RequestParams()
        params.put("res_id", intent.getStringExtra("res_id"))
        params.put("start", 0)
        params.put("count", 0)

        HelperAPI.get("reviews", params, object : JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val exeTime = System.currentTimeMillis()
                super.onSuccess(statusCode, headers, response)
                try {
                    for (i in 0 until response!!.getJSONArray("user_reviews").length()) {
                        tv_review?.text = "Review (" + response.getInt("reviews_shown") + "/" + response.getInt("reviews_count") + ")"
                        val review = UserReview()
                        review.rating = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("rating")
                        review.reviewText = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("review_text")
                        review.id = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("id")
                        review.ratingColor = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("rating_color")
                        review.reviewTimeFriendly = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("review_time_friendly")
                        review.ratingText = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("rating_text")
                        review.timestamp = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("timestamp")
                        review.likes = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("likes")
                        val user = User()
                        user.name = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getJSONObject("user").getString("name")
                        user.profileImage = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getJSONObject("user").getString("profile_image")
                        review.user = user
                        review.commentsCount = response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("comments_count")
                        userReviewList.add(review)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                reviewAdapter!!.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
                val endtime = System.currentTimeMillis()
                startTime += endtime - exeTime
                progressDialog!!.dismiss()
                println("details with network " + (endtime - loadTime))
                println("Total time details() without network: $startTime")
            }
        })
    }

    private fun initSlider(img : String) {
        val defaultSliderView = DefaultSliderView(this)
        defaultSliderView
                .image(img)
                .setScaleType(BaseSliderView.ScaleType.Fit)
                .setOnSliderClickListener { slider -> Toast.makeText(this@ActivityViewDetail, "photo", Toast.LENGTH_SHORT).show()}

        slider!!.addSlider(defaultSliderView)
        slider!!.setPresetTransformer(SliderLayout.Transformer.ZoomOut)
        slider!!.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        slider!!.setCustomAnimation(DescriptionAnimation())
        slider!!.setDuration(4000)
        slider!!.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onSliderClick(slider: BaseSliderView) {

    }

    // expand arrow animation
    fun expand(): RotateAnimation {
        val rotate = RotateAnimation(0f, 90f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        return rotate
    }

    //collapse arrow animation
    fun collapse(): RotateAnimation {
        val rotate = RotateAnimation(90f, 0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        return rotate
    }
}

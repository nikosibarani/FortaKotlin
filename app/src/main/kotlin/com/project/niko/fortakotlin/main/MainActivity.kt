package com.project.niko.fortakotlin.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.project.niko.fortakotlin.Adapter.AdapterCategory
import com.project.niko.fortakotlin.Helper.HelperAPI
import com.project.niko.fortakotlin.Model.Category
import com.project.niko.fortakotlin.Model.Location
import com.project.niko.fortakotlin.R
import com.project.niko.fortakotlin.R.id.container
import org.apache.http.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private val location: Location? = null

    private var rv_nav_category: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private val tv_location_name: TextView? = null

    private val categoryList = ArrayList<Category>()

    private var adapterCategory: AdapterCategory? = null

    private var menu: Menu? = null
    var executionTime: Long = 0
    var startTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis()
        setContentView(R.layout.activity_main)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                startTime = System.currentTimeMillis()
                categoryList.clear()
                getCategoryData()
            }
        }
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        initial()

        adapterCategory = AdapterCategory(categoryList, this)
        rv_nav_category?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_nav_category?.adapter = adapterCategory

        //start fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(container, FragmentHome()).commit()
    }


    private fun initial() {
        rv_nav_category = this.findViewById(R.id.rv_category_nav)
        progressBar = this.findViewById(R.id.progressBar)
    }

    private fun getCategoryData(){
        val startTime = System.currentTimeMillis()
        progressBar?.visibility = View.VISIBLE
        HelperAPI.get("categories", null, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
                val arrayCategory: JSONArray
                try {
                    arrayCategory = response!!.getJSONArray("categories")
                    for (i in 0 until arrayCategory.length()) {
                        val category = Category(arrayCategory.getJSONObject(i).getJSONObject("categories").getString("id"),
                                arrayCategory.getJSONObject(i).getJSONObject("categories").getString("name"))
                        categoryList.add(category)
                    }
                    adapterCategory?.notifyDataSetChanged()
                    progressBar?.visibility = View.GONE
                    executionTime += System.currentTimeMillis() - startTime
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                Toast.makeText(this@MainActivity, "Request Timeout", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu_container; this adds items to the action bar if it is present.
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //notification
        val refresh = menu.findItem(R.id.refresh)
        val rootView = refresh.actionView as FrameLayout

        rootView.setOnClickListener { onOptionsItemSelected(refresh) }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                finish()
                val i = applicationContext.packageManager
                        .getLaunchIntentForPackage(this.packageName)!!
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

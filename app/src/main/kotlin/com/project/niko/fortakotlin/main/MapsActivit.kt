package com.project.niko.fortakotlin.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.project.niko.fortakotlin.Helper.DirectionJSONParser
import com.project.niko.fortakotlin.Helper.PicassoClient
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MapsActivit : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    private var restaurant = Restaurant()

    internal var startTime: Long = 0
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        startTime = System.currentTimeMillis()
        restaurant = intent.getSerializableExtra("restaurant") as Restaurant

        init()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // mMap.setMyLocationEnabled(true);
    }

    private fun init() {
        val tv_restorant_name = this.findViewById<TextView>(R.id.tv_restorant_name)
        val tv_restorant_cuisines = this.findViewById<TextView>(R.id.tv_restorant_cuisines)
        val tv_restorant_address = this.findViewById<TextView>(R.id.tv_restorant_address)
        val img = this.findViewById<ImageView>(R.id.img_photo)

        tv_restorant_name.text = restaurant.name
        tv_restorant_cuisines.text = restaurant.cuisines
        tv_restorant_address.text = restaurant.location!!.address
        PicassoClient.downloadImage(this, restaurant.featuredImage!!, img)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker and move the camera
        val origin = LatLng(intent.getDoubleExtra("originLat", 0.0), intent.getDoubleExtra("originLng", 0.0))
        mMap!!.addMarker(MarkerOptions().position(origin).title("Your Position")).showInfoWindow()
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(origin))
        mMap!!.setMinZoomPreference(15f)
        val dest = LatLng(restaurant.location!!.latitude!!, restaurant.location!!.longitude!!)
        mMap!!.addMarker(MarkerOptions().position(dest).title(restaurant.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        //        LatLng origin = new LatLng(-6.872029, 107.574092);
        //        System.out.println("destinatiaotio" + getIntent().getStringExtra("destLat"));
        //        LatLng dest = new LatLng(getIntent().getDoubleExtra("destLat", 0), getIntent().getDoubleExtra("destLng", 0));

        val url = getDirectionsUrl(origin, dest)

        val downloadTask = DownloadTask()

        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor"

        // Output format
        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    @SuppressLint("LongLogTag")
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.inputStream

            val sb = StringBuilder()

            val reader = iStream.bufferedReader()
            val iterator = reader.lineSequence().iterator()
            while(iterator.hasNext()) {
                val line = iterator.next()
                sb.append(line)
            }
            reader.close()

            data = sb.toString()

        } catch (e: Exception) {
            Log.d("Exception while downloading url", e.toString())
        } finally {
            iStream!!.close()
            assert(urlConnection != null)
            urlConnection!!.disconnect()
        }
        return data
    }

    // Fetches data from url passed
    @SuppressLint("StaticFieldLeak")
    private inner class DownloadTask : AsyncTask<String, Void, String>() {

        // Downloading data in non-ui thread
        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }

            println("DATA $data")
            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask()
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionJSONParser()

                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>? = null
            var lineOptions: PolylineOptions? = null
            //            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path = result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]

                    val lat = java.lang.Double.parseDouble(point["lat"])
                    val lng = java.lang.Double.parseDouble(point["lng"])
                    val position = LatLng(lat, lng)

                    points.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(10f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap!!.addPolyline(lineOptions)
                val elapsedTime = System.currentTimeMillis() - startTime
                println("Total time Maps Direction(): $elapsedTime")
            } else {
                Toast.makeText(this@MapsActivit, "Direction not found", Toast.LENGTH_SHORT).show()
            }

        }
    }
}

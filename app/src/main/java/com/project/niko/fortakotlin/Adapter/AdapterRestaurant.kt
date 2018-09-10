package com.project.niko.fortakotlin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.project.niko.fortakotlin.main.ActivityViewDetail
import com.project.niko.fortakotlin.Helper.PicassoClient
import com.project.niko.fortakotlin.Model.Restaurant
import com.project.niko.fortakotlin.R

class AdapterRestaurant(private val restaurantList: List<Restaurant>, private val context: Context) :
        RecyclerView.Adapter<AdapterRestaurant.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRestaurant.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_restaurant, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterRestaurant.MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tv_restorant_name.text = restaurantList[position].name
        holder.tv_restourant_cousines.text = restaurantList[position].cuisines
        holder.tv_restourant_address.text = restaurantList[position].location!!.localityVerbose
        holder.tv_price_approx.text = "Rp." + restaurantList[position].averageCostForTwo + " for two people(approx..)"
        holder.tv_rating.text = restaurantList[position].userRating!!.aggregateRating
        holder.tv_rating.setBackgroundColor(Color.parseColor("#" + restaurantList[position].userRating!!.ratingColor!!))
        if (!restaurantList[position].featuredImage!!.isEmpty()) {
            PicassoClient.downloadImage(context, restaurantList[position].featuredImage!!, holder.img_photo)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ActivityViewDetail::class.java)
            intent.putExtra("res_id", restaurantList[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_restorant_name: TextView
        val tv_restourant_cousines: TextView
        val tv_restourant_address: TextView
        val tv_price_approx: TextView
        val tv_rating: TextView
        val img_photo: ImageView

        init {
            tv_restorant_name = itemView.findViewById(R.id.tv_restorant_name)
            tv_restourant_cousines = itemView.findViewById(R.id.tv_restorant_cuisines)
            tv_restourant_address = itemView.findViewById(R.id.tv_restorant_address)
            tv_price_approx = itemView.findViewById(R.id.tv_price_approx)
            img_photo = itemView.findViewById(R.id.img_photo)
            tv_rating = itemView.findViewById(R.id.tv_rating)
        }
    }
}

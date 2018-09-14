package com.project.niko.fortakotlin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.niko.fortakotlin.Model.City
import com.project.niko.fortakotlin.R
import com.project.niko.fortakotlin.main.SearchActivity

class AdapterLocation(private val cityList: List<City>, private val context: Context) : RecyclerView.Adapter<AdapterLocation.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLocation.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_location, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterLocation.MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tv_city.setText(cityList[position].cityName)
        holder.tv_country.setText(cityList[position].cityName)

        holder.itemView.setOnClickListener { (context as SearchActivity).getLocation(cityList[position]) }
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_city: TextView
        val tv_country: TextView

        init {
            tv_city = itemView.findViewById(R.id.tv_city)
            tv_country = itemView.findViewById(R.id.tv_country)
        }
    }
}

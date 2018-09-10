package com.project.niko.fortakotlin.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.project.niko.fortakotlin.main.ActivityViewResult
import com.project.niko.fortakotlin.main.MainActivity
import com.project.niko.fortakotlin.Model.Category
import com.project.niko.fortakotlin.R

class AdapterCategory(private val categoryList: List<Category>, private val context: Context) : RecyclerView.Adapter<AdapterCategory.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategory.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_category, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterCategory.MyViewHolder, position: Int) {
        holder.tv_category_name.text = categoryList[position].category_name

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.putExtra("title", categoryList[position].category_name)
            intent.putExtra("key", "category")
            intent.putExtra("value", categoryList[position].category_id)
            intent.putExtra("time", (context as MainActivity).startTime)
            intent.setClass(context, ActivityViewResult::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_category_name: TextView

        init {
            tv_category_name = itemView.findViewById(R.id.tv_category_name)
        }
    }
}

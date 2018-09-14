package com.project.niko.fortakotlin.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.project.niko.fortakotlin.Helper.PicassoClient

import com.project.niko.fortakotlin.Model.UserReview
import com.project.niko.fortakotlin.R

class AdapterReview(private val userReviewList: List<UserReview>, private val context: Context)
    : RecyclerView.Adapter<AdapterReview.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_review, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_name.text = userReviewList[position].user!!.name
        holder.tv_time.text = userReviewList[position].reviewTimeFriendly
        holder.tv_review_text.text = userReviewList[position].reviewText
        PicassoClient.downloadImage(context, userReviewList[position].user!!.profileImage!!, holder.img_photo)
    }

    override fun getItemCount(): Int {
        return userReviewList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img_photo: ImageView
        val tv_name: TextView
        val tv_time: TextView
        val tv_review_text: TextView

        init {
            img_photo = itemView.findViewById(R.id.img_photo)
            tv_name = itemView.findViewById(R.id.tv_user_name)
            tv_time = itemView.findViewById(R.id.tv_review_time)
            tv_review_text = itemView.findViewById(R.id.tv_review_teks)
        }
    }
}

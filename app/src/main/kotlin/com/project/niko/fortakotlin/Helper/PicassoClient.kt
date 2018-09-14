package com.project.niko.fortakotlin.Helper

import android.content.Context
import android.widget.ImageView

import com.project.niko.fortakotlin.R
import com.squareup.picasso.Picasso

object PicassoClient {
    fun downloadImage(context: Context, url: String, img: ImageView) {
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.no_image_product_detail)
                .into(img)
    }
}

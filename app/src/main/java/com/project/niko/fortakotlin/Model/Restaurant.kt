package com.project.niko.fortakotlin.Model

import java.io.Serializable

data class Restaurant (
    var id: String,
    var name: String,
    var url: String,
    var location: Location,
    var cuisines: String,
    var averageCostForTwo: Int,
    var priceRange: Int,
    var currency: String,
    var thumb: String?,
    var userRating: UserRating,
    var photosUrl: String,
    var menuUrl: String,
    var featuredImage: String
) : Serializable

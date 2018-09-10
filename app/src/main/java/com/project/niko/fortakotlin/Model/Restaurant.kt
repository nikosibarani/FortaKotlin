package com.project.niko.fortakotlin.Model

import java.io.Serializable

class Restaurant : Serializable{
    var id: String? = null
    var name: String? = null
    var url: String? = null
    var location: Location? = null
    var cuisines: String? = null
    var averageCostForTwo: Int? = null
    var priceRange: Int? = null
    var currency: String? = null
    var thumb: String? = null
    var userRating: UserRating? = null
    var photosUrl: String? = null
    var menuUrl: String? = null
    var featuredImage: String? = null
}

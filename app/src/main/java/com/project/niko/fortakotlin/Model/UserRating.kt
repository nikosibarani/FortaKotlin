package com.project.niko.fortakotlin.Model

import java.io.Serializable

class UserRating : Serializable {
    var aggregateRating: String? = null
    var ratingText: String? = null
    var ratingColor: String? = null
    var votes: String? = null
}

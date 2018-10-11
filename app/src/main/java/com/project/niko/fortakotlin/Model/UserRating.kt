package com.project.niko.fortakotlin.Model

import java.io.Serializable

data class UserRating (
    var aggregateRating: String,
    var ratingText: String,
    var ratingColor: String,
    var votes: String
) : Serializable
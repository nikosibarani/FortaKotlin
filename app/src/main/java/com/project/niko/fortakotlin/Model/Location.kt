package com.project.niko.fortakotlin.Model

import java.io.Serializable

data class Location (
    var address: String,
    var locality: String,
    var city: String,
    var cityId: Int,
    var latitude: Double,
    var longitude: Double,
    var zipcode: String,
    var countryId: Int,
    var localityVerbose: String
) : Serializable

package com.project.niko.fortakotlin.Model

import java.io.Serializable

class Location: Serializable {
    var address: String? = null
    var locality: String? = null
    var city: String? = null
    var cityId: Int? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var zipcode: String? = null
    var countryId: Int? = null
    var localityVerbose: String? = null
}

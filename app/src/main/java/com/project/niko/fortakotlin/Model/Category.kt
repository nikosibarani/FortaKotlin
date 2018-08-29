package com.project.niko.fortakotlin.Model

class Category {

    var category_id: String? = null
    var category_name: String? = null

    constructor() {}

    constructor(category_id: String, category_name: String) {
        this.category_id = category_id
        this.category_name = category_name
    }
}

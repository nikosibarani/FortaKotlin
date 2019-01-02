package com.project.niko.fortakotlin.Model

data class DailyMenu(
        var dailyMenuId: String,var name: String,var startDate: String,var endDate: String,var dishes: List<Dish>
)

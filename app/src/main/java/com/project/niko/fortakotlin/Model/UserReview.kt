package com.project.niko.fortakotlin.Model

data class UserReview(
        var rating: Int,
        var reviewText: String,
        var id: String,
        var ratingColor: String,
        var reviewTimeFriendly: String,
        var ratingText: String,
        var timestamp: Int,
        var likes: Int,
        var user: User,
        var commentsCount: Int
)

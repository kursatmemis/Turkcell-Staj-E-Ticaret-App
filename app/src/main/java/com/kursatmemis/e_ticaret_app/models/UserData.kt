package com.kursatmemis.e_ticaret_app.models

/**
 * The object of UserData will use for the response of the post method for login.
 */
data class UserData (
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val token: String
)

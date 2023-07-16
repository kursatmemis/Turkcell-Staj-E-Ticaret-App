package com.kursatmemis.e_ticaret_app.models

data class UserProfileInfo(
    val image: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: Address,
    val birthDate: String,
    val gender: String
)

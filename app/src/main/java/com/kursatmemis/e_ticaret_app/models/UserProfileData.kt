package com.kursatmemis.e_ticaret_app.models

data class UserProfileData(
    val image: String?="",
    val firstName: String?="",
    val lastName: String?="",
    val phone: String?="",
    val address: Address?=Address("","",Coordinates(0.0, 0.0), "", ""),
    val birthDate: String?="",
    val gender: String?=""
)

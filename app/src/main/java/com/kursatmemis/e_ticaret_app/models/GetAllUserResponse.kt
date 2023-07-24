package com.kursatmemis.e_ticaret_app.models

data class GetAllUserResponse (
    val users: List<UserAllData>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

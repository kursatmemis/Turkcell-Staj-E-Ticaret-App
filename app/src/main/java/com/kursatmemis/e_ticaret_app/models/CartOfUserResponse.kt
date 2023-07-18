package com.kursatmemis.e_ticaret_app.models

data class CartOfUserResponse (
    val carts: List<Cart>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

data class Cart (
    val id: Long,
    val products: List<ProductOfCart>,
    val total: Long,
    val discountedTotal: Long,
    val userID: Long,
    val totalProducts: Long,
    val totalQuantity: Long
)

    data class ProductOfCart (
        val id: Long,
        val title: String,
        val price: Long,
        val quantity: Long,
        val total: Long,
        val discountPercentage: Double,
        val discountedPrice: Long
    )

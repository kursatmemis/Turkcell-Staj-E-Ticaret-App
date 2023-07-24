package com.kursatmemis.e_ticaret_app.models

data class CartToBeAdded(
    val userId: Long,
    val products: List<ProductToBeAdded>
)

data class ProductToBeAdded(
    val id: Long,
    val quantity: Long
)

data class AddedToBeCartResponse (
    val id: Long,
    val products: List<ProductCart>,
    val total: Long,
    val discountedTotal: Long,
    val userID: Long,
    val totalProducts: Long,
    val totalQuantity: Long
)

data class ProductCart (
    val id: Long,
    val title: String,
    val price: Long,
    val quantity: Long,
    val total: Long,
    val discountPercentage: Double,
    val discountedPrice: Long
)
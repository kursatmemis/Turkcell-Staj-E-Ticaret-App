package com.kursatmemis.e_ticaret_app.models

data class CartOfUserResponse(
    val carts: List<Cart>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

data class Cart(
    val id: Long,
    val products: List<ProductInCart>,
    val total: Long,
    val discountedTotal: Long,
    val userID: Long,
    val totalProducts: Long,
    val totalQuantity: Long
)

data class ProductInCart(
    var img: String = "",
    var id: Long? = 0,
    var title: String? = "",
    var desc: String? = "",
    var price: Long? = 0,
    var quantity: Long? = 0,
    var total: Long? = 0,
    val discountPercentage: Double? = 0.0,
    val discountedPrice: Long? = 0
)

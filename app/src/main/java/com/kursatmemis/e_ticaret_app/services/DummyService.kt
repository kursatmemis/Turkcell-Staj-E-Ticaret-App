package com.kursatmemis.e_ticaret_app.services

import com.kursatmemis.e_ticaret_app.models.CartOfUserResponse
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DummyService {

    @POST("auth/login")
    fun login(@Body user: User): Call<UserData>

    @GET("products")
    fun getAllProducts(): Call<ProductResponse>

    @GET("products/categories")
    fun getCategoryNames(): Call<List<String>>

    @GET("products/category/{endpoint}")
    fun getProductsOfCategory(@Path("endpoint") categoryName: String): Call<ProductResponse>

    @GET("carts/user/{endpoint}")
    fun getCartOfUser(@Path("endpoint") userId: String): Call<CartOfUserResponse>

    @GET("users/{endpoint}")
    fun getUserInfo(@Path("endpoint") userId: String): Call<UserResponse>
}
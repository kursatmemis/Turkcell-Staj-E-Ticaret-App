package com.kursatmemis.e_ticaret_app.services

import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.CartOfUserResponse
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.models.UserProfileInfo
import com.kursatmemis.e_ticaret_app.models.UserDetail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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
    fun getCartOfUser(@Path("endpoint") userId: Long): Call<CartOfUserResponse>

    @GET("users/{endpoint}")
    fun getUserInfos(@Path("endpoint") userId: Long = MainActivity.userId): Call<UserDetail>

    @PUT("users/{endpoint}")
    fun updateUserInfo(@Path("endpoint") userId: Long, @Body profileInfo: UserProfileInfo): Call<UserDetail>

    @POST("carts/add")
    fun addNewCart(@Body cartToBeAdded: CartToBeAdded): Call<AddedToBeCartResponse>
}
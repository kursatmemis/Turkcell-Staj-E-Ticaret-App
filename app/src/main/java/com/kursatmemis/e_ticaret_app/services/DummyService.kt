package com.kursatmemis.e_ticaret_app.services

import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.CartOfUserResponse
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import com.kursatmemis.e_ticaret_app.models.UpdatedUser
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import com.kursatmemis.e_ticaret_app.models.UserAllData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    fun getUserAllData(@Path("endpoint") userId: Long = MainActivity.userId!!.toLong()): Call<UserAllData>

    @PUT("users/{endpoint}")
    fun updateUserInfo(@Path("endpoint") userId: Long, @Body profileInfo: UserProfileData): Call<UserAllData>

    @POST("carts/add")
    fun addNewCart(@Body cartToBeAdded: CartToBeAdded): Call<AddedToBeCartResponse>

    @GET("products/search")
    fun searchProduct(@Query("q") query: String) : Call<ProductResponse>

    @GET("users")
    fun getAllUsers(): Call<GetAllUserResponse>

    @PUT("users/{endpoint}")
    fun updateUser(@Path("endpoint") userId: Long, @Body updatedUser: UpdatedUser): Call<UserAllData>

    @GET("products/{endpoint}")
    fun getSingleProduct(@Path("endpoint") productId: Long): Call<Product>

}


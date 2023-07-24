package com.kursatmemis.e_ticaret_app.managers

import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserAllData
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import com.kursatmemis.e_ticaret_app.services.DummyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object RetrofitManager {
    private val dummyService = ApiClient.getClient().create(DummyService::class.java)

    fun login(user: User, callback: CallBack<UserData>) {

        dummyService.login(user).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                val userData = response.body()
                if (userData != null) {
                    callback.onSuccess(userData)
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                callback.onFailure()
            }

        })

    }

    fun getAllUser(callback: CallBack<GetAllUserResponse>) {

        dummyService.getAllUsers().enqueue(object : Callback<GetAllUserResponse> {
            override fun onResponse(
                call: Call<GetAllUserResponse>,
                response: Response<GetAllUserResponse>
            ) {
                val getAllUserResponse = response.body()
                if (getAllUserResponse != null) {
                    callback.onSuccess(getAllUserResponse)
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<GetAllUserResponse>, t: Throwable) {
                callback.onFailure()
            }

        })
    }

    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = dummyService.getAllProducts().awaitResponse()
            if (response.isSuccessful) {
                response.body()?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductsInCart(): List<ProductInCart> {
        return try {
            val response =
                dummyService.getCartOfUser(MainActivity.userId!!.toLong()).awaitResponse()
            if (response.isSuccessful) {
                response.body()?.carts?.get(0)?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategoryNames(): List<String> {
        return try {
            val response = dummyService.getCategoryNames().awaitResponse()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchProduct(query: String): List<Product> {
        return try {
            val response = dummyService.searchProduct(query).awaitResponse()
            if (response.isSuccessful) {
                response.body()?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserAllData(): UserAllData? {
        return try {
            val response = dummyService.getUserAllData().awaitResponse()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userProfileData: UserProfileData): Boolean {
        val response = dummyService.updateUserInfo(MainActivity.userId!!.toLong(), userProfileData)
            .awaitResponse()
        return if (response.isSuccessful) {
            response.body() != null
        } else false
    }

    fun getProductsOfCategory(categoryName: String?, callback: CallBack<MutableList<Product>>) {
        dummyService.getProductsOfCategory(categoryName!!).enqueue(object : Callback<ProductResponse>{
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    callback.onSuccess(body.products.toMutableList())
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                callback.onFailure()
            }

        })
    }

    suspend fun addProductToCart(cartToBeAdded: CartToBeAdded): AddedToBeCartResponse? {
        return try {
            val response = dummyService.addNewCart(cartToBeAdded).awaitResponse()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    interface CallBack<T> {
        fun onSuccess(data: T)
        fun onFailure()
    }

}

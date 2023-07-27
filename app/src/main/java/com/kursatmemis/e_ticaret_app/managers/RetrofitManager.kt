package com.kursatmemis.e_ticaret_app.managers

import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import com.kursatmemis.e_ticaret_app.models.UpdatedUser
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

    val dummyService = ApiClient.getClient().create(DummyService::class.java)

    fun login(user: User, callback: CallBack<UserData>) {

        dummyService.login(user).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                val userData = response.body()
                if (userData != null) {
                    callback.onSuccess(userData)
                } else {
                    callback.onFailure("Error! We couldn't find your account!")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                callback.onFailure("${t.message}")
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
                    callback.onFailure("Error!")
                }
            }

            override fun onFailure(call: Call<GetAllUserResponse>, t: Throwable) {
                callback.onFailure("${t.message}")
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

    suspend fun getProductsInCart(dataSource: MutableList<Any>, updateAdapter: ArrayAdapter<Any>) {
        try {
            val response =
                dummyService.getCartOfUser(MainActivity.userId!!.toLong()).awaitResponse()
            if (response.isSuccessful) {
                val products = response.body()?.carts?.get(0)?.products
                val productsInCart = mutableListOf<ProductInCart>()
                if (products != null) {
                    for (item in products) {
                        dummyService.getSingleProduct(item.id!!).enqueue(object : Callback<Product>{
                            override fun onResponse(
                                call: Call<Product>,
                                response: Response<Product>
                            ) {
                                val body = response.body()
                                if (body != null) {
                                    item.img = body.thumbnail
                                    dataSource.add(item)
                                    updateAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<Product>, t: Throwable) {

                            }

                        })
                    }
                }

            } else {

            }
        } catch (e: Exception) {

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
        dummyService.getProductsOfCategory(categoryName!!)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    val body = response.body()
                    if (body != null) {
                        callback.onSuccess(body.products.toMutableList())
                    } else {
                        callback.onFailure("Error!")
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    callback.onFailure("${t.message}")
                }

            })
    }

    fun addProductToCart(cartToBeAdded: CartToBeAdded, callback: CallBack<Boolean>) {

        dummyService.addNewCart(cartToBeAdded)
            .enqueue(object : Callback<AddedToBeCartResponse> {
                override fun onResponse(
                    call: Call<AddedToBeCartResponse>,
                    response: Response<AddedToBeCartResponse>
                ) {
                    val body = response.body()
                    if (body != null) {
                        callback.onSuccess(true)
                    } else {
                        callback.onFailure("Error!")
                    }
                }

                override fun onFailure(call: Call<AddedToBeCartResponse>, t: Throwable) {
                    callback.onFailure("${t.message}")
                }

            })
    }

    fun updateUser(updatedUser: UpdatedUser, callback: CallBack<UserAllData>) {
        dummyService.updateUser(MainActivity.userId!!.toLong(), updatedUser)
            .enqueue(object : Callback<UserAllData> {
                override fun onResponse(call: Call<UserAllData>, response: Response<UserAllData>) {
                    val userAllData = response.body()
                    if (userAllData != null) {
                        callback.onSuccess(userAllData)
                    } else {
                        callback.onFailure("Error!")
                    }

                }

                override fun onFailure(call: Call<UserAllData>, t: Throwable) {
                    callback.onFailure("${t.message}")
                }

            })
    }

    interface CallBack<T> {
        fun onSuccess(data: T)
        fun onFailure(errorMessage: String)
    }

}

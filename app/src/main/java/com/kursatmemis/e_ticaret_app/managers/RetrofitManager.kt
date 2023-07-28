package com.kursatmemis.e_ticaret_app.managers

import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.CartOfUserResponse
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.HelperData
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

object RetrofitManager {

    val dummyService: DummyService = ApiClient.getClient().create(DummyService::class.java)

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

    fun getAllProducts(callback: CallBack<List<Product>>) {

        dummyService.getAllProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                val products = response.body()?.products
                if (products != null) {
                    callback.onSuccess(products)
                } else {
                    callback.onSuccess(emptyList())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                callback.onFailure(t.message.toString())
            }
        })
    }

    fun getProductsInCart(callback: CallBack<List<ProductInCart>>) {
        dummyService.getCartOfUser(MainActivity.userId.toLong())
            .enqueue(object : Callback<CartOfUserResponse> {
                override fun onResponse(
                    call: Call<CartOfUserResponse>,
                    response: Response<CartOfUserResponse>
                ) {
                    val carts = response.body()?.carts
                    if (carts?.isNotEmpty() == true) {
                        val products = carts[0].products
                        if (products != null) {
                            for (item in products) {
                                getSingleProduct(item.id, object : CallBack<HelperData> {
                                    override fun onSuccess(data: HelperData) {
                                        item.img = data.img
                                        item.desc = data.desc
                                    }

                                    override fun onFailure(errorMessage: String) {
                                        // Bir şey yapmaya gerek yok.
                                    }
                                })
                            }
                            callback.onSuccess(products)
                        } else {
                            callback.onFailure("Products are null.")
                        }
                    }
                    else {
                        callback.onSuccess(emptyList())
                    }
                }

                override fun onFailure(call: Call<CartOfUserResponse>, t: Throwable) {
                    callback.onFailure(t.message.toString())
                }

            })
    }

    private fun getSingleProduct(id: Long?, callback: CallBack<HelperData>) {
        dummyService.getSingleProduct(id!!).enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                val body = response.body()
                if (body != null) {
                    val helperData = HelperData(body.thumbnail, body.description)
                    callback.onSuccess(helperData)
                } else {
                    callback.onFailure("Product is null.")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                callback.onFailure(t.message.toString())
            }

        })
    }

    fun getCategoryNames(callback: CallBack<List<String>>) {
        dummyService.getCategoryNames().enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                val categoryNames = response.body()
                if (categoryNames != null) {
                    callback.onSuccess(categoryNames)
                } else {
                    callback.onSuccess(emptyList())
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                callback.onFailure(t.message.toString())
            }

        })
    }

    fun searchProduct(query: String, callback: CallBack<List<Product>>) {
        dummyService.searchProduct(query).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                val products = response.body()?.products
                if (products != null) {
                    callback.onSuccess(products)
                } else {
                    callback.onSuccess(emptyList())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                callback.onFailure(t.message.toString())
            }

        })
    }

    fun getUserAllData(callback: CallBack<UserAllData?>) {
        dummyService.getUserAllData().enqueue(object : Callback<UserAllData> {
            override fun onResponse(call: Call<UserAllData>, response: Response<UserAllData>) {
                val userAllData = response.body()
                if (userAllData != null) {
                    callback.onSuccess(userAllData)
                } else {
                    callback.onSuccess(null)
                }
            }

            override fun onFailure(call: Call<UserAllData>, t: Throwable) {
                callback.onFailure(t.message.toString())
            }
        })
    }

    fun updateUserProfile(userProfileData: UserProfileData, callback: CallBack<Boolean>) {
        dummyService.updateUserInfo(MainActivity.userId!!.toLong(), userProfileData)
            .enqueue(object : Callback<UserAllData> {
                override fun onResponse(call: Call<UserAllData>, response: Response<UserAllData>) {
                    val userAllData = response.body()
                    if (userAllData != null) {
                        callback.onSuccess(true)
                    } else {
                        callback.onSuccess(false)
                    }
                }

                override fun onFailure(call: Call<UserAllData>, t: Throwable) {
                    callback.onFailure(t.message.toString())
                }

            })
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
        dummyService.updateUser(MainActivity.userId.toLong(), updatedUser)
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

}

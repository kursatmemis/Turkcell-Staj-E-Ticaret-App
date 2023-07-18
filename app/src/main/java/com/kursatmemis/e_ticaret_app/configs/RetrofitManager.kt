package com.kursatmemis.e_ticaret_app.configs
import android.util.Log
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductOfCart
import com.kursatmemis.e_ticaret_app.models.UserAllData
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import com.kursatmemis.e_ticaret_app.services.DummyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse

object RetrofitManager {
    private val dummyService = ApiClient.getClient().create(DummyService::class.java)

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

    suspend fun getCartOfUser(): List<ProductOfCart> {
        return try {
            val response = dummyService.getCartOfUser(MainActivity.userId).awaitResponse()
            if (response.isSuccessful) {
                response.body()?.carts?.get(0)?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategoryNames() : List<String> {
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
        val response = dummyService.updateUserInfo(MainActivity.userId, userProfileData)
            .awaitResponse()
        return if (response.isSuccessful) {
            response.body() != null
        } else false
    }
}


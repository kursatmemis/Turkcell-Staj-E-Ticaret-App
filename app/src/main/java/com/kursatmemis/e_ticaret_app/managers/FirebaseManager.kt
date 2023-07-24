package com.kursatmemis.e_ticaret_app.managers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    val database = Firebase.database.reference
    val auth = Firebase.auth

    suspend fun getProfileData(): UserProfileData? {
        return try {
            val snapshot =
                database.child("profile").child(MainActivity.userId.toString()).get().await()
            snapshot.getValue(UserProfileData::class.java)
        } catch (error: Exception) {
            null
        }
    }

    suspend fun getProductsInCart(): MutableList<ProductInCart> {
        val productsInCart = mutableListOf<ProductInCart>()

        return try {
            val snapshot =
                database.child("cart").child(MainActivity.userId.toString()).child("productId")
                    .get().await()

            for (item in snapshot.children) {
                val product = item.getValue(ProductInCart::class.java)
                if (product != null) {
                    productsInCart.add(product)
                }
            }
            productsInCart
        } catch (error: Exception) {
            productsInCart
        }

    }

    fun removeProductInCart(productId: String) {
        database.child("cart").child(MainActivity.userId.toString())
            .child("productId").child(productId).removeValue()
    }

    fun addProductToCart(product: Product) {
        database.child("cart").child(MainActivity.userId!!).child("productId")
            .child(product.id.toString()).setValue(product)
    }

    fun auth(email: String, password: String, callback: CallBack) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess()
                } else {
                    callback.onFailure(task.exception?.message!!)
                }
            }
    }

    fun createUser(email: String, password: String, callback: FirebaseManager.CallBack) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    callback.onSuccess()

                } else {
                    // If sign in fails, display a message to the user.
                    callback.onFailure(task.exception?.message!!)
                }

            }
    }

    fun saveData(ref: DatabaseReference, value: String) {
        ref.setValue(value)
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    interface CallBack {
        fun onSuccess()

        fun onFailure(exceptionMsg: String)
    }

}

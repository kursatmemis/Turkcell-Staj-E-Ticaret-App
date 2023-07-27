package com.kursatmemis.e_ticaret_app.managers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.activities.UserComment
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

    fun removeProductFromCart(productId: String) {
        database.child("cart").child(MainActivity.userId.toString())
            .child("productId").child(productId).removeValue()
    }

    fun addProductToCart(product: ProductInCart) {

        val ref = database.child("cart").child(MainActivity.userId!!).child("productId")
            .child(product.id.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Eğer product.id daha önce kaydedilmişse, quantity alanını arttırıp tekrar kaydedelim.
                    val existingProduct = snapshot.getValue(ProductInCart::class.java)
                    if (existingProduct != null) {
                        val newQuantity = existingProduct.quantity!! + 1
                        ref.child("quantity").setValue(newQuantity)
                        ref.child("total").setValue(existingProduct.price!! * 2)

                    }
                } else {
                    ref.setValue(product)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun calculateDiscountedPrice(price: Long, discountPercentage: Double): Double {
        return price - price * (discountPercentage / 100.0)
    }

    fun login(
        email: String,
        password: String,
        callback: CallBack<Any?>,
        isToSendVerification: Boolean
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (isToSendVerification) {
                        if (auth.currentUser!!.isEmailVerified) {
                            callback.onFailure("Your account is already verified.")
                        } else {
                            auth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    callback.onSuccess(null)
                                } else {
                                    callback.onFailure(it.exception?.message.toString())
                                }
                            }
                        }
                    } else if (auth.currentUser!!.isEmailVerified) {
                        callback.onSuccess(null)
                    } else {
                        callback.onFailure("You didn't verify your email address yet.")
                    }

                } else {
                    callback.onFailure(task.exception?.message!!)
                }
            }
    }

    fun createUser(email: String, password: String, callback: FirebaseManager.CallBack<Any?>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    auth.setLanguageCode("tr")
                    auth.currentUser!!.sendEmailVerification()
                    callback.onSuccess(null)

                } else {
                    // If sign in fails, display a message to the user.
                    callback.onFailure(task.exception?.message!!)
                }

            }
    }

    fun <T> saveData(ref: DatabaseReference, value: T) {
        ref.setValue(value)
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun updateUser(newEmail: String, newPassword: String, callback: CallBack<Any?>) {


        auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.onSuccess(null)
                    } else {
                        callback.onFailure(task.exception?.message!!)
                    }
                }
            } else {
                callback.onFailure(task.exception?.message!!)
            }
        }
    }

    fun resetPassword(email: String, callback: CallBack<Any?>) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback.onSuccess(null)
            } else {
                callback.onFailure(it.exception?.message.toString())
            }
        }
    }

    fun getComments(productId: Long, callback: CallBack<MutableList<UserComment>>) {
        val ref = database.child("comment").child("productId").child(productId.toString())
            .child("userId")
            .orderByChild("time")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCommentList = mutableListOf<UserComment>()
                for (item in snapshot.children) {
                    val y = item.getValue<UserComment>()
                    userCommentList.add(y!!)
                }

                callback.onSuccess(userCommentList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }


    interface CallBack<T> {
        fun onSuccess(data: T)

        fun onFailure(errorMessage: String)
    }

}

package com.kursatmemis.e_ticaret_app.managers

import android.util.Log
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
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.kursatmemis.e_ticaret_app.models.UserProfileData

object FirebaseManager {
    val database = Firebase.database.reference
    val auth = Firebase.auth

    fun getProfileData(callback: CallBack<UserProfileData>) {
        database.child("profile").child(MainActivity.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(UserProfileData::class.java)
                    if (data != null) {
                        callback.onSuccess(data)
                    } else {
                        callback.onFailure("Data is null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
    }

    fun getProductsInCart(callback: CallBack<MutableList<ProductInCart>>) {
        val productsInCart = mutableListOf<ProductInCart>()
        database.child("cart").child(MainActivity.userId).child("productId")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            val product = item.getValue(ProductInCart::class.java)
                            if (product != null) {
                                productsInCart.add(product)
                            }
                        }
                        callback.onSuccess(productsInCart)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.message)
                    }

                }
            )
    }

    fun removeProductFromCart(productId: String) {
        database.child("cart").child(MainActivity.userId)
            .child("productId").child(productId).removeValue()
    }

    fun addProductToCart(product: ProductInCart, callback: CallBack<Any>) {
        val ref = database.child("cart").child(MainActivity.userId).child("productId")
            .child(product.id.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Eğer product.id daha önce kaydedilmişse, quantity alanını arttırıp tekrar kaydeder.
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
                callback.onFailure(error.message)
            }

        })
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
                        // Email doğrulamak için.
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

    fun createUser(email: String, password: String, callback: CallBack<Any?>) {
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
                    val userComment = item.getValue<UserComment>()
                    Log.w("mKm - sss", userComment.toString())
                    if (userComment != null) {
                        userCommentList.add(userComment)
                    }
                }
                Log.w("mKm - sss-x", userCommentList.toString())
                callback.onSuccess(userCommentList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }


    fun addComment(productId: String, userId: String, userComment: UserComment) {
        database.child("comment").child("productId")
            .child(productId)
            .child("userId").child(userId).setValue(userComment)

    }


    fun <T> saveData(ref: DatabaseReference, value: T) {
        ref.setValue(value)
    }

    fun getUserComments(productId: Long, userId: String, callback: CallBack<MutableList<String>>) {
        database.child("comment").child("productId")
            .child(productId.toString())
            .child("userId").child(userId.toString()).child("comments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<String>()
                    val x = snapshot.value as? List<String>
                    if (x != null) {
                        for (element in x) {
                            comments.add(element)
                        }
                    }
                    callback.onSuccess(comments)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun deleteComment(productId: String, userId: String, comment: String) {
        database.child("comment").child("productId").child(productId).child("userId")
            .child(userId).child("comments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    val y = snapshot.value as? List<String>
                    if (y != null) {
                        for (element in y) {
                            list.add(element)
                        }
                    }

                    list.remove(comment)
                    val ref = database.child("comment").child("productId").child(productId)
                        .child("userId")
                        .child(userId).child("comments").setValue(list)

                    Log.w("asd", "geldi")
                }


                override fun onCancelled(error: DatabaseError) {

                }


            })

    }

}

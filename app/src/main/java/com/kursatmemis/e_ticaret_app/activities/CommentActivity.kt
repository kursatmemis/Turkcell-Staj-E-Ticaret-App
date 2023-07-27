package com.kursatmemis.e_ticaret_app.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.javafaker.Faker
import com.kursatmemis.e_ticaret_app.adapters.CommentAdapter
import com.kursatmemis.e_ticaret_app.databinding.ActivityCommentBinding
import com.kursatmemis.e_ticaret_app.databinding.AddCommentDialogBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class CommentActivity : BaseActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var adapter: CommentAdapter
    private var userComments = mutableListOf<UserComment>()
    private var productId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()

        productId = intent.getLongExtra("productId", -1)
        generateRandomComments(10, productId)
        getCommentsFromFirebase(productId)

        binding.addCommentButton.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val bindingDialog = AddCommentDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this@CommentActivity)
        builder.setView(bindingDialog.root)
        builder.setTitle("Submit Comment/Review")
        bindingDialog.btnSubmit.setOnClickListener {
            showProgressBar(bindingDialog.progressBar)
            val comment = bindingDialog.editTextReview.text.toString()
            if (comment.trim().isEmpty()) {
                Toast.makeText(
                    this@CommentActivity,
                    "Please fill in the field.",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar(bindingDialog.progressBar)
            } else {
                saveReviewToFirebase(comment)
                hideProgressBar(bindingDialog.progressBar)
            }
        }
        builder.show()
    }

    private fun saveReviewToFirebase(comment: String) {
        if (MainActivity.isServiceLogin) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val userAllData = RetrofitManager.getUserAllData()
                val userId = userAllData?.id
                val name = userAllData?.firstName
                val userComment = UserComment(userId.toString(), name, comment)
                val ref =
                    FirebaseManager.database.child("comment").child("productId").child(productId.toString())
                        .child("userId").child(userId.toString())

                FirebaseManager.saveData(ref, userComment)
                getCommentsFromFirebase(productId)
            }
        } else {
            val currentUser = FirebaseManager.auth.currentUser!!
            val userId = currentUser.uid
            var name = currentUser.displayName
            if (name?.isEmpty() == true || name == null) {
                name = currentUser.email?.substring(0, currentUser.email?.indexOf("@")!!)
            }
            val userComment = UserComment(userId, name, comment)
            val ref =
                FirebaseManager.database.child("comment").child("productId").child(productId.toString())
                    .child("userId").child(userId)

            FirebaseManager.saveData(ref, userComment)
            getCommentsFromFirebase(productId)
        }

    }

    private fun setAdapter() {
        adapter = CommentAdapter(this@CommentActivity, userComments)
        binding.commentListView.adapter = adapter
    }

    private fun getCommentsFromFirebase(productId: Long): Any {
        return FirebaseManager.getComments(
            productId,
            object : FirebaseManager.CallBack<MutableList<UserComment>> {

                override fun onSuccess(data: MutableList<UserComment>) {
                    updateAdapter(data as MutableList<Any>, adapter as ArrayAdapter<Any>)
                }


                override fun onFailure(errorMessage: String) {
                    showFancyToast(errorMessage, FancyToast.WARNING)
                }
            })
    }

    private fun generateRandomComments(count: Int, productId: Long) {
        val randomUsers = generateRandomUser(count)
        Log.w("mKm-comment", randomUsers.toString())

        for (item in randomUsers) {
            val ref = FirebaseManager.database.child("comment").child("productId").child(productId.toString())
                .child("userId")
                .child(item.id.toString())

            FirebaseManager.saveData(ref, item)
        }
    }

    private fun generateRandomUser(count: Int): MutableList<UserComment> {
        val faker = Faker()
        val userComments = mutableListOf<UserComment>()

        for (i in 1..count) {
            val id = i.toLong() * -1
            val name = faker.name().firstName()
            val comment = faker.lorem().sentence()
            val userComment = UserComment(id.toString(), name, comment)
            userComments.add(userComment)
        }

        return userComments
    }

    open fun updateAdapter(dataSource: MutableList<Any>, adapter: ArrayAdapter<Any>) {
        Log.w("mKm - update", dataSource.toString())
        adapter.clear()
        Log.w("mKm - update", dataSource.toString())
        dataSource.reverse()
        adapter.addAll(dataSource)
        adapter.notifyDataSetChanged()
    }
}

data class UserComment(
    val id: String? = "",
    val name: String? = "",
    val comment: String? = "",
    val time: String? = Calendar.getInstance().timeInMillis.toString()
)
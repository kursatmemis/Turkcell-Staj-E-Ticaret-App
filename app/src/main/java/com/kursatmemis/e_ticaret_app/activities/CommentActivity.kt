package com.kursatmemis.e_ticaret_app.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.kursatmemis.e_ticaret_app.adapters.CommentAdapter
import com.kursatmemis.e_ticaret_app.databinding.ActivityCommentBinding
import com.kursatmemis.e_ticaret_app.databinding.AddCommentDialogBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.UserAllData
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Calendar

class CommentActivity : BaseActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var adapter: CommentAdapter
    private var dataSource = mutableListOf<CommentInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getLongExtra("productId", -1)
        setAdapter(productId)

        getCommentsFromFirebase(productId)

        binding.addCommentButton.setOnClickListener {
            showAlertDialog(productId, it)
        }
    }

    private fun showAlertDialog(productId: Long, view: View) {
        val bindingDialog = AddCommentDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this@CommentActivity)
        builder.setView(bindingDialog.root)
        builder.setTitle("Submit Comment/Review")

        val alertDialog = builder.create() // AlertDialog'u bir değişkende saklayın.

        bindingDialog.btnSubmit.setOnClickListener {
            val comment = bindingDialog.editTextReview.text.toString()
            if (comment.trim().isEmpty()) {
                showFancyToast("Please fill in the field.", FancyToast.WARNING)
            } else {
                saveReviewToFirebase(comment, productId)
                alertDialog.dismiss()
            }
            closeKeyboard(it)
        }

        alertDialog.show()
    }

    private fun saveReviewToFirebase(comment: String, productId: Long) {
        var userId: String
        var name: String?

        if (MainActivity.isServiceLogin) {
            // Service login ile kullanıcı girişi yapılmışsa
            RetrofitManager.getUserAllData(object : CallBack<UserAllData?> {
                override fun onSuccess(data: UserAllData?) {
                    val userAllData = data
                    userId = userAllData?.id.toString()
                    name = userAllData?.firstName

                    addCommentToFirebase(productId.toString(), userId, name, comment)
                }

                override fun onFailure(errorMessage: String) {
                    showFancyToast(errorMessage, FancyToast.ERROR)
                }
            })
        } else {
            // Firebase authentication kullanarak giriş yapılmışsa
            val currentUser = FirebaseManager.auth.currentUser
            userId = currentUser?.uid ?: ""
            name = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: ""

            addCommentToFirebase(productId.toString(), userId, name, comment)
        }
    }

    private fun addCommentToFirebase(
        productId: String,
        userId: String,
        name: String?,
        comment: String
    ) {
        // Yorum ekleyecek olan kulanıcının bu ürün için daha önceden yapmış olduğu tüm yorum listesini alır,
        // yeni ekleyeceği yorumu bu listeye ekler ve güncellenmiş olan bu listeyi firebasde update eder.
        FirebaseManager.getUserComments(productId.toLong(), userId, object : CallBack<MutableList<String>> {
            override fun onSuccess(data: MutableList<String>) {
                data.add(comment)
                val userComment = UserComment(userId, name, data)
                FirebaseManager.addComment(productId, userId, userComment)
                getCommentsFromFirebase(productId.toLong())
            }

            override fun onFailure(errorMessage: String) {
                showFancyToast(errorMessage, FancyToast.ERROR)
            }
        })
    }

    private fun getCommentsFromFirebase(productId: Long) {
        // Firebase'e kaydedilmiş olan tüm yorumları alır.
        FirebaseManager.getComments(
            productId,
            object : CallBack<MutableList<UserComment>> {
                override fun onSuccess(data: MutableList<UserComment>) {
                    val comments = mutableListOf<CommentInfo>()
                    for (item in data) {
                        val name = item.name

                        for (comment in item.comments!!) {
                            val commentInfo = CommentInfo(item.id!!, name!!, comment)
                            comments.add(commentInfo)

                        }
                    }
                    dataSource = comments
                    updateAdapter(dataSource, adapter)
                }

                override fun onFailure(errorMessage: String) {
                    showFancyToast(errorMessage, FancyToast.WARNING)
                }
            })
    }

    fun updateAdapter(dataSource: MutableList<CommentInfo>, adapter: CommentAdapter) {
        adapter.clear()
        dataSource.reverse()
        adapter.addAll(dataSource)
        adapter.notifyDataSetChanged()
    }

    private fun setAdapter(productId: Long) {
        adapter = CommentAdapter(this@CommentActivity, dataSource, productId.toString())
        binding.commentListView.adapter = adapter
    }
}

data class UserComment(
    var id: String? = "",
    val name: String? = "",
    val comments: MutableList<String>? = emptyList<String>().toMutableList(),
    val time: String? = Calendar.getInstance().timeInMillis.toString()
)

data class CommentInfo(val uid: String, val name: String, val comment: String?)
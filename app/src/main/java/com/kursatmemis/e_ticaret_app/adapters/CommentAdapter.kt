package com.kursatmemis.e_ticaret_app.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.activities.CommentActivity
import com.kursatmemis.e_ticaret_app.activities.CommentInfo
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager

class CommentAdapter(context: Context, val userComments: MutableList<CommentInfo>, val productId:String) :
    ArrayAdapter<CommentInfo>(context, R.layout.comment_item, userComments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val commentItemView = layoutInflater.inflate(R.layout.comment_item, parent, false)
        val userFullNameTextView = commentItemView.findViewById<TextView>(R.id.userFullNameTextView)
        val commentTextView = commentItemView.findViewById<TextView>(R.id.commentTextView)
        val removeCommentImageView = commentItemView.findViewById<ImageView>(R.id.removeComment)

        Log.w("mKm-yy", userComments.toString())

        val userComment = userComments[position]
        userFullNameTextView.text = "${userComment.name}"
        commentTextView.text = "${userComment.comment}"

        if (MainActivity.userId == userComment.uid) {
            removeCommentImageView.visibility = View.VISIBLE
            removeCommentImageView.setOnClickListener {
                showAlertDialog(userComment, productId, position)
            }
        }

        return commentItemView
    }

    private fun showAlertDialog(userComment: CommentInfo, productId: String, position: Int) {

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("Are you sure to delete your comment?")
        alertDialog.setPositiveButton("Yes"
        ) { _, _ ->
            FirebaseManager.deleteComment(productId, userComment.uid, userComment.comment!!)
            userComments.remove(userComment)
            notifyDataSetChanged()
        }

        alertDialog.setNegativeButton("No"
        ) { _, _ ->

        }
        alertDialog.show()
    }

}
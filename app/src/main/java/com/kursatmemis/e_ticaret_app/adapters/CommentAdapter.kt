package com.kursatmemis.e_ticaret_app.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.activities.UserComment

class CommentAdapter(context: Context, val userComments: MutableList<UserComment>) :
    ArrayAdapter<UserComment>(context, R.layout.comment_item, userComments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val commentItemView = layoutInflater.inflate(R.layout.comment_item, parent, false)
        val userFullNameTextView = commentItemView.findViewById<TextView>(R.id.userFullNameTextView)
        val commentTextView = commentItemView.findViewById<TextView>(R.id.commentTextView)

        Log.w("mKm-yy", userComments.toString())

        val userComment = userComments[position]
        userFullNameTextView.text = "${userComment.name}"
        commentTextView.text = "${userComment.comment}"

        return commentItemView
    }

}
package com.kursatmemis.e_ticaret_app.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.models.Product


class ProductAdapter(context: Context, private val products: List<Product>) :
    ArrayAdapter<Product>(context, R.layout.product_item, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productItemView: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            productItemView = layoutInflater.inflate(R.layout.product_item, parent, false)
            val productImageImageView = productItemView.findViewById<ImageView>(R.id.productImageImageView)
            val productTitleTextView = productItemView.findViewById<TextView>(R.id.productTitleTextView)
            val productDescriptionTextView = productItemView.findViewById<TextView>(R.id.productDescriptionTextView)
            viewHolder = ViewHolder(productImageImageView, productTitleTextView, productDescriptionTextView)
            productItemView.tag = viewHolder
        } else {
            productItemView = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val imgLink = products[position].thumbnail
        val productTitle = products[position].title
        val productDescription = products[position].description

        Glide.with(context).load(imgLink).fitCenter().into(viewHolder.productImageImageView);
        viewHolder.productTitleTextView.text = productTitle
        viewHolder.productDescriptionTextView.text = productDescription

        return productItemView!!
    }

    data class ViewHolder(
        val productImageImageView: ImageView,
        val productTitleTextView: TextView,
        val productDescriptionTextView: TextView
    )

}
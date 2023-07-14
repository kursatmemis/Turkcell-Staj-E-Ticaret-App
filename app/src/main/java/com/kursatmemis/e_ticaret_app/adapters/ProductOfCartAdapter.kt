package com.kursatmemis.e_ticaret_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.models.ProductOfCart

class ProductOfCartAdapter(context: Context, private val productsOfCart: List<ProductOfCart>) :
    ArrayAdapter<ProductOfCart>(context, R.layout.product_of_cart_item, productsOfCart) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productOfCartItemView: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            productOfCartItemView = layoutInflater.inflate(R.layout.product_of_cart_item, parent, false)
            val productPriceTextView =
                productOfCartItemView.findViewById<TextView>(R.id.productPriceTextView)
            val productTitleTextView =
                productOfCartItemView.findViewById<TextView>(R.id.productTitleTextView)
            val totalPriceTextView =
                productOfCartItemView.findViewById<TextView>(R.id.totalPriceTextView)
            val discountPercentageTextView =
                productOfCartItemView.findViewById<TextView>(R.id.discountPercentageTextView)
            val discountedPriceTextView =
                productOfCartItemView.findViewById<TextView>(R.id.discountedPriceTextView)

            viewHolder = ViewHolder(
                productPriceTextView,
                productTitleTextView,
                totalPriceTextView,
                discountPercentageTextView,
                discountedPriceTextView
            )
            productOfCartItemView.tag = viewHolder
        } else {
            productOfCartItemView = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val productPrice = productsOfCart[position].price
        val productTitle = productsOfCart[position].title
        val totalPrice = productsOfCart[position].total
        val discountPercentage = productsOfCart[position].discountPercentage
        val discountedPrice = productsOfCart[position].discountedPrice

        viewHolder.productPriceTextView.text = "Product Price: $productPrice"
        viewHolder.productTitleTextView.text = "Product Title: $productTitle"
        viewHolder.totalPriceTextView.text = "Total Price: $totalPrice"
        viewHolder.discountPercentageTextView.text = "Discount Percentage: $discountPercentage"
        viewHolder.discountedPriceTextView.text = "Discounted Price: $discountedPrice"

        return productOfCartItemView!!
    }

    data class ViewHolder(
        val productPriceTextView: TextView,
        val productTitleTextView: TextView,
        val totalPriceTextView: TextView,
        val discountPercentageTextView: TextView,
        val discountedPriceTextView: TextView
    )

}
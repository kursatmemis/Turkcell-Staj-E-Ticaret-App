package com.kursatmemis.e_ticaret_app.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.models.ProductInCart

class CartAdapter(context: Context, private val productsInCart: MutableList<ProductInCart>) :
    ArrayAdapter<ProductInCart>(context, R.layout.cart_item, productsInCart) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val cartItem = layoutInflater.inflate(R.layout.cart_item, parent, false)
        val productImageImageView = cartItem.findViewById<ImageView>(R.id.productImageImageView)
        val productTitleTextView = cartItem.findViewById<TextView>(R.id.productTitleTextView)
        val productDescriptionTextView = cartItem.findViewById<TextView>(R.id.productDescriptionTextView)
        val removeProductFromCartImageView = cartItem.findViewById<ImageView>(R.id.removeProductFromCartImageView)
        val incrementButton = cartItem.findViewById<Button>(R.id.incrementButton)
        val countTextView = cartItem.findViewById<TextView>(R.id.countTextView)
        val decrementButton = cartItem.findViewById<Button>(R.id.decrementButton)
        val priceTextView = cartItem.findViewById<TextView>(R.id.priceTextView)
        val product = productsInCart[position]

        priceTextView.text = "${product.price}$"

        incrementButton.setOnClickListener {
            val count = countTextView.text.toString().toLong() + 1
            countTextView.text = count.toString()
            priceTextView.text = "${(product.price?.times(count))}$"

            decrementButton.isEnabled = count > 1
            if (!decrementButton.isEnabled) {
                decrementButton.setBackgroundColor(context.resources.getColor(R.color.gray))
            } else {
                decrementButton.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
        }

        decrementButton.setOnClickListener {
            val count = countTextView.text.toString().toLong() - 1
            countTextView.text = count.toString()
            priceTextView.text = "${(product.price?.times(count))}$"

            decrementButton.isEnabled = count > 1
            if (!decrementButton.isEnabled) {
                decrementButton.setBackgroundColor(context.resources.getColor(R.color.gray))
            } else {
                decrementButton.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
        }


        removeProductFromCartImageView.setOnClickListener{
            showAlertDialog(product)
        }

        Glide.with(context).load(product.img).fitCenter().into(productImageImageView)
        productTitleTextView.text = product.title
        productDescriptionTextView.text = product.desc

        return cartItem
    }

    private fun showAlertDialog(product: ProductInCart) {

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("Are you sure to remove the product from the cart?")
        alertDialog.setPositiveButton("Yes"
        ) { _, _ ->
            if (MainActivity.isServiceLogin) {
                // Serviste sepetten ürün silme aksiyonu yok.
                productsInCart.remove(product)
            } else {
                FirebaseManager.removeProductFromCart(product.id.toString())
                productsInCart.remove(product)
            }
            notifyDataSetChanged()

        }

        alertDialog.setNegativeButton("No"
        ) { _, _ ->

        }
        alertDialog.show()
    }

}
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
        var cartItem = convertView
        val viewHolder: ViewHolder

        if (cartItem == null) {
            val layoutInflater = LayoutInflater.from(context)
            cartItem = layoutInflater.inflate(R.layout.cart_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.productImageImageView = cartItem.findViewById(R.id.productImageImageView)
            viewHolder.productTitleTextView = cartItem.findViewById(R.id.productTitleTextView)
            viewHolder.productDescriptionTextView = cartItem.findViewById(R.id.productDescriptionTextView)
            viewHolder.removeProductFromCartImageView = cartItem.findViewById(R.id.removeProductFromCartImageView)
            viewHolder.incrementButton = cartItem.findViewById(R.id.incrementButton)
            viewHolder.countTextView = cartItem.findViewById(R.id.countTextView)
            viewHolder.decrementButton = cartItem.findViewById(R.id.decrementButton)
            viewHolder.priceTextView = cartItem.findViewById(R.id.priceTextView)
            cartItem.tag = viewHolder
        } else {
            viewHolder = cartItem.tag as ViewHolder
        }

        val product = productsInCart[position]

        viewHolder.countTextView.text = product.quantity.toString()
        viewHolder.priceTextView.text = "${product.price?.times(product.quantity!!)}$"

        viewHolder.decrementButton.isEnabled = product.quantity!! > 1
        viewHolder.decrementButton.setBackgroundColor(
            context.resources.getColor(
                if (product.quantity!! > 1) R.color.colorPrimary else R.color.gray
            )
        )

        viewHolder.incrementButton.setOnClickListener {
            product.quantity = product.quantity!! + 1
            viewHolder.countTextView.text = product.quantity.toString()
            viewHolder.priceTextView.text = "${product.price?.times(product.quantity!!)}$"

            viewHolder.decrementButton.isEnabled = product.quantity!! > 1
            viewHolder.decrementButton.setBackgroundColor(
                context.resources.getColor(
                    if (product.quantity!! > 1) R.color.colorPrimary else R.color.gray
                )
            )
        }

        viewHolder.decrementButton.setOnClickListener {
            product.quantity = product.quantity!! - 1
            viewHolder.countTextView.text = product.quantity.toString()
            viewHolder.priceTextView.text = "${product.price?.times(product.quantity!!)}$"

            viewHolder.decrementButton.isEnabled = product.quantity!! > 1
            viewHolder.decrementButton.setBackgroundColor(
                context.resources.getColor(
                    if (product.quantity!! > 1) R.color.colorPrimary else R.color.gray
                )
            )
        }

        viewHolder.removeProductFromCartImageView.setOnClickListener {
            showAlertDialog(product)
        }

        Glide.with(context).load(product.img).fitCenter().into(viewHolder.productImageImageView)
        viewHolder.productTitleTextView.text = product.title
        viewHolder.productDescriptionTextView.text = product.desc

        return cartItem!!
    }

    private class ViewHolder {
        lateinit var productImageImageView: ImageView
        lateinit var productTitleTextView: TextView
        lateinit var productDescriptionTextView: TextView
        lateinit var removeProductFromCartImageView: ImageView
        lateinit var incrementButton: Button
        lateinit var countTextView: TextView
        lateinit var decrementButton: Button
        lateinit var priceTextView: TextView
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